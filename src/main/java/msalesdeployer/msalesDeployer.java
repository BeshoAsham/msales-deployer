package msalesdeployer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * <p>
 * Title: LicenseGenerator.java
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright(c) EME International, 2022
 * </p>
 *
 * <p>
 * Company: EME International
 * </p>
 *
 * @author <a href="mailto:magdy.elsisi@emeint.net">Magdy El-Sisi</a>
 * @version 1.0
 * @date 29/05/2022
 */
public class msalesDeployer {
    private JButton deployAndBackupBtn;
    private JPanel panel;
    private JButton deployBtn;
    private JTextArea userMessage;
    private JCheckBox integrationGatewayCheckBox;
    private JCheckBox customerAppCheckBox;
    private JCheckBox backendCheckBox;
    private JCheckBox frontendCheckBox;


    public msalesDeployer()
            throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JFrame frame = new JFrame("Msales Deployer");
        frame.setSize(600, 650);
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        String fileContent = MsalesDeployerImp.readFileContent(new File("application.properties"));
        Map<String, String> variables = MsalesDeployerImp.extractVariables(fileContent);
        deployAndBackupBtn.setText("Backup & Deploy");
        String backendTomcatPath = variables.get("backend_tomcat_path");
        String backendServicesName = variables.get("backend_services_name");
        String frontendTomcatPath = variables.get("frontend_tomcat_path");
        String frontendServicesName = variables.get("frontend_services_name");
        String backupFolderPath = variables.get("backup_folder_path");


        deployBtn.addActionListener(e -> {
            long startDate = new Date().getTime();
            userMessage.setText(""); // Clear the text area before starting
            boolean isCustomerAppSelected = customerAppCheckBox.isSelected();
            boolean isIntegrationGatewaySelected = integrationGatewayCheckBox.isSelected();
            SwingWorker<Void, String> deployWorker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() {
                    try {


                        // Kill Tomcat processes
                        String killTomcatProcessMsg = DeployTasks.killTomcatProcess(new File(backendTomcatPath), backendServicesName, frontendServicesName);
                        publish(killTomcatProcessMsg);

                        // Delete extracted folders
                        String deleteExtractedFoldersMsg = DeployTasks.deleteExtractedFolders(new File(backendTomcatPath), new File(frontendTomcatPath), isCustomerAppSelected, isIntegrationGatewaySelected);
                        publish(deleteExtractedFoldersMsg);

                        // Copy WAR files
                        String copyWarsMsg = DeployTasks.copyWars(new File(backendTomcatPath), new File(frontendTomcatPath), isCustomerAppSelected, isIntegrationGatewaySelected);
                        publish(copyWarsMsg);
                        publish("Backend and Frontend Tomcat are starting...\n");

                        // Start Tomcat processes
                        String startTomcatProcessMsg = DeployTasks.startTomcatProcess(new File(backendTomcatPath), new File(frontendTomcatPath), backendServicesName, frontendServicesName);
                        publish(startTomcatProcessMsg);

                        long endDate = new Date().getTime();
                        long deployedTime = endDate - startDate;
                        publish("Deploy Time: " + (deployedTime / 1000) + " Seconds");

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    // Update the text area with each message
                    for (String message : chunks) {
                        userMessage.append(message + "\n");
                    }
                }
            };


            deployWorker.execute();
        });

        deployAndBackupBtn.addActionListener(e -> {
            long startDate = new Date().getTime();
            userMessage.setText(""); // Clear the text area before starting
            boolean isCustomerAppSelected = customerAppCheckBox.isSelected();
            boolean isIntegrationGatewaySelected = integrationGatewayCheckBox.isSelected();
            SwingWorker<Void, String> deployWorker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {


                        // Backup WAR files
                        String backupWarsMsg = DeployTasks.backupWars(new File(backendTomcatPath), new File(frontendTomcatPath), isCustomerAppSelected, isIntegrationGatewaySelected, new File(backupFolderPath));
                        publish(backupWarsMsg);
                        // Kill Tomcat processes
                        String killTomcatProcessMsg = DeployTasks.killTomcatProcess(new File(backendTomcatPath), backendServicesName, frontendServicesName);
                        publish(killTomcatProcessMsg);

                        // Delete extracted folders
                        String deleteExtractedFoldersMsg = DeployTasks.deleteExtractedFolders(new File(backendTomcatPath), new File(frontendTomcatPath), isCustomerAppSelected, isIntegrationGatewaySelected);
                        publish(deleteExtractedFoldersMsg);

                        // Copy WAR files
                        String copyWarsMsg = DeployTasks.copyWars(new File(backendTomcatPath), new File(frontendTomcatPath), isCustomerAppSelected, isIntegrationGatewaySelected);
                        publish(copyWarsMsg);
                        publish("Backend and Frontend Tomcat are starting...");

                        // Start Tomcat processes
                        String startTomcatProcessMsg = DeployTasks.startTomcatProcess(new File(backendTomcatPath), new File(frontendTomcatPath), backendServicesName, frontendServicesName);
                        publish(startTomcatProcessMsg);

                        long endDate = new Date().getTime();
                        long deployedTime = endDate - startDate;
                        publish("Backup and Deploy Time: " + (deployedTime / 1000) + " Seconds");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String message : chunks) {
                        userMessage.append(message + "\n");
                    }
                }
            };

            deployWorker.execute();
        });

    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            new msalesDeployer();
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}