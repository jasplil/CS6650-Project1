package client;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.LogManager;
//import java.util.logging.Logger;

import org.apache.logging.log4j.*;
/**
 * A UDP client to send PUT, GET, DELETE operations to server.
 */
public class UdpClient {
    private static Logger logger = (Logger) LogManager.getLogManager().getLogger(UdpClient.class.getName());
    private static Socket socket;
    private static int PORT = 8080;
    private static String HOST = "localhost";

    private static void AckFromServer(DatagramSocket client) {
        try {
            client.setSoTimeout(Integer.parseInt(PropertiesHandler.getInstance().getValue("CLIENT_SOCKET_TIMEOUT")));
            byte[] ackMsgBuffer = new byte[500];
            DatagramPacket returnMsgPacket = new DatagramPacket(ackMsgBuffer, ackMsgBuffer.length);
            client.receive(returnMsgPacket);
            logger.info("Acknowledgement message: " + new String(returnMsgPacket.getData()));
        } catch (SocketTimeoutException e) {
            logger.info("Server is not responding. Timeout error has occurred.");
        } catch (IOException e) {
            logger.info("An exception has occurred: " + e);
        } catch (Exception ex) {
            logger.info("Exception: " + ex);
        }
    }

    private static void Put(InetAddress host, int portNumber) {
        String putReqData = PropertiesHandler.getInstance().getValue("UDP_PUT_REQUEST_DATA");
        logger.debug("put data in client: " + putReqData);
        DatagramSocket client = null;

        try {
            String[] items = putReqData.split("\\s*\\|\\s*");
            for (String tokens : items) {
                client = new DatagramSocket();
                logger.info("Message String items: " + tokens);
                String clientMsg = "PUT " + tokens;
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

    private static void Get(InetAddress host, int portNumber) {
        String reqReqData = PropertiesHandler.getInstance().getValue("UDP_GET_REQUEST_DATA");
        logger.info("get data in client: " + reqReqData);
        DatagramSocket client = null;
        try {
            String[] items = reqReqData.split("\\s*,\\s*");
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
        String reqReqData = PropertiesHandler.getInstance().getValue("UDP_DEL_REQUEST_DATA");
        logger.info("get delete data in client: " + reqReqData);
        DatagramSocket client = null;
        try {
            String[] items = reqReqData.split("\\s*,\\s*");
            for (String tokens : items) {
                client = new DatagramSocket();
                logger.info("Message String items: " + tokens);
                String clientMsg = "DEL " + tokens;
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

    public static void main(String[] args) {
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
