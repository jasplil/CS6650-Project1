package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
//import java.util.*;
//import java.util.logging.Logger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A TCP client to send PUT, GET, DELETE operations to server.
 */
public class TcpClient {
    /**
     * Change to dynamic port & host !!!
     */
    private static Socket socket;
    private static int PORT = 2900;
    private static String HOST = "localhost";
    private static Logger logger = LogManager.getLogger(TcpClient.class.getName());

    /**
     * Function that is robust to server failure by using a timeout mechanism to deal with an
     * unresponsive server
     * @param client listens to connection
     */
    private static void ResFromServer(Socket client) {
        try {
            // Set timeout
            String timeOut = PropertiesHandler.getInstance().getValue("CLIENT_SOCKET_TIMEOUT");
            client.setSoTimeout(Integer.parseInt(timeOut));
            // Get response from server
            DataInputStream inputStream = new DataInputStream(client.getInputStream());
            String ackMessage = inputStream.readUTF();
            logger.info("Acknowledgement message: " + ackMessage);
        } catch (SocketTimeoutException e) {
            logger.error("Server is not responding. Timeout error has occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            logger.error("There is an exception: " + exception);
        }
    }

    /**
     * Sends 5 PUT operations to server
     */
    public static void Put() {
        try {
            // Get pre-populated data
            String putData = PropertiesHandler.getInstance().getValue("TCP_PUT_REQUEST_DATA");
            String[] items = putData.split("\\s*\\|\\s*");

            DataOutputStream outputStream;
            Socket client;

            // Send 5 put requests
            for (String item : items) {
                client = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(client.getOutputStream());
                logger.info("String items: " + item);
                outputStream.writeUTF("PUT " + item);
                ResFromServer(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Get() {
        String getData = PropertiesHandler.getInstance().getValue("TCP_GET_REQUEST_DATA");
        Socket client = null;

        try {
            String[] items = getData.split("\\s*,\\s*");
            DataOutputStream outputStream;

            // Send 5 get requests
            for (String item : items) {
                client = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(client.getOutputStream());
//                logger.info("Get items: " + item);
                outputStream.writeUTF("GET " + item);
                ResFromServer(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void Delete() {
        String reqReqData = PropertiesHandler.getInstance().getValue("TCP_DEL_REQUEST_DATA");
        logger.info("deleting data in clients array: " + reqReqData);
        Socket client = null;

        try {
            String[] items = reqReqData.split("\\s*,\\s*");
            DataOutputStream outputStream;

            for (String item : items) {
                client = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(client.getOutputStream());
                outputStream.writeUTF("DELETE " + item);
                ResFromServer(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Put();
        Get();
        Delete();
    }
}
