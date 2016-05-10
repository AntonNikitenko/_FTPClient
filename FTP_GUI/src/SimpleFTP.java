
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class SimpleFTP {

    /**
     * Create an instance of SimpleFTP.
     */
    public SimpleFTP() {

    }

    /**
     * Connects to an FTP server and logs in with the supplied username and
     * password.
     */
    public synchronized String connect(String host, int port, String user,
            String pass) throws IOException {

        String buffer = "";

        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        String response = readLine();
        buffer = buffer.concat(response + "\n");
        if (!response.startsWith("220")) {
            throw new IOException(
                    "SimpleFTP received an unknown response when connecting to the FTP server: "
                    + response);
        }

        buffer = buffer.concat(readAll());
        buffer = buffer.concat("USER " + user + "\n");
        sendLine("USER " + user);

        response = readLine();
        buffer = buffer.concat(response + "\n");
        if (!response.startsWith("331")) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                    + response);
        }
        buffer = buffer.concat(readAll());

        buffer = buffer.concat("PASS " + pass + "\n");
        sendLine("PASS " + pass);
        response = readLine();
        buffer = buffer.concat(response + "\n");
        if (!response.startsWith("230")) {
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                    + response);
        }
        buffer = buffer.concat(readAll());
        // Now logged in.
        return buffer;
    }

    /**
     * Disconnects from the FTP server.
     */
    public synchronized void disconnect() throws IOException {
        try {
            sendLine("QUIT");
        } finally {
            socket = null;
        }

    }

    public synchronized String pwdResponse() throws IOException {
        String buffer = "";
        String command = "PWD";
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        buffer = buffer.concat(readLine());
        return buffer.concat(readAll());
    }

    /**
     * Returns the working directory of the FTP server it is connected to.
     */
    public synchronized String pwd(String pwdResponse) throws IOException {
        String[] parts = pwdResponse.split("\n");
        if ((parts[1].startsWith("257 ")) || (parts[1].startsWith("250 "))) {
            int firstQuote = pwdResponse.indexOf('\"');
            int secondQuote = pwdResponse.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                return pwdResponse.substring(firstQuote + 1, secondQuote);
            }
        }
        return null;
    }

    public synchronized String mkd(String dir) throws IOException {
        String command = "MKD " + dir;
        String buffer = "";
        buffer = buffer.concat(command + "\n");
        sendLine(command);
        String response = readLine();
        buffer = buffer.concat(response + "\n");
        buffer = buffer.concat(readAll());
        return buffer;
    }

    public synchronized String rmd(String dir) throws IOException {
        String command = "RMD " + dir;
        String buffer = "";
        buffer = buffer.concat(command + "\n");
        sendLine(command);
        String response = readLine();
        buffer = buffer.concat(response + "\n");
        buffer = buffer.concat(readAll());
        return buffer;
    }

    public synchronized String dele(String file) throws IOException {
        String command = "DELE " + file;
        String buffer = "";
        buffer = buffer.concat(command + "\n");
        sendLine(command);
        String response = readLine();
        buffer = buffer.concat(response + "\n");
        buffer = buffer.concat(readAll());
        return buffer;
    }

    /**
     * Changes the working directory (like cd).
     */
    public synchronized String cwdResponse(String dir) throws IOException {
        String buffer = "";
        String command = "CWD " + dir;
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        String response = readLine();
        buffer = buffer.concat(response + "\n");
        buffer = buffer.concat(readAll());
        return buffer;
    }

    public synchronized boolean cwd(String cwdResponse) throws IOException {
        String[] parts = cwdResponse.split("\n");
        if ((parts[1].startsWith("257 ")) || (parts[1].startsWith("250 "))) {
            return true;
        }
        return false;
    }

    public synchronized String stor(InputStream inputStream, String filename)
            throws IOException {

        BufferedInputStream input = new BufferedInputStream(inputStream);
        String buf = "";
        String command = "PASV";
        sendLine(command);
        buf = buf.concat(command + "\n");
        String response = readLine();
        buf = buf.concat(response + "\n");
        buf = buf.concat(readAll());

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        command = "STOR " + filename;
        buf = buf.concat(command + "\n");
        sendLine(command);
        Socket dataSocket = new Socket(ip, port);
        response = readLine();
        buf = buf.concat(response + "\n");
        if (!response.startsWith("150 ")) {
            buf = buf.concat(readAll());
            return buf;
        }
        buf = buf.concat(readAll());

        BufferedOutputStream output = new BufferedOutputStream(dataSocket
                .getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();

        response = readLine();
        buf = buf.concat(response + "\n");
        buf = buf.concat(readAll());
        return buf;
    }

    public synchronized String LIST(String dir)
            throws IOException {
        String buffer = "";
        String command = "PASV";
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        String response = readLine();
        buffer = buffer.concat(response + "\n");
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }
        buffer = buffer.concat(readAll());

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        command = "LIST " + dir;
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        Socket dataSocket = new Socket(ip, port);
        response = readLine();
        buffer = buffer.concat(response + "\n");
        buffer = buffer.concat(readAll());

        BufferedInputStream dataInput = new BufferedInputStream(dataSocket
                .getInputStream());
        byte b[] = new byte[BLOCK_SIZE];
        int amount;
        StringBuffer sb = new StringBuffer();
        // Read the data into the StringBuffer
        while ((amount = dataInput.read(b)) > 0) {
            sb.append(new String(b, 0, amount));
        }

        String fileSystem = sb.toString();
        dataInput.close();

        response = readLine();
        if (response.startsWith("226")) {
            buffer = buffer.concat(response + "\n");
            buffer = buffer.concat(readAll());
        }
        buffer = buffer.concat("%" + fileSystem + "\n");

        return buffer;
    }

    /**
     * Sends a file to be stored on the FTP server. Returns true if the file
     * transfer was successful. The file is sent in passive mode to avoid NAT or
     * firewall problems at the client end.
     */
    public synchronized String retr(OutputStream outputStream, String filename)
            throws IOException {
        String buf = "";
        BufferedOutputStream outData = new BufferedOutputStream(outputStream);
        String command;
        command = "PASV";
        buf = buf.concat(command + "\n");
        sendLine("PASV");
        String response = readLine();
        buf = buf.concat(response + "\n");
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }
        buf = buf.concat(readAll());

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        command = "RETR " + filename;
        sendLine(command + "\n");
        Socket dataSocket = new Socket(ip, port);
        response = readLine();
        buf = buf.concat(response + "\n");
        if (!response.startsWith("150")) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }
        buf = buf.concat(readAll());
        BufferedInputStream dataInput = new BufferedInputStream(dataSocket
                .getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = dataInput.read(buffer)) != -1) {
            outData.write(buffer, 0, bytesRead);
        }
        outData.flush();
        outData.close();
        dataInput.close();

        response = readLine();
        buf = buf.concat(response + "\n");
        if (response.startsWith("226")) {
            buf = buf.concat(readAll());
            return buf;
        }
        return buf;
    }

    /**
     * Enter binary mode for sending binary files.
     */
    public synchronized String bin() throws IOException {
        String buffer = "";
        String command = "TYPE I";
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        buffer = buffer.concat(readLine() + "\n");
        return buffer.concat(readAll());
    }

    /**
     * Enter ASCII mode for sending text files. This is usually the default
     * mode. Make sure you use binary mode if you are sending images or other
     * binary data, as ASCII mode is likely to corrupt them.
     */
    public synchronized String ascii() throws IOException {
        String buffer = "";
        String command = "TYPE A";
        sendLine(command);
        buffer = buffer.concat(command + "\n");
        buffer = buffer.concat(readLine() + "\n");
        return buffer.concat(readAll());
    }

    /**
     * Sends a raw command to the FTP server.
     */
    private void sendLine(String line) throws IOException {
        if (socket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
        } catch (IOException e) {
            socket = null;
            throw e;
        }
    }

    private String readLine() throws IOException {
        String line = reader.readLine();
        return line;
    }

    String readAll() throws IOException {
        String response;
        String buffer = "";
        while ((reader.ready()) && ((response = readLine()) != null)) {
            buffer = buffer.concat(response + "\n");
        }
        return buffer;
    }

    private Socket socket = null;
    /**
     * The offset at which we resume a file transfer.
     */

    private BufferedReader reader = null;

    private BufferedWriter writer = null;

    /**
     * The socket output stream.
     */
    private PrintStream outputStream = null;
    /**
     * The socket input stream.
     */
    private BufferedReader inputStream = null;
    private static int BLOCK_SIZE = 4096;
}
