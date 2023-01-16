/*
 * Client for COMP3331 assignment 1
 *
 * Author: Max Xue
 * Date: 25-9-2022
 * */


import java.net.*;
import java.util.ArrayList;
import java.io.*;


public class Client {
    // server host and port number, which would be acquired from command line parameter
    private static String serverHost;
    private static Integer serverPort;
    private static String deviceName;

    private static final Integer UDP_PORT_LOWER_BOUND = 1025;
    private static final Integer UDP_PORT_UPPER_BOUND = 65534;

    // * Status codes for protocol. Should be matched exactly in server
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
    private static final String UVF_KILL_THREAD = "uvf kill thread";


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

    // Closes the given socket and data streams
    public static void closeSocket(Socket clientSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.close();
            dataInputStream.close(); 
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Returns true if the user has been authenticated on the server based on client inputs. If it returns false, the client should be closed as it signifies a timeout.
    public static boolean authenticate(Socket clientSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, BufferedReader reader) {
        // * Authentication
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            // Authenticating device name/username
            boolean validDeviceName = false;
            while (!validDeviceName) {
                System.out.print("Device Name: ");
                try {
                    String givenDeviceName = reader.readLine();
                    // send Username into dataOutputStream and send/flush to the server
                    dataOutputStream.writeUTF(givenDeviceName);
                    dataOutputStream.flush();

                    String responseMessage = (String) dataInputStream.readUTF();
                    if (responseMessage.equals(VALID_DEVICE_NAME)) {
                        validDeviceName = true;
                        deviceName = givenDeviceName; // This is a class attribute. Can use it outside the while loop.
                        // If the client is shut down (maybe due to wrong password causing timeout causing logout), this variable should be reset so other device names can be used.
                    } else if (responseMessage.equals(INVALID_DEVICE_NAME)) {
                        System.out.println("Invalid device name. Please try again");
                    } else if (responseMessage.equals(DEVICE_TIMEOUT)) {
                        System.out.println("Your account is blocked due to multiple authentication failures. Please try again later");
                        closeSocket(clientSocket, dataInputStream, dataOutputStream);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            // Authenticating password
            boolean validPassword = false;
            while (!validPassword) {
                System.out.print("Password: ");
                try {
                    String password = reader.readLine();
                    // send Password into dataOutputStream and send/flush to the server
                    dataOutputStream.writeUTF(password);
                    dataOutputStream.flush(); 

                    String responseMessage = (String) dataInputStream.readUTF();

                    if (responseMessage.equals(VALID_PASSWORD)) {
                        validPassword = true;
                        isAuthenticated = true;
                    } else if (responseMessage.equals(INVALID_PASSWORD_TIMEOUT)) {
                        System.out.println("Invalid password. Your account has been blocked. Please try again later");
                        closeSocket(clientSocket, dataInputStream, dataOutputStream);
                        return false;
                    } else if (responseMessage.equals(INVALID_PASSWORD)) {
                        System.out.println("Invalid Password. Please try again");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    // EDG processing
    public static void EDG(String inputs[]) {
        // Checking valid inputs/arguments
        if (inputs.length != 3) {
            System.out.println("EDG command requires a positive integer fileID and dataAmount (integer) larger than 0 as arguments.");
            return;
        }
        if (!isInteger(inputs[2]) || Integer.parseInt(inputs[2]) < 1 || !isInteger(inputs[1]) || Integer.parseInt(inputs[1]) < 0) {
            if (!isInteger(inputs[2]) || Integer.parseInt(inputs[2]) < 1) {
                System.out.println("dataAmount error: " + inputs[2]);
            }
            if (!isInteger(inputs[1]) || Integer.parseInt(inputs[1]) < 0) {
                System.out.println("fileID error: " + inputs[1]);
            }
            System.out.println("EDG command requires a positive integer fileID and dataAmount (integer) larger than 0 as arguments.");
            return;
        }
        
        // Generating data and putting it in file 
        try {
            File file = new File(deviceName + "-" + inputs[1] + ".txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            String dataString = "";
            for (int i = 0; i < Integer.parseInt(inputs[2]); i++) {
                if (i != 0) {
                    dataString = dataString + "\n";
                }
                dataString = dataString + Integer.toString(i+1);
            }
            fileWriter.write(dataString);
            fileWriter.close();
            System.out.println("Data generation done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // UED processing
    public static void UED(String inputs[], DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        // Checking valid inputs/arguments
        if (inputs.length != 2) {
            System.out.println("“fileID is needed to upload the data.");
            return;
        }
        if (!isInteger(inputs[1]) || Integer.parseInt(inputs[1]) < 0) {
            System.out.println("fileID error: " + inputs[1]);
            System.out.println("fileID must be positive integer.");
            return;
        }
        File requestedFile = new File(deviceName + "-" + inputs[1] + ".txt");
        if (!requestedFile.exists()) {
            System.out.println("The file to be uploaded does not exist.");
            return;
        }

        try {
            // Getting data out of specified file
            BufferedReader fileDataReader = new BufferedReader(new FileReader(requestedFile));
            String fileData = "";
            int dataAmount = 0;
            String line = fileDataReader.readLine();

            while (line != null) {
                fileData = fileData + line + "\n";
                dataAmount++;
                line = fileDataReader.readLine();
            }
            fileDataReader.close();

            // Sending file info for logging
            dataOutputStream.writeUTF(UED_CLIENT_SEND_FILE);
            dataOutputStream.flush();

            dataOutputStream.writeUTF(deviceName);
            dataOutputStream.flush();

            dataOutputStream.writeUTF(requestedFile.getName());
            dataOutputStream.flush();

            dataOutputStream.writeUTF(inputs[1]); // fileID
            dataOutputStream.flush();

            dataOutputStream.writeUTF(Integer.toString(dataAmount));
            dataOutputStream.flush();

            // Sending file data
            dataOutputStream.writeUTF(fileData.trim()); // Get rid of trailing \n
            dataOutputStream.flush();

            // Checking if data was sent successfully
            String responseMessage = (String) dataInputStream.readUTF();
            if (responseMessage.equals(UED_SERVER_RECEIVE_DATA_FILE_SUCCESS)) {
                System.out.println("File uploaded successfully.");
            } else if (responseMessage.equals(UED_SERVER_RECEIVE_DATA_FILE_FAILURE)) {
                System.out.println("Server failure.");
            } else if (responseMessage.equals(UED_SERVER_RECEIVE_DATA_FILE_ALREADY_EXISTS)) {
                System.out.println("File already exists on server.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file to be uploaded does not exist.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    // SCS processing
    public static void SCS(String inputs[], DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        // Checking valid inputs/arguments
        if (inputs.length != 3) {
            System.out.println("SCS command requires fileID and computationOperation as arguments.");
            return;
        }
        String operationsArray[] = {"AVERAGE", "MAX", "MIN", "SUM"};
        ArrayList<String> operations = new ArrayList<>();
        for (String o : operationsArray) {
            operations.add(o);
        }
        if (!operations.contains(inputs[2])) {
            System.out.println("Invalid SCS computationOperation. Please select from:\nAVERAGE\nMAX\nMIN\nSUM");
            return;
        }
        if (!isInteger(inputs[1]) || Integer.parseInt(inputs[1]) < 0) {
            System.out.println("fileID error: " + inputs[1]);
            System.out.println("fileID should be an positive integer.");
            return;
        }

        // Sending computation command to server
        String fileID = inputs[1];
        String computationOperation = inputs[2];
        try {
            dataOutputStream.writeUTF(SCS_CLIENT_COMPUTE_REQUEST);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(deviceName + "-" + fileID + ".txt");
            dataOutputStream.flush();

            if (computationOperation.equals("SUM")) {
                dataOutputStream.writeUTF(SCS_CLIENT_COMPUTE_REQUEST_SUM);
                dataOutputStream.flush();
            } else if (computationOperation.equals("AVERAGE")) {
                dataOutputStream.writeUTF(SCS_CLIENT_COMPUTE_REQUEST_AVG);
                dataOutputStream.flush();
            } else if (computationOperation.equals("MAX")) {
                dataOutputStream.writeUTF(SCS_CLIENT_COMPUTE_REQUEST_MAX);
                dataOutputStream.flush();
            } else if (computationOperation.equals("MIN")) {
                dataOutputStream.writeUTF(SCS_CLIENT_COMPUTE_REQUEST_MIN);
                dataOutputStream.flush();
            }

            // Checking if request was successful
            String responseMessage = (String) dataInputStream.readUTF();
            if (responseMessage.equals(SCS_SERVER_COMPUTE_FAILURE_FILE_NOT_FOUND)) {
                System.out.println("Requested fileID not on server.");
            } else if (responseMessage.equals(SCS_SERVER_COMPUTE_FAILURE)) {
                System.out.println("Server failure.");
            

            } else if (responseMessage.equals(SCS_SERVER_COMPUTE_SUCCESS)) { // Recieving and printing computation result from server
                String result = (String) dataInputStream.readUTF();
                System.out.println("Computation (" + computationOperation + ") result on the file (ID:" + fileID + ") returned from the server is: " + result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DTE processing
    public static void DTE(String inputs[], DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        // Checking valid inputs/arguments
        if (inputs.length != 2) {
            System.out.println("“fileID argument is needed to delete file from server.");
            return;
        }
        if (!isInteger(inputs[1]) || Integer.parseInt(inputs[1]) < 0) {
            System.out.println("fileID error: " + inputs[1]);
            System.out.println("fileID must be positive integer.");
            return;
        }

        try {
            // Sending file name to server to be deleted
            dataOutputStream.writeUTF(DTE_CLIENT_REQUEST);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(deviceName + "-" + inputs[1] + ".txt");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(inputs[1]);
            dataOutputStream.flush();

            // Checking if deletion was successful
            String responseMessage = (String) dataInputStream.readUTF();
            if (responseMessage.equals(DTE_SERVER_FAILURE_FILE_NOT_FOUND)) {
                System.out.println("Requested fileID not on server.");
            } else if (responseMessage.equals(DTE_SERVER_FAILURE)) {
                System.out.println("Server failure.");
            } else if (responseMessage.equals(DTE_SERVER_SUCCESS)) {
                System.out.println("File with ID of " + inputs[1] + " successfully deleted from server.");
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }

    }

    // UVF processing
    public static void UVF(String inputs[], DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        // Checking valid inputs/arguments
        if (inputs.length != 3) {
            System.out.println("UVF command requires deviceName and filename as arguments.");
            return;
        }
        if (inputs[1].equals(deviceName)) {
            System.out.println(deviceName + " is the current device. Can't send file to self.");
            return;
        }
        File requestedFile = new File(inputs[2]);
        if (!requestedFile.exists()) {
            System.out.println("The file to be uploaded does not exist.");
            return;
        }
        
        try {
            // Sending UVF request
            dataOutputStream.writeUTF(UVF_CLIENT_REQUEST);
            dataOutputStream.flush();
    
            String audienceDeviceName = inputs[1];
            String fileName = deviceName + "_" + inputs[2];

            // Sending specified audience device name to receive IP and port details
            dataOutputStream.writeUTF(audienceDeviceName);
            dataOutputStream.flush();
            
            // Checking if specified audience device is registers/online
            String responseMessage = (String) dataInputStream.readUTF();
            if (responseMessage.equals(UVF_SERVER_AUDIENCE_OFFLINE)) {
                System.out.println(audienceDeviceName + " is offline.");
                return;
            } else if (responseMessage.equals(UVF_SERVER_UNKNOWN_DEVICE)) {
                System.out.println("Unknown audience device: " + audienceDeviceName);
                return;
            } else if (responseMessage.equals(UVF_SERVER_AUDIENCE_ONLINE)) {
                System.out.println(audienceDeviceName + " is online.");
            }
    
            // Reviecing IP and port details of specified device
            String audienceIPString = (String) dataInputStream.readUTF();
            String audiencePortString = (String) dataInputStream.readUTF();
            InetAddress audienceIP = InetAddress.getByName(audienceIPString);
            int audiencePort = Integer.parseInt(audiencePortString);

            // Creating UDP socket to send audience client and opening file to be transferred
            final int BUFFERSIZE = 32768;
            String sourceFilePath = inputs[2];
            File file = new File(sourceFilePath);
            FileInputStream fin = new FileInputStream(file);
            DatagramSocket socket = new DatagramSocket();

            // * Sending the name of the file to be uploaded
            byte[] name = fileName.getBytes();
            DatagramPacket fileNamePacket = new DatagramPacket(name, name.length, audienceIP, audiencePort);
            socket.send(fileNamePacket);

            // * Sending the size of the file to be sent
            String fileLength = Long.toString(file.length());
            byte[] fileLengthBytes = fileLength.getBytes();
            DatagramPacket fileLengthPacket = new DatagramPacket(fileLengthBytes, fileLengthBytes.length, audienceIP, audiencePort);
            socket.send(fileLengthPacket);

            // * Sending the port the confirm messages are coming from
            int port = getRandomNumber();
            while (!portAvailable(port)) {
                port = getRandomNumber();
            }
            DatagramSocket confirmSocket = new DatagramSocket(port);
            String confirmationPortString = Integer.toString(port);
            byte[] confirmationPortBuffer = confirmationPortString.getBytes();
            DatagramPacket confirmationPortPacket = new DatagramPacket(confirmationPortBuffer, confirmationPortBuffer.length, audienceIP, audiencePort);
            socket.send(confirmationPortPacket);
            
            // Reading from file in chunks and sending chunks of file data to audience peer
            while (fin.available() != 0) {
                byte[] buffer = new byte[BUFFERSIZE];
                fin.read(buffer);
                // write to server, need to create DatagramPAcket with server address and port No
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, audienceIP, audiencePort);
                socket.send(sendPacket);
                DatagramPacket packetReceivedMsg = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE);
                confirmSocket.receive(packetReceivedMsg); // This should wait for the confirmation msg to denote the UDP packet has been received before continuing the loop and sending the next packet
            }

            confirmSocket.close();
            socket.close(); 
            fin.close();
            System.out.println(fileName + " sent to " + audienceDeviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    // Check if a given port number is available
    public static boolean portAvailable(int port) { 
        // https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
        if (port < UDP_PORT_LOWER_BOUND || port > UDP_PORT_UPPER_BOUND) {
            return false;
        }
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

    // Get random number between UDP_PORT_LOWER_BOUND and UDP_PORT_UPPER_BOUND to randomly assign a port number for the confirmation socket for UVF audience device thread
    public static int getRandomNumber() {
        return (int) ((Math.random() * (UDP_PORT_UPPER_BOUND - UDP_PORT_LOWER_BOUND)) + UDP_PORT_LOWER_BOUND);
    }


    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("===== Error usage: java TCPClient SERVER_IP SERVER_PORT CLIENT_UDP_SERVER_PORT =====");
            return;
        }

        serverHost = args[0];
        serverPort = Integer.parseInt(args[1]);

        // Checking if specified client UDP port is integer
        if (!isInteger(args[2]) || Integer.parseInt(args[2]) < UDP_PORT_LOWER_BOUND || Integer.parseInt(args[2]) > UDP_PORT_UPPER_BOUND) {
            System.out.println("===== Error usage: java TCPClient SERVER_IP SERVER_PORT CLIENT_UDP_SERVER_PORT =====");
            System.out.println("Please pick a CLIENT_UDP_SERVER_PORT integer than is between " + UDP_PORT_LOWER_BOUND + " and " + UDP_PORT_UPPER_BOUND);
            return;
        }
        

        // define socket for client
        Socket clientSocket = new Socket(serverHost, serverPort);

        // define DataInputStream instance which would be used to receive response from the server
        // define DataOutputStream instance which would be used to send message to the server
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

        // define a BufferedReader to get input from command line i.e., standard input from keyboard
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        boolean authSuccess = authenticate(clientSocket, dataInputStream, dataOutputStream, reader);
        if (!authSuccess) return; // If false, device is either timed out or exception error so close client
        System.out.println("Welcome!");

        int clientUDPPort = Integer.parseInt(args[2]);
        if (!portAvailable(clientUDPPort)) {
            System.out.println("===== Error usage: java TCPClient SERVER_IP SERVER_PORT CLIENT_UDP_SERVER_PORT =====");
            System.out.println("Chosen CLIENT_UDP_SERVER_PORT is not available");
            return;
        }

        UDPThread udpThread = new UDPThread(clientUDPPort);
        udpThread.start();
        dataOutputStream.writeUTF(Integer.toString(clientUDPPort));
        dataOutputStream.flush();

        while (true) {
            System.out.print("\nEnter one of the following commands (EDG, UED, SCS, DTE, AED, UVF, OUT): ");
            // read input from command line
            String message = reader.readLine();
            String inputs[] = message.split("\\s+");
            String command = inputs[0];

            if (command.equals("EDG")) {
                EDG(inputs);
            } else if (command.equals("UED")) {
                UED(inputs, dataInputStream, dataOutputStream);
            } else if (command.equals("SCS")) {
                SCS(inputs, dataInputStream, dataOutputStream);
            } else if (command.equals("DTE")) {
                DTE(inputs, dataInputStream, dataOutputStream);
            } else if (command.equals("AED")) {
                if (inputs.length != 1) {
                    // ! Incorrect args
                    System.out.println("AED has no arguments.");
                    continue;
                }
                dataOutputStream.writeUTF(AED_CLIENT_REQUEST);
                dataOutputStream.flush();
                String responseMessage = (String) dataInputStream.readUTF();
                if (responseMessage.equals(AED_SERVER_NO_OTHER_DEVICES)) {
                    System.out.println("No other active edge devices.");
                } else if (responseMessage.equals(AED_SERVER_SUCCESS)) {
                    String responseDevicesMessage = (String) dataInputStream.readUTF();
                    System.out.println(responseDevicesMessage);
                } else {
                    System.out.println("Server error");
                    continue;
                }
            } else if (command.equals("OUT")) {
                if (inputs.length != 1) {
                    // ! Incorrect args
                    System.out.println("OUT has no arguments. Do you want to continue(y/n) :");
                    if (!reader.readLine().equals("n")) continue;
                }
                dataOutputStream.writeUTF(OUT_CLIENT_REQUEST);
                System.out.println("Good bye");
                clientSocket.close();
                dataOutputStream.close();
                dataInputStream.close();

                DatagramSocket socket = new DatagramSocket();
                String finString = UVF_KILL_THREAD;
                byte[] finBtyes = finString.getBytes();
                DatagramPacket finPacket = new DatagramPacket(finBtyes, finBtyes.length, clientSocket.getInetAddress(), clientUDPPort);
                socket.send(finPacket);
                socket.close(); 

                return;
            } else if (command.equals("UVF")) {
                UVF(inputs, dataInputStream, dataOutputStream);
            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    // This UDPThread is for allowing the client to act as an audience/server of peer 2 peer transmissions
    // UDPThread needs to extend Thread and override run() method
    private static class UDPThread extends Thread {

        private final int UDPPort;

        UDPThread(int UDPPort) {
            this.UDPPort = UDPPort;
        }
        @Override
        public void run() {
            super.run();
            

            try {
                while (true) {
                    final int BUFFERSIZE = 32768;
                    // Create a datagram socket for receiving and sending UDP packets
                    // through the port specified on the command line.
                    DatagramSocket socket = new DatagramSocket(UDPPort);

                    // Create a datagram packet to hold incomming UDP packet.
                    DatagramPacket fileNamePacket = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE); 
                    socket.receive(fileNamePacket);

                    if (getStringData(fileNamePacket).equals(UVF_KILL_THREAD)) {
                        socket.close();
                        return;
                    }

                    String fileName = getStringData(fileNamePacket);
                    String outputFilePath = fileName;
                    File newFile = new File(outputFilePath);
                    if (newFile.exists()) {
                        newFile.delete();
                        newFile = new File(outputFilePath);
                    }
                    FileOutputStream fout = new FileOutputStream(newFile);

                    // Get UDP packet specifying size of file in bytes.
                    DatagramPacket fileSizePacket = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE); 
                    socket.receive(fileSizePacket);
                    int fileSize = Integer.parseInt(getStringData(fileSizePacket));
                    
                    // Setting up a socket to recieve UDP packets with confirmation messages that the previous UDP data packet has successfully been recieved
                    DatagramSocket confirmSocket = new DatagramSocket();
                    String recievedMessage = "recieved packet";
                    byte[] recievedMessageBytesArray = recievedMessage.getBytes();
                    // Get UDP packet specifying the port to send confirmation msgs that packets were recieved.
                    DatagramPacket confirmationPortPacket = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE); 
                    socket.receive(confirmationPortPacket);
                    int confirmationPort = Integer.parseInt(getStringData(confirmationPortPacket));


                    while (true) {
                        DatagramPacket dataRequest = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE); 

                        // Block until the host receives a UDP packet.
                        socket.receive(dataRequest);

                        int currFileSize = (int) newFile.length();

                        if (fileSize - currFileSize < BUFFERSIZE) {
                            byte[] buffer = new byte[BUFFERSIZE];
                            buffer = dataRequest.getData();
                            fout.write(buffer, 0, fileSize - currFileSize);

                            DatagramPacket recievedMessagePacket = new DatagramPacket(recievedMessageBytesArray, recievedMessageBytesArray.length, confirmationPortPacket.getAddress(), confirmationPort); 
                            confirmSocket.send(recievedMessagePacket);
                            break;
                        }

                        byte[] buffer = new byte[BUFFERSIZE];
                        buffer = dataRequest.getData();
                        fout.write(buffer, 0, buffer.length);

                        DatagramPacket recievedMessagePacket = new DatagramPacket(recievedMessageBytesArray, recievedMessageBytesArray.length, confirmationPortPacket.getAddress(), confirmationPort); 
                        confirmSocket.send(recievedMessagePacket);
                    }

                    socket.close();
                    fout.close();
                    confirmSocket.close();
                    System.out.println("\n\nReceived " + fileName);
                    System.out.print("\nEnter one of the following commands (EDG, UED, SCS, DTE, AED, UVF, OUT): ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return; // Should kill the thread here
            }
        }

        /* 
        * Return data as string.
        */
        private static String getStringData(DatagramPacket request) throws Exception
        {
            // Obtain references to the packet's array of bytes.
            byte[] buf = request.getData();

            // Wrap the bytes in a byte array input stream,
            // so that you can read the data as a stream of bytes.
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);

            // Wrap the byte array output stream in an input stream reader,
            // so you can read the data as a stream of characters.
            InputStreamReader isr = new InputStreamReader(bais);

            // Wrap the input stream reader in a bufferred reader,
            // so you can read the character data a line at a time.
            // (A line is a sequence of chars terminated by any combination of \r and \n.) 
            BufferedReader br = new BufferedReader(isr);

            // The message data is contained in a single line, so read this line.
            String line = br.readLine().trim();
            return new String(line);
        }
    }
}
