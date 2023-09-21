package msalesdeployer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeployTasks {
    private static final SimpleDateFormat logsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
    public static String killTomcatProcess(File backendTomcatPath, String backendServicesName, String frontendServiceName) throws IOException, InterruptedException {
        StringBuilder userMessages = new StringBuilder();

        ProcessBuilder taskKillProcess = new ProcessBuilder("cmd.exe", "/c", "taskkill /im " + backendServicesName + " /f");
        Process taskKill = taskKillProcess.start();
        int exitCode = taskKill.waitFor();
        if (exitCode == 0) {
            userMessages.append(logsDateFormat.format(new Date())).append(" [Backend Tomcat process killed successfully]\n");
            boolean fileToDelete = new File(backendTomcatPath.toString()+"\\backendStarted.txt").delete();
            if(!fileToDelete){
                userMessages.append(logsDateFormat.format(new Date())).append(" [Failed to delete the file: ").append(backendTomcatPath).append("\\backendStarted.txt]");

            }
            taskKillProcess = new ProcessBuilder("cmd.exe", "/c", "taskkill /im " + frontendServiceName + " /f");
            taskKill = taskKillProcess.start();
            exitCode = taskKill.waitFor();
            if (exitCode == 0) {
                userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend Tomcat process killed successfully]\n");
            } else {
                userMessages.append(logsDateFormat.format(new Date())).append(" [Error while killing Frontend Tomcat process]\n");
            }
        } else {
            userMessages.append(logsDateFormat.format(new Date())).append(" [Error while killing Backend Tomcat process]\n");
        }
        return userMessages.toString();
    }

    public static String backupWars(File backendTomcatPath, File frontendTomcatPath, boolean deployCustomerApp, boolean deployIntegrationGateway, File backupFolderPath) throws IOException {
        StringBuilder userMessages = new StringBuilder();

        SimpleDateFormat folderDateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm a");
        String currentDate = folderDateFormat.format(new Date());

        File msalesWebWar = new File(backendTomcatPath, "\\msales-web\\msales-web.war");
        File msalesPortalWar = new File(frontendTomcatPath, "\\msalesportal\\msalesportal.war");

        // Backend WAR backup
        File backupFolderDir = new File(backupFolderPath,  currentDate);
        if (!backupFolderDir.exists()) {
            backupFolderDir.mkdir();
        }
        File backendBackupWar = new File(backupFolderDir, "msales-web.war");
        Files.copy(msalesWebWar.toPath(), backendBackupWar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Backend War backed up]\n");

        // Frontend WAR backup
        File frontendBackupWar = new File(backupFolderDir, "msalesportal.war");
        Files.copy(msalesPortalWar.toPath(), frontendBackupWar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend War backed up]\n");
        if (deployCustomerApp) {
            File customerAppWar = new File(backendTomcatPath, "\\CustomerApp\\CustomerApp.war");
            File customerAppBackupWar = new File(backupFolderDir, "CustomerApp.war");
            Files.copy(customerAppWar.toPath(), customerAppBackupWar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            userMessages.append(logsDateFormat.format(new Date())).append(" [Customer App War backed up]\n");
        }
        if (deployIntegrationGateway) {
            File integrationGatewayWar = new File(backendTomcatPath, "\\integration-gateway\\integration-gateway.war");
            File integrationGatewayBackupWar = new File(backupFolderDir, "integration-gateway.war");
            Files.copy(integrationGatewayWar.toPath(), integrationGatewayBackupWar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            userMessages.append(logsDateFormat.format(new Date())).append(" [Integration Gateway War backed up]\n");
        }

        return userMessages.toString();
    }

    public static String deleteExtractedFolders(File backendTomcatPath, File frontendTomcatPath, boolean deployCustomerApp, boolean deployIntegrationGateway) {
        StringBuilder userMessages = new StringBuilder();
        File msalesWebDir = new File(backendTomcatPath, "msales-web");
        deleteDirectory(msalesWebDir);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Backend Extracted Folders deleted]\n");

        File msalesPortalDir = new File(frontendTomcatPath, "msalesportal");
        deleteDirectory(msalesPortalDir);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend Extracted Folders deleted]\n");

        if (deployCustomerApp) {
            File customerAppDir = new File(backendTomcatPath, "CustomerApp");
            deleteDirectory(customerAppDir);
            userMessages.append(logsDateFormat.format(new Date())).append(" [CustomerApp Extracted Folders deleted]\n");

        }

        if (deployIntegrationGateway) {
            File integrationGatewayDir = new File(backendTomcatPath, "integration-gateway");
            deleteDirectory(integrationGatewayDir);
            userMessages.append(logsDateFormat.format(new Date())).append(" [IntegrationGateway Extracted Folders deleted]\n");

        }
        return userMessages.toString();
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            directory.delete();
        }
    }


    public static String copyWars(File backendTomcatPath, File frontendTomcatPath, boolean deployCustomerApp, boolean deployIntegrationGateway) throws IOException {
        StringBuilder userMessages = new StringBuilder();

        File msalesWebWar = new File("msales-web.war");
        File msalesPortalWar = new File("msalesportal.war");
        File msalesWebDir = new File(backendTomcatPath, "msales-web");
        if (!msalesWebDir.exists()) {
            msalesWebDir.mkdir();
        }

        File msalesWebDest = new File(msalesWebDir, "msales-web.war");
        Files.copy(msalesWebWar.toPath(), msalesWebDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Backend War copied]\n");

        File msalesPortalDir = new File(frontendTomcatPath, "msalesportal");
        if (!msalesPortalDir.exists()) {
            msalesPortalDir.mkdir();
        }
        File msalesPortalDest = new File(msalesPortalDir, "msalesportal.war");
        Files.copy(msalesPortalWar.toPath(), msalesPortalDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend War copied]\n");

        if (deployCustomerApp) {
            File customerAppDir = new File(backendTomcatPath, "CustomerApp");
            if (!customerAppDir.exists()) {
                customerAppDir.mkdir();
            }
            File customerAppWar = new File("CustomerApp.war");
            File customerAppDest = new File(customerAppDir, "CustomerApp.war");
            Files.copy(customerAppWar.toPath(), customerAppDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            userMessages.append(logsDateFormat.format(new Date())).append(" [CustomerApp War copied]\n");

        }
        if (deployIntegrationGateway) {
            File integrationGatewayDir = new File(backendTomcatPath, "integration-gateway");
            if (!integrationGatewayDir.exists()) {
                integrationGatewayDir.mkdir();
            }
            File integrationGatewayWar = new File("integration-gateway.war");
            File integrationGatewayDest = new File(integrationGatewayDir, "integration-gateway.war");
            Files.copy(integrationGatewayWar.toPath(), integrationGatewayDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            userMessages.append(logsDateFormat.format(new Date())).append(" [IntegrationGateway War copied]\n");

        }
        return userMessages.toString();

    }

    public static String startTomcatProcess(File backendTomcatPath, File frontendTomcatPath, String backendServicesName, String frontendServiceName) throws IOException, InterruptedException {
        StringBuilder userMessages = new StringBuilder();

        ProcessBuilder backendTomcatStartProcess = new ProcessBuilder(
                new File(backendTomcatPath, "bin\\" + backendServicesName).getAbsolutePath(),
                "start",
                "--StartMode=jvm"
        );
        Process backendTomcatStart = backendTomcatStartProcess.start();
        boolean isBackendStarted = backendTomcatStart.waitFor() == 0;
        if (isBackendStarted) {
            String backendFilePath = backendTomcatPath.toString()+"\\backendStarted.txt";
            waitForFileCreation(backendFilePath); // Wait for backend file creation
            userMessages.append(logsDateFormat.format(new Date())).append(" [Backend tomcat started] \n");
            ProcessBuilder frontendTomcatStartProcess = new ProcessBuilder(
                    new File(frontendTomcatPath, "bin\\" + frontendServiceName).getAbsolutePath(),
                    "start",
                    "--StartMode=jvm"
            );
            Process frontendTomcatStart = frontendTomcatStartProcess.start();

            boolean isFrontendStarted = frontendTomcatStart.waitFor() == 0;
            if (isFrontendStarted) {
                userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend tomcat started]\n");
            } else {
                userMessages.append(logsDateFormat.format(new Date())).append(" [Frontend tomcat not started]\n");
            }
        } else {
            userMessages.append(logsDateFormat.format(new Date())).append(" [Backend tomcat not started]\n");

        }
        return userMessages.toString();

    }
    private static void waitForFileCreation(String filePath) throws InterruptedException {
        File newFile = new File(filePath);

        while (!newFile.exists()) {
            Thread.sleep(2000); // Wait for 2 second before checking again
        }
    }

}

