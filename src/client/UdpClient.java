package client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import org.apache.logging.log4j.*;

/**
 * A UDP client to send PUT, GET, DELETE operations to server.
 */
public class UdpClient {
    private static Logger logger = LogManager.getLogger(UdpClient.class.getName());
    private static int PORT = 8080;

    private static void AckFromServer(DatagramSocket client) {
        try {
            client.setSoTimeout(Integer.parseInt(PropertiesHandler.getInstance().getValue("CLIENT_SOCKET_TIMEOUT")));
            byte[] ackMsgBuffer = new byte[500];
            DatagramPacket returnMsgPacket = new DatagramPacket(ackMsgBuffer, ackMsgBuffer.length);
            client.receive(returnMsgPacket);
            logger.info("Acknowledgement message: " + new String(returnMsgPacket.getData()));
        } catch (SocketTimeoutException e) {
            logger.info("Server is not responding. Timeout error occurred.");
        } catch (IOException e) {
            logger.info("An exception has occurred: " + e);
        } catch (Exception ex) {
            logger.info("Exception: " + ex);
        }
    }

    private static void Put(InetAddress host, int portNumber) {
        String putData = PropertiesHandler.getInstance().getValue("UDP_PUT_REQUEST_DATA");
        logger.debug("Put data in client: " + putData);
        DatagramSocket client = null;

        try {
            String[] items = putData.split("\\s*\\|\\s*");

            for (String tokens : items) {
                client = new DatagramSocket();
                logger.info("Message String items: " + tokens);
                String clientMsg = "PUT " + tokens;
                DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(), clientMsg.length(), host, portNumber);
                client.send(clientMsgPacket);
                AckFromServer(client);
                client.close();
            }
        } catch (IOException e) {
            logger.info("An exception has occurred: " + e);
        } finally {
            client.close();
        }
    }

    private static void Get(InetAddress host, int portNumber) {
        String getData = PropertiesHandler.getInstance().getValue("UDP_GET_REQUEST_DATA");
        logger.info("Get data in client: " + getData);
        DatagramSocket client = null;
        try {
            String[] items = getData.split("\\s*,\\s*");
            for (String tokens : items) {
                client = new DatagramSocket();
                logger.info("Message String items: " + tokens);
                String clientMsg = "GET " + tokens;
                DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(),clientMsg.length(),host,portNumber);
                client.send(clientMsgPacket);
                AckFromServer(client);
                client.close();
            }

        } catch (IOException e) {
            logger.info("An exception has occurred: " + e);
        } finally {
            client.close();
        }

    }

    private static void Delete(InetAddress host, int portNumber) {
        String delData = PropertiesHandler.getInstance().getValue("UDP_DEL_REQUEST_DATA");
        logger.info("Delete data in client: " + delData);
        DatagramSocket client = null;
        try {
            String[] items = delData.split("\\s*,\\s*");
            for (String tokens : items) {
                client = new DatagramSocket();
                logger.info("Message String items: " + tokens);
                String clientMsg = "DELETE" + tokens;
                DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(),clientMsg.length(),host,portNumber);
                client.send(clientMsgPacket);
                AckFromServer(client);
                client.close();
            }

        } catch (IOException e) {
            logger.info("An exception has occurred: " + e);
        } finally {
            client.close();
        }

    }

    public static void main(String[] args) throws IOException {
        // Get port from console
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port number:");
        PORT = scanner.nextInt();
        if (PORT < 0) throw new IOException("Port number should be larger than 0");

        try {
            InetAddress host = InetAddress.getByName("localhost");
            Put(host, PORT);
            Get(host,PORT);
            Delete(host,PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
