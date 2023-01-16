/*
 * Server for COMP3331 assignment 1
 *
 * Author: Max Xue
 * Date: 25-9-2022
 * */

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
// import java.util.concurrent.locks.ReadWriteLock;
// import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.*;

public class Server {

    // Server information
    private static ServerSocket serverSocket;
    private static Integer serverPort;

    private static HashMap<String, String> credentials = new HashMap<>(); // Holds all device credentials (name/password)
    private static HashMap<String, Date> deviceTimeoutDate = new HashMap<>(); // Holds the most recent date a device was set to be timed out
    private static HashMap<String, Integer> deviceConsecutiveLoginAttempts = new HashMap<>(); // Holds the current number of attempted logins
    private static Integer MAX_CONSECUTIVE_LOGIN_ATTEMPTS = -1; // Holds the maximum number of consecutive invalid login attempts for a device set by the argument given at server start up

    // * Status codes for protocol. Should be matched exactly in client
    // Auth request/response messages
    private static final String INVALID_DEVICE_NAME = "invalid device name";
    private static final String VALID_DEVICE_NAME = "valid device name";
    private static final String INVALID_PASSWORD = "invalid password";
    private static final String VALID_PASSWORD = "valid password";
    private static final String INVALID_PASSWORD_TIMEOUT = "invalid password timeout";
    private static final String DEVICE_TIMEOUT = "device currently timeout";

    // UED request/response messages
    private static final String UED_CLIENT_SEND_FILE = "ued client sending data file";
    private static final String UED_SERVER_RECEIVE_DATA_FILE_SUCCESS = "ued server receive data file success";
    private static final String UED_SERVER_RECEIVE_DATA_FILE_FAILURE = "ued server receive data file failure";
    private static final String UED_SERVER_RECEIVE_DATA_FILE_ALREADY_EXISTS = "ued server receive data file already exists";

    // SCS request/response messages
    private static final String SCS_CLIENT_COMPUTE_REQUEST = "scs client compute request";
    private static final String SCS_CLIENT_COMPUTE_REQUEST_SUM = "scs client compute request sum";
    private static final String SCS_CLIENT_COMPUTE_REQUEST_AVG = "scs client compute request avg";
    private static final String SCS_CLIENT_COMPUTE_REQUEST_MIN = "scs client compute request min";
    private static final String SCS_CLIENT_COMPUTE_REQUEST_MAX = "scs client compute request max";
    private static final String SCS_SERVER_COMPUTE_SUCCESS = "scs server compute success";
    private static final String SCS_SERVER_COMPUTE_FAILURE = "scs server compute failure";
    private static final String SCS_SERVER_COMPUTE_FAILURE_FILE_NOT_FOUND = "scs server compute failure file not found";

    // DTE request/response messages
    private static final String DTE_CLIENT_REQUEST = "dte client request";
    private static final String DTE_SERVER_SUCCESS = "dte client success";
    private static final String DTE_SERVER_FAILURE = "dte client failure";
    private static final String DTE_SERVER_FAILURE_FILE_NOT_FOUND = "dte client failure file not found";

    // AED request/response messages
    private static final String AED_CLIENT_REQUEST = "aed client request";
    private static final String AED_SERVER_NO_OTHER_DEVICES = "aed server no other devices";
    private static final String AED_SERVER_SUCCESS = "aed server success";
    
    // OUT request/response messages
    private static final String OUT_CLIENT_REQUEST = "out client request";

    // UVF request/response messages
    private static final String UVF_CLIENT_REQUEST = "uvf client request";
    private static final String UVF_SERVER_AUDIENCE_OFFLINE = "uvf server audience offline";
    private static final String UVF_SERVER_UNKNOWN_DEVICE = "uvf server unknown device";
    private static final String UVF_SERVER_AUDIENCE_ONLINE = "uvf server audience online";

