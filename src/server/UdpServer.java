package server;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class UdpServer {
    private static int PORT = 8080;
    private static Logger LOGGER = LogManager.getLogger(UdpServer.class.getName());

    private static void AnsToClient(DatagramSocket socket, DatagramPacket request, String requestType, String key, String returnMsg) {
        LOGGER.info("Sending acknowledgement to client");
        try {
            byte[] ackMessage = new byte[500];
            if (returnMsg != "" && requestType.equalsIgnoreCase("GET")) {
                ackMessage = ("Received with key: " + key + " value: " + returnMsg).getBytes();
            } else {
                ackMessage = (requestType + " with key: " + key + " SUCCESS").getBytes();
            }
            DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
                    request.getPort());
            socket.send(ackMsgPacket);

        } catch (IOException e) {
            LOGGER.info("Exception: " + e);
        }

    }

    private static void sendFailureAnsToClient(DatagramSocket socket, DatagramPacket request, String returnMsg) {
        LOGGER.info("Sending acknowledgement to client for failure...");
        try {
            byte[] ackMessage = new byte[500];
            ackMessage = ("Request FAILED due to: " + returnMsg).getBytes();
            DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
                    request.getPort());
            socket.send(ackMsgPacket);

        } catch (IOException e) {
            LOGGER.info("An exception has occured: " + e);
        }

    }

    private static void Put(DatagramSocket socket, DatagramPacket clientPacket, Map<String, String> messageStoreMap) {
        LOGGER.info("Received a PUT request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
        String messageData = new String(clientPacket.getData());
        if (messageData != "") {
            String keyValueData = messageData.substring(messageData.indexOf(" "));
            String key = keyValueData.substring(0, keyValueData.indexOf(","));
            String message = keyValueData.substring(keyValueData.indexOf(",") + 1);
            if (key != "") {
                LOGGER.info("The request is to store a message with key: " + key + " and Message" + message);
                messageStoreMap.put(key.trim(), message);
                AnsToClient(socket, clientPacket, "PUT", key, "");
            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: " + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.error(failureMsg);
                sendFailureAnsToClient(socket, clientPacket, failureMsg);
            }
        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.info(failureMsg);
            sendFailureAnsToClient(socket, clientPacket, failureMsg);
        }
    }

    private static void Get(DatagramSocket socket, DatagramPacket clientPacket,
                                   Map<String, String> messageStoreMap) {
        LOGGER.info("Received a GET request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
        String messageData = new String(clientPacket.getData());
        if (messageData != "") {
            String keyValueData = messageData.substring(messageData.indexOf(" "));
            String key = keyValueData.substring(0, keyValueData.indexOf(","));
            if (key != "") {
                LOGGER.info("The request is to get a message with key: " + key);
                if (messageStoreMap.containsKey(key.trim())) {
                    String retrievedMsg = messageStoreMap.get(key.trim());
                    AnsToClient(socket, clientPacket, "GET", key, retrievedMsg);
                } else {
                    String failureMsg = "There is no key-value pair for key: " + key;
                    LOGGER.error(failureMsg);
                    sendFailureAnsToClient(socket, clientPacket, failureMsg);
                }

            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.error(failureMsg);
                sendFailureAnsToClient(socket, clientPacket, failureMsg);
            }

        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.error(failureMsg);
            sendFailureAnsToClient(socket, clientPacket, failureMsg);
        }

    }

    private static void Delete(DatagramSocket socket, DatagramPacket clientPacket,
                                      Map<String, String> messageStoreMap) {
        LOGGER.info(
                "Received a DELETE request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
        String messageData = new String(clientPacket.getData());
        if (messageData != "") {
            String keyValueData = messageData.substring(messageData.indexOf(" "));
            String key = keyValueData.substring(0, keyValueData.indexOf(","));
            if (key != "") {
                LOGGER.info("The request is to get a message with key: " + key);
                if (messageStoreMap.containsKey(key.trim())) {
                    messageStoreMap.remove(key.trim());
                    AnsToClient(socket, clientPacket, "DEL", key, "");
                } else {
                    String failureMsg = "There exist no such key-value pair for key: " + key;
                    LOGGER.info(failureMsg);
                    sendFailureAnsToClient(socket, clientPacket, failureMsg);
                }

            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.info(failureMsg);
                sendFailureAnsToClient(socket, clientPacket, failureMsg);
            }

        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.info(failureMsg);
            sendFailureAnsToClient(socket, clientPacket, failureMsg);
        }

    }

    public static void main(String[] args) {
        Map<String, String> messageStoreMap = new HashMap<>();

        DatagramSocket socket;
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the port number:");
            PORT = scanner.nextInt();
            if (PORT < 0) throw new IOException("Port number should be larger than 0");

            socket = new DatagramSocket(PORT);

            byte[] buffer = new byte[500];

            while (true) {
                DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(dataPacket);
                System.out.println("Message from client: " + new String(dataPacket.getData()));
                String clientMessage = new String(dataPacket.getData());
                if (clientMessage != "") {
                    String requestType = clientMessage.substring(0, clientMessage.indexOf(" "));
                    LOGGER.info("requestType: " + requestType);
                    if (requestType != "" && requestType.equalsIgnoreCase("PUT")) {
                        Put(socket, dataPacket, messageStoreMap);
                    } else if (requestType != "" && requestType.equalsIgnoreCase("GET")) {
                        Get(socket, dataPacket, messageStoreMap);
                    } else if (requestType != "" && requestType.equalsIgnoreCase("DEL")) {
                        Delete(socket, dataPacket, messageStoreMap);
                    } else {
                        LOGGER.info("Unknown request type: " + requestType + " is received.");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
