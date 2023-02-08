package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

import org.apache.logging.log4j.*;

/**
 * A TCP server to receive operations of PUT, GET, DELETE from client.
 */
public class TcpServer {
    private static ServerSocket serverSocket;
    private static Socket client;
    private static int PORT = 8080;
    private static java.util.logging.Logger logger = Logger.getLogger(TcpServer.class.getName());

    private static void AnsToClient(Socket client, String requestType, String key, String returnMsg) {
        logger.info("Sending acknowledgement to client...");

        try {
            DataOutputStream outStream = new DataOutputStream(client.getOutputStream());

            if (returnMsg != "" && requestType.equalsIgnoreCase("GET")) {
                outStream.writeUTF("Retrieved message with key: " + key + " is: " + returnMsg);
            } else {
                outStream.writeUTF(requestType + " with key: " + key + " SUCCESS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle PUT request from TcpClient
     * @param client listens to the connection
     * @param content message sent by TcpClient
     * @param storeMap key-value store
     */
    private static void Put(Socket client, String content, Map<String, String> storeMap) {
        logger.info("PUT request received from " + client.getInetAddress() + " at Port " + client.getPort());

        if (content != "") {
            String key = content.substring(0, content.indexOf(","));
            String message = content.substring(content.indexOf(","));
            if (key != "") {
                logger.info("The request is to store a message with key: " + key);
                storeMap.put(key, message);
                AnsToClient(client,"PUT", key, "");
            } else {
                logger.info("Received a wrong request of length: " + content.length() + " from: " + client .getInetAddress() + " at Port: " + client.getPort());
            }
        } else {
            logger.info("The content is not found.");
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Get(Socket client, String content, Map<String, String> storeMap) {
        logger.info("GET request received from " + client.getInetAddress() + " at Port " + client.getPort());

        if (content != "") {
            String key = content;

            if (key != "") {
                logger.info(" Requesting to get a message with key: " + key);
                if (storeMap.containsKey(key)) {
                    String retrievedMsg = storeMap.get(key);
                    AnsToClient(client, "GET", key, retrievedMsg);
                } else {
                    logger.info("There exist no key-value pair for key: " + key);
                }
            } else {
                logger.info("Received a wrong request of length: " + content.length() + " from: "
                        + client.getInetAddress() + " at Port: " + client.getPort());
            }
        } else {
            logger.info("The content is not found.");
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Delete(Socket client, String msgContent, Map<String, String> storeMap) {
        logger.info(" DELETE request received from " + client.getInetAddress() + " at Port " + client.getPort());

        if (msgContent != "") {
            String key = msgContent;
            if (key != "") {
                logger.info(" Requesting to delete a message with key: " + key);
                if (storeMap.containsKey(key)) {
                    storeMap.remove(key);
                    AnsToClient(client, "DELETE", key, "");
                } else {
                    logger.info("There exists no key-value pair for key: " + key);
                }

            } else {
                logger.info("Received a wrong request of length: " + msgContent.length() + " from: " + client.getInetAddress() + " at Port: " + client.getPort());
            }
        } else {
            logger.info("The searched message content is not present.");
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Listening to port " + PORT);

            while (true) {
                // Create socket connection
                client = serverSocket.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                String clientMessage = input.readUTF();

                if (clientMessage != "") {
                    String requestType = clientMessage.substring(0, clientMessage.indexOf(" "));
                    String msgContent = clientMessage.substring(clientMessage.indexOf(" "));
                    logger.info("requestType: " + requestType + " msgContent" + msgContent);

                    if (requestType != "" && requestType.equalsIgnoreCase("PUT")) {
                        Put(client, msgContent, map);
                    } else if (requestType != "" && requestType.equalsIgnoreCase("GET")) {
                        Get(client, msgContent, map);
                    } else if (requestType != "" && requestType.equalsIgnoreCase("DELETE")) {
                        Delete(client, msgContent, map);
                    } else {
                        logger.info("Received unsolicited response acknowledging unknown: "+ requestType);
                    }

                    logger.info("current Map size is: " + map.size());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