    // Checks if a string is an integer
    // https://www.baeldung.com/java-check-string-number
    public static boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        if (strNum.contains(".")) { // This is just for formatting so the string number doesn't include a decimal
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
            if ((d % 1) == 0) { // https://stackoverflow.com/questions/9898512/how-to-test-if-a-double-is-an-integer
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    // Class for handling authentication
    private static class Authenticator {
        // Updates the most recent timeout instance
        private void updateTimeoutDate(String username, Date date, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
            try {
                Date currDate = new Date();
                long currDateLong = currDate.getTime();
                if (currDateLong - this.getTimeoutDate(username).getTime() < 10000) { // Doesn't update the timeout date if the device is timed out because that means the client was too late and the device is already timed out
                    dataOutputStream.writeUTF(DEVICE_TIMEOUT); // After sending the DEVICE_TIMEOUT status code, the socket should be closed by the client so this whole thread should close.
                    dataOutputStream.flush();
                } else {
                    deviceTimeoutDate.put(username, date);
                }
            } catch (IOException e) { 
                throw e;
            }
        }

        private Date getTimeoutDate(String username) {
            return deviceTimeoutDate.get(username);
        }

        // Updates number of consecutive invalid login attempts before timeout
        private void updateConsecutiveLoginAttempts(String username, Integer attempts, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
            try {
                Date currDate = new Date();
                long currDateLong = currDate.getTime();
                if (currDateLong - this.getTimeoutDate(username).getTime() < 10000) { // Doesn't update the consecutive login attempts if the device is timed out because that means the client was too late and the device is already timed out
                    dataOutputStream.writeUTF(DEVICE_TIMEOUT); // After sending the DEVICE_TIMEOUT status code, the socket should be closed by the client so this whole thread should close.
                    dataOutputStream.flush();
                } else {
                    deviceConsecutiveLoginAttempts.put(username, attempts);
                }
            } catch (IOException e) {
                throw e;
            }
        }

        private Integer getConsecutiveLoginAttempts(String username) {
            return deviceConsecutiveLoginAttempts.get(username);
        }

        private String authenticate(String clientID, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws EOFException, IOException {

            try {
                String deviceNameInput;
                while (true) {
                    deviceNameInput = (String) dataInputStream.readUTF();
                    // Checking valid deviceNameInput
                    if (credentials.keySet().contains(deviceNameInput)) {
                        break; // If valid username, break while loop
                    }
                    dataOutputStream.writeUTF(INVALID_DEVICE_NAME);
                    dataOutputStream.flush();
                }

                // Checking for existing timeouts
                Date currDate = new Date();
                long currDateLong = currDate.getTime();
                if (currDateLong - getTimeoutDate(deviceNameInput).getTime() < 10000) {
                    dataOutputStream.writeUTF(DEVICE_TIMEOUT);
                    dataOutputStream.flush();
                    return null; // Client should quit after DEVICE_TIMEOUT status code response so this thread should close since the TCP connection should be closed at this point
                }

                dataOutputStream.writeUTF(VALID_DEVICE_NAME);
                dataOutputStream.flush();

                // Checking valid password
                while (true) {
                    String password = (String) dataInputStream.readUTF();
                    if (credentials.get(deviceNameInput).equals(password)) {
                        // This function will logout the client if they try to update the consecutive login attempts when the device is currently timed out
                        updateConsecutiveLoginAttempts(deviceNameInput, 0, dataInputStream, dataOutputStream); 

                        dataOutputStream.writeUTF(VALID_PASSWORD);
                        dataOutputStream.flush();
                        return deviceNameInput;
                    }
                    // This function will logout the client if they try to update the consecutive login attempts when the device is currently timed out
                    updateConsecutiveLoginAttempts(deviceNameInput, getConsecutiveLoginAttempts(deviceNameInput)+1, dataInputStream, dataOutputStream);

                    // Timing out device if too many consecutive incorrect login attempts
                    if (getConsecutiveLoginAttempts(deviceNameInput) >= MAX_CONSECUTIVE_LOGIN_ATTEMPTS) {
                        // This function will logout the client if they try to update the consecutive login attempts when the device is currently timed out
                        updateConsecutiveLoginAttempts(deviceNameInput, 0, dataInputStream, dataOutputStream);
                        
                        // This function will logout the client if they try to update the device timeout date when the device is currently timed out
                        updateTimeoutDate(deviceNameInput, new Date(), dataInputStream, dataOutputStream);
                        
                        dataOutputStream.writeUTF(INVALID_PASSWORD_TIMEOUT);
                        dataOutputStream.flush();
                        return null;
                    }

                    dataOutputStream.writeUTF(INVALID_PASSWORD);
                    dataOutputStream.flush();
                }
            } catch (EOFException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static void UEDProcessing(String deviceName, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        System.out.println(deviceName + " issued UED command");
        try {
            // Getting file info for logging
            String edgeDeviceName = (String) dataInputStream.readUTF();
            String fileName = (String) dataInputStream.readUTF();
            String fileID = (String) dataInputStream.readUTF();
            String dataAmount = (String) dataInputStream.readUTF();

            // Getting file data
            String fileData = (String) dataInputStream.readUTF();

            // Creating and writing to file. If the file already exists, send back "file already exists" error response message and do nothing else
            File file = new File(fileName);
            if (file.exists()) {
                dataOutputStream.writeUTF(UED_SERVER_RECEIVE_DATA_FILE_ALREADY_EXISTS);
                dataOutputStream.flush();
                System.out.println("File " + fileName + " already exists on server");
                return;
            }
            file.createNewFile(); 
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileData);
            fileWriter.close();
            System.out.println("Data file " + fileName + " received and saved");
            dataOutputStream.writeUTF(UED_SERVER_RECEIVE_DATA_FILE_SUCCESS);
            dataOutputStream.flush();

            // Logging file info after creating file successfully
            String pattern = "dd MMMM yyyy HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String timestamp = simpleDateFormat.format(new Date());
            String logEntry = edgeDeviceName + "; " + timestamp + "; " + fileID + "; " + dataAmount;
            File logFile = new File("upload-log.txt");
            boolean isNewFile = logFile.createNewFile();
            FileWriter logFileWriter = new FileWriter(logFile, true);
            if (!isNewFile) {
                logFileWriter.write("\n");
            }
            logFileWriter.write(logEntry);
            logFileWriter.close();
            System.out.println("Data file " + fileName + " logged.");

        } catch (IOException fileError) {
            try {
                dataOutputStream.writeUTF(UED_SERVER_RECEIVE_DATA_FILE_FAILURE);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("Error when trying to send UED_SERVER_RECEIVE_DATA_FILE_FAILURE status code and exception message to client.");
                e.printStackTrace();
            }
            System.out.println("IO Error while saving file to server.");
            fileError.printStackTrace();
        }
    }

    public static void SCSProcessing(String requestedFile, String operation, String deviceName, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        System.out.println(deviceName + " issued SCS command");
        try {
            // Check if requested file exists
            File file = new File(requestedFile);
            if (!file.exists()) {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_FAILURE_FILE_NOT_FOUND);
                dataOutputStream.flush();
                System.out.println("File " + requestedFile + " not found on server.");
                return;
            }

            // Get data from file and turn it into integers and put it in array list
            Path filePath = Path.of(requestedFile);
            String fileData = Files.readString(filePath);
            String integerStrings[] = fileData.split("\\s+");
            ArrayList<Integer> data = new ArrayList<>();
            for (String num : integerStrings) {
                data.add(Integer.parseInt(num));
            }

            // Process data depending on requested computation and send result back to client
            if (operation.equals(SCS_CLIENT_COMPUTE_REQUEST_SUM)) {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_SUCCESS);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(Integer.toString(data.stream().mapToInt(Integer::intValue).sum()));
                dataOutputStream.flush();
                System.out.println("Compute SUM for file: " + requestedFile);
            } else if (operation.equals(SCS_CLIENT_COMPUTE_REQUEST_AVG)) {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_SUCCESS);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(Double.toString((data.stream().mapToDouble(Integer::doubleValue).sum()/integerStrings.length)));
                dataOutputStream.flush();
                System.out.println("Compute AVERAGE for file: " + requestedFile);
            }  else if (operation.equals(SCS_CLIENT_COMPUTE_REQUEST_MAX)) {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_SUCCESS);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(Integer.toString(Collections.max(data)));
                dataOutputStream.flush();
                System.out.println("Compute MAX for file: " + requestedFile);
            }  else if (operation.equals(SCS_CLIENT_COMPUTE_REQUEST_MIN)) {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_SUCCESS);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(Integer.toString(Collections.min(data)));
                dataOutputStream.flush();
                System.out.println("Compute MIN for file: " + requestedFile);
            } else {
                System.out.println("ERROR INVALID COMPUATION REQUEST MESSAGE");
            }

        } catch (IOException error) {
            try {
                dataOutputStream.writeUTF(SCS_SERVER_COMPUTE_FAILURE);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("Error when trying to send SCS_SERVER_COMPUTE_FAILURE status code and exception message to client.");
                e.printStackTrace();
            }
            System.out.println("Error while computing " + operation + " on file " + requestedFile + ".");
            error.printStackTrace();
        }
    }

