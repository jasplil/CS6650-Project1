# Project 1 Single Server, Key-Value Store (TCP and UDP)
## Project description
The assignment is to design a server program that acts as a key-value store and a client program that communicates with the server to perform three basic operations: PUT, GET, and DELETE. The client and server programs must use sockets and must be configurable to communicate using either UDP or TCP protocols.

The client must take the hostname or IP address of the server and its port number as command-line arguments and must be robust to server failure using a timeout mechanism. The client must also have simple protocol to communicate packet contents and must be robust against malformed or unrequested packets.

The server must take the port number as a command-line argument and must run forever, displaying requests and responses in a human-readable fashion. The server must be robust to malformed datagram packets and must timestamp every line printed to the log. There must be two instances of the client and server, one for each protocol.
## Project highlights
###Logging
The assignment requires the client and server to log their activities in a human-readable format. We need to decide what information to log in the console and how it should be displayed.

###Network Communication
The assignment requires the client and server to communicate using two distinct L4 communication protocols, UDP and TCP. We need to have a good understanding of socket programming and how to encode and decode packets using the two protocols.

###Robustness
The assignment requires the client and server to be robust to malformed or incorrect data packets, which could cause the program to crash. We need to carefully consider how to handle these cases and provide error messages in a human-readable format.

###Protocol Design
To design a simple protocol to communicate packet contents between the client and server. This requires careful consideration of the data that needs to be transmitted, the format of the data, and how it will be interpreted by the receiver.

###Timestamping
The assignment requires the client and server to log every request and response with a timestamp. We need to decide how to format the timestamp and ensure that it is displayed with millisecond precision.

## Instructions to run
```aidl
* Open terminal
* Type java -jar and then drag and drop the TCPServer.jar in terminal
* Type java -jar and then drag and drop the TCPClient.jar in terminal
* For the port number, please kindly enter the same number for Server and Client. And for the hostname it could either be "localhost" or "127.0.0.1"

* Repeat the same steps for UDPServer and UDPClient jar files
```

