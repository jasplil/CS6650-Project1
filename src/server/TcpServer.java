package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A TCP server to receive operations of PUT, GET, DELETE from client.
 */
public class TcpServer {
    private static ServerSocket serverSocket;
    private static Socket client;
    private static int PORT = 2900;
    private static Logger logger = LogManager.getLogger(TcpServer.class.getName());

    private static void AnsToClient(Socket client, String requestType, String key, String returnMsg) {
        logger.info("Sending acknowledgement to client...");

        try {
            DataOutputStream outStream = new DataOutputStream(client.getOutputStream());

            if (!returnMsg.equals("") && requestType.equalsIgnoreCase("GET")) {
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

        if (!content.equals("")) {
            String key = content.substring(0, content.indexOf(","));
            String message = content.substring(content.indexOf(","));
            if (!key.equals("")) {
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

        if (!content.equals("")) {
            logger.info(" Requesting to get a message with key: " + content);
            if (storeMap.containsKey(content)) {
                String retrievedMsg = storeMap.get(content);
                AnsToClient(client, "GET", content, retrievedMsg);
            } else {
                logger.info("There exist no key-value pair for key: " + content);
            }
        } else {
            logger.error("Received a wrong request from: " + client.getInetAddress() + " at Port: " + client.getPort());
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Delete(Socket client, String msgContent, Map<String, String> storeMap) {
        logger.info(" DELETE request received from " + client.getInetAddress() + " at Port " + client.getPort());

        if (!msgContent.equals("")) {
//            String key = msgContent;
            logger.info(" Requesting to delete a message with key: " + msgContent);
            if (storeMap.containsKey(msgContent)) {
                storeMap.remove(msgContent);
                AnsToClient(client, "DELETE", msgContent, "");
            } else {
                logger.info("There exists no key-value pair for key: " + msgContent);
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

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Listening to port " + PORT);

            while (true) {
                // Create socket connection
                client = serverSocket.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                String clientMessage = input.readUTF();

                if (!clientMessage.equals("")) {
                    String requestType = clientMessage.substring(0, clientMessage.indexOf(" "));
                    String msgContent = clientMessage.substring(clientMessage.indexOf(" "));
                    logger.info("Request type: " + requestType + " Message content" + msgContent);

                    if (requestType.equalsIgnoreCase("PUT")) {
                        Put(client, msgContent, map);
                    } else if (requestType.equalsIgnoreCase("GET")) {
                        Get(client, msgContent, map);
                    } else if (requestType.equalsIgnoreCase("DELETE")) {
                        Delete(client, msgContent, map);
                    } else {
                        logger.info("Received unsolicited response acknowledging unknown: "+ requestType);
                    }
                    logger.debug("current Map size is: " + map.size());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