    public static void DTEProcessing(String fileID, String requestedFile, String deviceName, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        System.out.println(deviceName + " issued DTE command");
        try {
            // Check if requested file exists
            File file = new File(requestedFile);
            if (!file.exists()) {
                dataOutputStream.writeUTF(DTE_SERVER_FAILURE_FILE_NOT_FOUND);
                dataOutputStream.flush();
                System.out.println("File " + requestedFile + " not found on server.");
                return;
            }

            // Get file info for logging
            BufferedReader fileDataReader = new BufferedReader(new FileReader(requestedFile));
            int dataAmount = 0;
            String line = fileDataReader.readLine();
            while (line != null) {
                dataAmount++;
                line = fileDataReader.readLine();
            }
            fileDataReader.close();

            // Delete file
            Path filePath = Path.of(requestedFile);
            Files.delete(filePath);
            dataOutputStream.writeUTF(DTE_SERVER_SUCCESS);
            dataOutputStream.flush();
            System.out.println("Data file " + requestedFile + " deleted.");

            // Log file deletion
            String pattern = "dd MMMM yyyy HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String timestamp = simpleDateFormat.format(new Date());
            String logEntry = deviceName + "; " + timestamp + "; " + fileID + "; " + dataAmount;
            File logFile = new File("deletion-log.txt");
            boolean isNewFile = logFile.createNewFile();
            FileWriter logFileWriter = new FileWriter(logFile, true);
            if (!isNewFile) {
                logFileWriter.write("\n");
            }
            logFileWriter.write(logEntry);
            logFileWriter.close();
            System.out.println("Data file " + requestedFile + " deletion logged.");


        } catch (NoSuchFileException x) {
            try {
                dataOutputStream.writeUTF(DTE_SERVER_FAILURE_FILE_NOT_FOUND);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("Error when trying to send DTE_SERVER_FAILURE status code and exception message to client.");
                e.printStackTrace();
            }
            System.out.println("Delete error: " + requestedFile + " was not found.");
        } catch (IOException x) {
            try {
                dataOutputStream.writeUTF(DTE_SERVER_FAILURE);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("Error when trying to send DTE_SERVER_FAILURE status code and exception message to client.");
                e.printStackTrace();
            }
            x.printStackTrace();
        }
    }

