
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Максим Гусев
 */
public class Connection extends javax.swing.JFrame {

    String currentDir;
    String selectedItem;

    private void addToProtocol(String str) throws Exception {
        myTextPane_protocol.getStyledDocument().insertString(myTextPane_protocol.getStyledDocument().getLength(), str, null);
    }

    private String reloadPWD() throws Exception {
        String pwdResponse = FTP_GUI.ftp.pwdResponse();
        addToProtocol(pwdResponse + "\n");
        currentDir = FTP_GUI.ftp.pwd(pwdResponse);
        return pwdResponse;
    }

    private void reloadLIST() throws Exception {
        String[] parts = (FTP_GUI.ftp.LIST(currentDir)).split("%");
        addToProtocol(parts[0]);
        String real = parts[1].replaceAll(" {2,}", " ");
        String[] lines = real.split("\r\n");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentDir);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(root);

        for (int i = 0; i < lines.length - 1; i++) {
            String[] part = lines[i].split(" ");
            String path;
            if (part[0].startsWith("-")) {
                path = "/flags/file.png";
            } else {
                path = "/flags/folder.png";
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreePosition(part[8], path));
            root.add(node);
        }
        model.reload();
    }

    private void goToRoot() throws Exception {
        int endIndex = currentDir.lastIndexOf("/");
        if (currentDir.length() > 1) {
            String cwdResponse = FTP_GUI.ftp.cwdResponse(currentDir.substring(0, endIndex + 1));
            addToProtocol(cwdResponse + "\n");
            if (FTP_GUI.ftp.cwd(cwdResponse)) {
                reloadPWD();
                reloadLIST();
            }
        }
    }

    /**
     * Creates new form Connection
     */
    public Connection() throws Exception {
        initComponents();

        addToProtocol(FTP_GUI.start);

        connectionType.add(connectionBinary);
        connectionType.add(connectionASCII);
        connectionType.clearSelection();
        connectionType.setSelected(connectionBinary.getModel(), true);
        addToProtocol(FTP_GUI.ftp.bin());

        reloadPWD();

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                if (node == null) //Nothing is selected.  
                {
                    return;
                }

                Object nodeInfo = node.getUserObject();
                try {
                    if (node.isLeaf()) {
                        TreePosition pst = (TreePosition) nodeInfo;
                        selectedItem = pst.getName();
                    } else {
                        goToRoot();
                    }
                } catch (Exception ex) {
                    try {
                        goToRoot();
                    } catch (Exception ex1) {
                    }
                }

            }
        });

        tree.setCellRenderer(new CountryTreeCellRenderer());
        reloadLIST();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectionType = new javax.swing.ButtonGroup();
        connectionBinary = new javax.swing.JRadioButton();
        connectionASCII = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        myTextPane_protocol = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        myButton_MKD = new javax.swing.JButton();
        myButton_RMD = new javax.swing.JButton();
        myButton_DELE = new javax.swing.JButton();
        myButton_QUIT = new javax.swing.JButton();
        myTextField_path = new javax.swing.JTextField();
        myButton_STOR = new javax.swing.JButton();
        myButton_RETR = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        connectionBinary.setText("Binary mode");
        connectionBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionBinaryActionPerformed(evt);
            }
        });

        connectionASCII.setText("ASCII mode");
        connectionASCII.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionASCIIActionPerformed(evt);
            }
        });

        jLabel2.setText("Connection protocol");

        jScrollPane2.setViewportView(myTextPane_protocol);

        jButton1.setText("CWD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        myButton_MKD.setText("MKD");
        myButton_MKD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_MKDActionPerformed(evt);
            }
        });

        myButton_RMD.setText("RMD");
        myButton_RMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_RMDActionPerformed(evt);
            }
        });

        myButton_DELE.setText("DELE");
        myButton_DELE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_DELEActionPerformed(evt);
            }
        });

        myButton_QUIT.setText("QUIT");
        myButton_QUIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_QUITActionPerformed(evt);
            }
        });

        myButton_STOR.setText("STOR");
        myButton_STOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_STORActionPerformed(evt);
            }
        });

        myButton_RETR.setText("RETR");
        myButton_RETR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButton_RETRActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(tree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(myTextField_path)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(myButton_MKD)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(myButton_RMD, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(myButton_DELE)
                                .addContainerGap(69, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(connectionBinary)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(connectionASCII))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(myButton_STOR)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(myButton_RETR)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(myButton_QUIT)))
                                .addGap(0, 0, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(connectionBinary)
                            .addComponent(connectionASCII))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(myButton_RMD)
                            .addComponent(jButton1)
                            .addComponent(myButton_MKD)
                            .addComponent(myButton_DELE))
                        .addGap(7, 7, 7)
                        .addComponent(myTextField_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(myButton_STOR)
                            .addComponent(myButton_RETR)
                            .addComponent(myButton_QUIT))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectionBinaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionBinaryActionPerformed
        try {
            myTextPane_protocol.getStyledDocument().insertString(myTextPane_protocol.getStyledDocument().getLength(), FTP_GUI.ftp.bin(), null);
        } catch (BadLocationException | IOException ex) {
            System.out.println("binary");
        }

    }//GEN-LAST:event_connectionBinaryActionPerformed

    private void connectionASCIIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionASCIIActionPerformed
        try {
            myTextPane_protocol.getStyledDocument().insertString(myTextPane_protocol.getStyledDocument().getLength(), FTP_GUI.ftp.ascii(), null);
        } catch (BadLocationException | IOException ex) {
            System.out.println("ascii");
        }
    }//GEN-LAST:event_connectionASCIIActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {

            String cwdResponse = FTP_GUI.ftp.cwdResponse(selectedItem);

            addToProtocol(cwdResponse);
            if (FTP_GUI.ftp.cwd(cwdResponse)) {
                reloadPWD();
                reloadLIST();
            }
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void myButton_MKDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_MKDActionPerformed
        try {
            String mkdResponse = FTP_GUI.ftp.mkd(myTextField_path.getText());
            addToProtocol(mkdResponse);
            reloadLIST();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_myButton_MKDActionPerformed

    private void myButton_QUITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_QUITActionPerformed
        try {
            FTP_GUI.ftp.disconnect();
        } catch (IOException ex) {
        }
        Frame[] open;
        open = FTP_GUI.getFrames();
        System.exit(0);
    }//GEN-LAST:event_myButton_QUITActionPerformed

    private void myButton_RMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_RMDActionPerformed
        try {
            String rmdResponse = FTP_GUI.ftp.rmd(selectedItem);
            addToProtocol(rmdResponse);
            reloadLIST();
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_myButton_RMDActionPerformed

    private void myButton_DELEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_DELEActionPerformed
        try {
            String deleResponse = FTP_GUI.ftp.dele(selectedItem);
            addToProtocol(deleResponse);
            reloadLIST();
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_myButton_DELEActionPerformed

    private void myButton_STORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_STORActionPerformed
        try {
            JFrame parentFrame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String storResponse = FTP_GUI.ftp.stor(new FileInputStream(file), file.getName());
                addToProtocol(storResponse);
                reloadLIST();
            }
        } catch (Exception ex) {

        }

    }//GEN-LAST:event_myButton_STORActionPerformed

    private void myButton_RETRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButton_RETRActionPerformed
        try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selectedItem));
        JFrame parentFrame = new JFrame();
        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            
                File fileToSave = fileChooser.getSelectedFile();
                String retrResponse = FTP_GUI.ftp.retr(new FileOutputStream(fileToSave), selectedItem);

                addToProtocol(retrResponse);

        }
                    } catch (Exception ex) {
            }
    }//GEN-LAST:event_myButton_RETRActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {

        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                try {
                    new Connection().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton connectionASCII;
    private javax.swing.JRadioButton connectionBinary;
    private javax.swing.ButtonGroup connectionType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton myButton_DELE;
    private javax.swing.JButton myButton_MKD;
    private javax.swing.JButton myButton_QUIT;
    private javax.swing.JButton myButton_RETR;
    private javax.swing.JButton myButton_RMD;
    private javax.swing.JButton myButton_STOR;
    private javax.swing.JTextField myTextField_path;
    private javax.swing.JTextPane myTextPane_protocol;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
