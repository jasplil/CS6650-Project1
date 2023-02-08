package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.logging.Logger;

import org.apache.logging.log4j.*;

public class TcpClient {
    /**
     * Change to dynamic port & host later!!!
     */
    private static Socket socket;
    private static int PORT = 8080;
    private static String HOST = "localhost";
    private static Logger logger = Logger.getLogger(TcpClient.class.getName());
    private static PropertiesHandler PropertiesHandler;

    private static void ReqFromServer(Socket client) {
        try {
            DataInputStream inputStream = new DataInputStream(client.getInputStream());
            client.setSoTimeout(Integer.valueOf(PropertiesHandler.getInstance().getValue("CLIENT SOCKET TIMEOUT")));
            String ackMessage = inputStream.readUTF();
            logger.info("Acknowledgement message2: " + ackMessage);
        } catch (SocketTimeoutException e) {
            logger.info("Server is not responding. Timeout error has occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            logger.info("Exception2: " + ex);
        }
    }

    public static void put() {
        try {
            String putData = PropertiesHandler.getInstance().getValue("TCP_PUT_REQUEST_DATA");
            logger.info("put data in client hashmap: " + putData);

            List<String> items = Arrays.asList(putData.split("\\s*\\|\\s*"));
            logger.info("items stored in as arrays: " + items);

            DataOutputStream outputStream;
            Socket client;
            for (String item : items) {
                client = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(client.getOutputStream());
                logger.info("String items: " + item);
                outputStream.writeUTF("PUT " + item);
                ReqFromServer(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        put();
    }
}