    // Remove a given device from the edge device log file
    public static void removeDevice(String deviceName) {
        if (deviceName == null) return;
        // Check if edge device log file exists
        File logFile = new File("edge-device-log.txt");
        if (!logFile.exists()) {
            System.out.println("Tried to update edge device log file due to " + deviceName + " leaving. \nFile not found.");
            return;
        }
        try {
            // Get all data in file
            Path edgeDeviceLogPath = Path.of("./edge-device-log.txt");
            String edgeDeviceLogData = Files.readString(edgeDeviceLogPath);
            String edgeDeviceLogStrings[] = edgeDeviceLogData.split("; |\n");

            // Reset file
            logFile.delete();
            File newLogFile = new File("edge-device-log.txt");
            FileWriter logFileWriter = new FileWriter(newLogFile, true);

            // Readd all device logs back into new file except device to be deleted. Update all sequence numbers
            int currSeqNum = 1;
            for (int i = 0; i < edgeDeviceLogStrings.length; i = i + 5) {
                if (edgeDeviceLogStrings[i+2].equals(deviceName)) {
                    continue;
                }
                if (currSeqNum != 1) {
                    logFileWriter.write("\n");
                }
                String logEntry = Integer.toString(currSeqNum) + "; " + edgeDeviceLogStrings[i+1] + "; " + edgeDeviceLogStrings[i+2] + "; " + edgeDeviceLogStrings[i+3] + "; " + edgeDeviceLogStrings[i+4];
                logFileWriter.write(logEntry);
                currSeqNum++;
            }

            logFileWriter.close();
        } catch (IOException e) {
            System.out.println("Could not update edge device log file.");
            e.printStackTrace();
        }
        System.out.println(deviceName + " logged out.");
    }

