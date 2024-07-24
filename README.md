# chat-app-java
This app consist of a server and a client,
server handles multiple clients using multithreading, 
and broadcasts messages to all connected clients, 
provides a GUI (Swing) for users to send and receive messages, and displays chat history.

The server and client communicate over a socket connection which uses two classes :
The ServerSocket class used by the server to listen for incoming client connections on a specified port (5000 in this case).
The Socket class used by both the server and the clients to establish and manage connections.

References : Head First Java 2nd Edition 
