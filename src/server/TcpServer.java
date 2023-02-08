package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

import client.TcpClient;
import org.apache.logging.log4j.*;

public class TcpServer {
    private static ServerSocket serverSocket;
    private static Socket client;
    private static int PORT = 8080;
    private static java.util.logging.Logger logger = Logger.getLogger(TcpServer.class.getName());

    /**
     * Handle PUT request from TcpClient
     * @param client
     * @param content
     * @param storeMap
     */
    private static void Put(Socket client, String content, Map<String, String> storeMap) {
        logger.info("PUT request received from " + client.getInetAddress() + " at Port " + client.getPort());
        if (content != "") {

            String key = content.substring(0, content.indexOf(","));
            String message = content.substring(content.indexOf(","));
            if (key != "") {
                logger.info("The request is to store a message with key: " + key);
                storeMap.put(key, message);
//                AckToClient(client, "PUT", key, "");
            } else {
                logger.info("Received a wrong request of length: " + content.length() + " from: "
                        + client .getInetAddress() + " at Port: " + client.getPort());
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
        /**
         * Create socket connection
         */
        serverSocket = new ServerSocket(PORT);
        client = serverSocket.accept();
        System.out.println("Listening to port " + PORT);
    }
}
