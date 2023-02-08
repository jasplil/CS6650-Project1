package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import org.apache.logging.log4j.*;

public class UdpServer {
    private static ServerSocket serverSocket;
    private static Socket client;
    private static int PORT = 8080;
    private static Logger LOGGER = Logger.getLogger(UdpServer.class.getName());

    private static void AckToClient(DatagramSocket socket, DatagramPacket request, String requestType, String key, String returnMsg) {
        LOGGER.info("Sending acknowledgement to client...");
        try {
            byte[] ackMessage = new byte[500];
            if (returnMsg != "" && requestType.equalsIgnoreCase("GET")) {
                ackMessage = ("Retrieved message with key: " + key + " is: " + returnMsg).getBytes();
            } else {
                ackMessage = (requestType + " with key: " + key + " SUCCESS").getBytes();
            }
            DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
                    request.getPort());
            socket.send(ackMsgPacket);

        } catch (IOException e) {
            LOGGER.info("An exception has occured: " + e);
        }

    }

    private static void sendFailureAckToClient(DatagramSocket socket, DatagramPacket request, String returnMsg) {
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
                AckToClient(socket, clientPacket, "PUT", key, "");
            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.info(failureMsg);
                sendFailureAckToClient(socket, clientPacket, failureMsg);
            }
        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.info(failureMsg);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
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
                    AckToClient(socket, clientPacket, "GET", key, retrievedMsg);
                } else {
                    String failureMsg = "There is no key-value pair for key: " + key;
                    LOGGER.info(failureMsg);
                    sendFailureAckToClient(socket, clientPacket, failureMsg);
                }

            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.info(failureMsg);
                sendFailureAckToClient(socket, clientPacket, failureMsg);
            }

        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.info(failureMsg);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
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
                    AckToClient(socket, clientPacket, "DEL", key, "");
                } else {
                    String failureMsg = "There exist no such key-value pair for key: " + key;
                    LOGGER.info(failureMsg);
                    sendFailureAckToClient(socket, clientPacket, failureMsg);
                }

            } else {
                String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                LOGGER.info(failureMsg);
                sendFailureAckToClient(socket, clientPacket, failureMsg);
            }

        } else {
            String failureMsg = "The message content is not present.";
            LOGGER.info(failureMsg);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
        }

    }

    public static void main(String[] args) {
        Map<String, String> messageStoreMap = new HashMap<>();

        DatagramSocket socket;
        try {
            socket = new DatagramSocket(PORT);

            byte[] msgbuffer = new byte[500];

            while (true) {
                DatagramPacket dataPacket = new DatagramPacket(msgbuffer, msgbuffer.length);
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
                LOGGER.info("current Map size is: " + messageStoreMap.size());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