    public static void AEDProcessing(String deviceName, DataOutputStream dataOutputStream) {
        System.out.println(deviceName + " issued AED command");
        try {
            // Get data of all active devices from edge device log file
            Path edgeDeviceLogPath = Path.of("./edge-device-log.txt");
            String edgeDeviceLogData = Files.readString(edgeDeviceLogPath);
            String edgeDeviceLogStrings[] = edgeDeviceLogData.split("; |\n");
            String activeDevices = "";

            // Loop through data and get all edge devices and info except for requesting client device
            boolean firstLineAdded = false;
            for (int i = 0; i < edgeDeviceLogStrings.length; i = i + 5) {
                if (edgeDeviceLogStrings[i+2].equals(deviceName)) { // Skip over device data if the device has the same name as the requesting client device
                    continue;
                } 
                
                if (i != 0 && firstLineAdded) {
                    activeDevices = activeDevices + "\n"; // Adding a new line for all device info rows except for the first device info
                }

                activeDevices = activeDevices + edgeDeviceLogStrings[i+2] + "; " + edgeDeviceLogStrings[i+1] + "; " + edgeDeviceLogStrings[i+3] + "; " + edgeDeviceLogStrings[i+4];
                firstLineAdded = true;
            }

            // Sending back results
            if (activeDevices.equals("")) {
                dataOutputStream.writeUTF(AED_SERVER_NO_OTHER_DEVICES);
                dataOutputStream.flush();
                System.out.println("No other devices.");
            } else {
                dataOutputStream.writeUTF(AED_SERVER_SUCCESS);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(activeDevices);
                dataOutputStream.flush();
                System.out.println("AED success.");
                System.out.println("Message sent: " + activeDevices);
            }

        } catch (IOException e) {
            System.out.println("Could not update edge device log file.");
            e.printStackTrace();
        }
    }

