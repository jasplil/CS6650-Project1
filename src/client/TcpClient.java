package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A TCP client to send PUT, GET, DELETE operations to server.
 */
public class TcpClient {
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
            logger.error("Server is not responding. Timeout error occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            logger.error("There is an exception: " + exception);
        }
    }

    /**
     * Sends 5 PUT operations to server
     */
    public static void Put(String HOST, int PORT) {
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

    private static void Get(String HOST, int PORT) {
        String getData = PropertiesHandler.getInstance().getValue("TCP_GET_REQUEST_DATA");
        Socket client = null;

        try {
            String[] items = getData.split("\\s*,\\s*");
            DataOutputStream outputStream;

            // Send 5 get requests
            for (String item : items) {
                client = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(client.getOutputStream());
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

    private static void Delete(String HOST, int PORT) {
        String delData = PropertiesHandler.getInstance().getValue("TCP_DEL_REQUEST_DATA");
        logger.info("Deleting data in clients array: " + delData);
        Socket client = null;

        try {
            String[] items = delData.split("\\s*,\\s*");
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port number:");
        int PORT = scanner.nextInt();

        scanner = new Scanner(System.in);
        System.out.println("Please enter the hostname/Local ip:");
        String HOST = scanner.nextLine();

        if (PORT < 0) throw new IOException("Port number should be larger than 0");

        Put(HOST, PORT);
        Get(HOST, PORT);
        Delete(HOST, PORT);
    }
}