    public static void UVFProcessing(String deviceName, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        System.out.println(deviceName + " issued UVF command");
        try {
            // Getting the name of the audience device
            String audienceDevice = (String) dataInputStream.readUTF();

            // Checking if the audience device is a valid device
            if (!credentials.keySet().contains(audienceDevice)) {
                dataOutputStream.writeUTF(UVF_SERVER_UNKNOWN_DEVICE);
                dataOutputStream.flush();
                System.out.println("Unknown audience device " + audienceDevice + ".");
                return;
            }
            
            // Getting the data of the audience device
            Path edgeDeviceLogPath = Path.of("./edge-device-log.txt");
            String edgeDeviceLogData = Files.readString(edgeDeviceLogPath);
            String edgeDeviceLogStrings[] = edgeDeviceLogData.split("; |\n");
            boolean isAudienceDeviceActive = false; // If the audience device is online, this will be set to true
            String audienceIP = "";
            String audiencePort = "";
            for (int i = 0; i < edgeDeviceLogStrings.length; i = i + 5) {
                if (edgeDeviceLogStrings[i+2].equals(audienceDevice)) {
                    isAudienceDeviceActive = true;
                    audienceIP = edgeDeviceLogStrings[i+3];
                    audiencePort = edgeDeviceLogStrings[i+4];
                    break;
                } 
            }

            // Checks if audience device is online. If not, send to client that audience device is offline.
            if (!isAudienceDeviceActive) {
                dataOutputStream.writeUTF(UVF_SERVER_AUDIENCE_OFFLINE);
                dataOutputStream.flush();
                System.out.println("Audience device " + audienceDevice + " is offline.");
                return;
            }

            // Sending audience device data
            dataOutputStream.writeUTF(UVF_SERVER_AUDIENCE_ONLINE);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(audienceIP);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(audiencePort);
            dataOutputStream.flush();
            System.out.println("Sent audience device " + audienceDevice + " information successfully.");
            System.out.println(audienceDevice + " IP: " + audienceIP);
            System.out.println(audienceDevice+ " port: " + audiencePort);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

    // define ClientThread for handling multi-threading issue
    // ClientThread needs to extend Thread and override run() method
    private static class ClientThread extends Thread {
        private final Socket clientSocket;
        private String deviceName = null;

        ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            
            super.run();
            // get client Internet Address and port number
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort();
            String clientID = "("+ clientAddress + ", " + clientPort + ")";

            System.out.println("New connection created for user - " + clientID);

            // define the dataInputStream to get message (input) from client
            // DataInputStream - used to acquire input from client
            // DataOutputStream - used to send data to client
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                dataInputStream = new DataInputStream(this.clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
                
                // Authenticating
                Authenticator authUtil = new Authenticator();
                this.deviceName = authUtil.authenticate(clientID, dataInputStream, dataOutputStream);

                // Invalid authentication
                if (this.deviceName == null) {
                    dataInputStream.close();
                    dataOutputStream.close();
                    this.clientSocket.close();
                    System.out.println("\n*** User " + clientID + " disconnected.");
                    return; 
                }

                // Getting the UDP audience port for the edge device
                String clientUDPPort = (String) dataInputStream.readUTF();

                // * ===== Adding to edge device log file
                File logFile = new File("edge-device-log.txt");
                int seqNum = 0;
                if (logFile.exists()) {
                    Scanner sc = new Scanner(logFile);
                    while(sc.hasNextLine()) {
                    sc.nextLine();
                    seqNum++;
                    }
                    sc.close();
                }
                seqNum++;

                boolean isEmptyFile = false; // This is for checking if the file exists but the file has no content, i.e., only one row but the row has nothing in it.
                if (logFile.exists() && seqNum == 1) {
                    BufferedReader buffer = new BufferedReader(new FileReader("edge-device-log.txt"));
                    String firstLine = buffer.readLine();
                    if (firstLine == null) isEmptyFile = true;
                    buffer.close();
                }

                String pattern = "dd MMMM yyyy HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String timestamp = simpleDateFormat.format(new Date());
          
                String logEntry = Integer.toString(seqNum) + "; " + timestamp + "; " + deviceName + "; " + clientAddress + "; " + clientUDPPort;

                boolean isNewFile = logFile.createNewFile();
                FileWriter logFileWriter = new FileWriter(logFile, true);
                if (!isNewFile && !isEmptyFile) {
                    logFileWriter.write("\n");
                }
                logFileWriter.write(logEntry);
                logFileWriter.close();
                // * ===== Adding to edge device log file END

                System.out.println("\n" + this.deviceName + " logged in.");

                while (true) {
                    // get input from client
                    // socket like a door/pipe which connects client and server together
                    // data from client would be read from clientSocket
                    assert dataInputStream != null;
                    assert dataOutputStream != null;
                    String message = (String) dataInputStream.readUTF();
                    System.out.println("");
                    if (message.equals(UED_CLIENT_SEND_FILE)) {
                        UEDProcessing(deviceName, dataInputStream, dataOutputStream);
                    } else if (message.equals(SCS_CLIENT_COMPUTE_REQUEST)) {
                        String requestedFile = (String) dataInputStream.readUTF();
                        String operation = (String) dataInputStream.readUTF();
                        SCSProcessing(requestedFile, operation, deviceName, dataInputStream, dataOutputStream);
                    } else if (message.equals(DTE_CLIENT_REQUEST)) {
                        String requestedFile = (String) dataInputStream.readUTF();
                        String fileID = (String) dataInputStream.readUTF();
                        DTEProcessing(fileID, requestedFile, deviceName, dataInputStream, dataOutputStream);
                    } else if (message.equals(OUT_CLIENT_REQUEST)) {
                        removeDevice(this.deviceName);
                        System.out.println("\n*** User " + clientID + " disconnected.");
                        return;
                    } else if (message.equals(AED_CLIENT_REQUEST)) {
                        AEDProcessing(this.deviceName, dataOutputStream);
                    } else if (message.equals(UVF_CLIENT_REQUEST)) {
                        UVFProcessing(deviceName, dataInputStream, dataOutputStream);
                    } else {
                        System.out.println("[recv]  " + message + " from user - " + clientID);
                        String responseMessage = "unknown request";
                        System.out.println("[send] " + message);
                        dataOutputStream.writeUTF(responseMessage);
                        dataOutputStream.flush();
                    }
                }


            } catch (EOFException e) {
                removeDevice(deviceName);
                System.out.println("\n*** User " + clientID + " disconnected.");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return; // Should kill the thread here
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("===== Error usage: java TCPServer SERVER_PORT LOGIN_ATTEMPT_NO =====");
            return;
        }

        if (!isInteger(args[1])) {
            System.out.println("Invalid number of allowed failed consecutive attempts: " + args[1] + ". The valid value of argument number is an integer between 1 and 5");
            return;
        }

        MAX_CONSECUTIVE_LOGIN_ATTEMPTS = Integer.parseInt(args[1]);

        if (MAX_CONSECUTIVE_LOGIN_ATTEMPTS < 1 || MAX_CONSECUTIVE_LOGIN_ATTEMPTS > 5 ) {
            System.out.println("Invalid number of allowed failed consecutive attempts: " + args[1] + ". The valid value of argument number is an integer between 1 and 5");
            return;
        }

        // Adding the credentials data to a hashmap for authentication
        Path credentialsPath = Path.of("./credentials.txt");
        String credentialsData = Files.readString(credentialsPath);
        String credentialStrings[] = credentialsData.split("\\s+");
        for (int i = 0; i < credentialStrings.length; i = i + 2) {
            credentials.put(credentialStrings[i], credentialStrings[i+1]);
            deviceTimeoutDate.put(credentialStrings[i], new Date(0));
            deviceConsecutiveLoginAttempts.put(credentialStrings[i], 0);
        }

        // acquire port number from command line parameter
        serverPort = Integer.parseInt(args[0]);

        // define server socket with the input port number, by default the host would be localhost i.e., 127.0.0.1
        serverSocket = new ServerSocket(serverPort);
        // make serverSocket listen connection request from clients
        System.out.println("===== Server is running =====");
        System.out.println("===== Waiting for connection request from clients...=====");

        while (true) {
            // when new connection request reaches the server, then server socket establishes connection
            Socket clientSocket = serverSocket.accept();
            // for each user there would be one thread, all the request/response for that user would be processed in that thread
            // different users will be working in different thread which is multi-threading (i.e., concurrent)
            ClientThread clientThread = new ClientThread(clientSocket);
            clientThread.start();
        }
    }
}



