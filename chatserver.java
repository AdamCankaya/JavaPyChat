// Adam Cankaya, cankayaa@onid.oregonstate.edu
// CS 372 Project 1
// Due Feb 8, 2015

// References: 
//  Boiler plate socket code from CS372 class lecture videos
//  EOL character help from https://norwied.wordpress.com/2012/04/17/how-to-connect-a-python-client-to-java-server-with-tcp-sockets/

// Client-server turn based chat program
//   Run on flip.engr.oregonstate.edu
//   Compile Java server: javac chatserver.java
//   Run Java server: java chatserver <PORTNUM>
//   Run Python server: python chatserver.py <PORTNUM>
//   Run Python client: python chatclient.py <HOST> <PORTNUM>
//   Send '\quit' to end connection and close client
//   Send SIG_INT (Ctrl+C) to shut down server

import java.net.*;
import java.io.*;
import java.util.*;

public class chatserver {

    // Server properties
    public static String handle = "MrServer> ";
    public static int serverPort = 0;
    // Socket variables
    public static ServerSocket serverSocket = null;
    public static Socket clientSocket = null;
    // I/O variables
    public static BufferedReader in = null;
    public static PrintWriter out = null;
    public static Scanner in_scan = null;
    public static String clientInput, serverInput;
    // Other variables
    public static boolean loop = true;
    
    // Initialize shutdown protocol and get server port from command line
    public static void setupServer(String[] args) {
        // Install shutdown hook MyShutdown
        MyShutdown sh = new MyShutdown();
        Runtime.getRuntime().addShutdownHook(sh);
        
        // Get server port
        in_scan = new Scanner(System.in);
        serverPort = Integer.parseInt(args[0]);
        
        return;
    }
    
    // Start server and wait for client to initiate connection then create socket and exchange introduction
    public static void startServer() {
        try {
        System.out.println(handle + "Waiting on client to connect on port " + serverPort);
        serverSocket = new ServerSocket(serverPort);
        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));      // default buffer > 512 chars
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Listen for first message from client, print it, send first message to client, print it
        clientInput = in.readLine();
        System.out.println(clientInput);
        out.println(handle + "I'm ready to chat on port " + serverPort + "!");
        System.out.println(handle + "I'm ready to chat on port " + serverPort + "!");
        }
        catch(IOException e) {
         //   System.out.println("Exception caught when trying to listen on port " + serverPort);
         //   System.out.println(e.getMessage()); 
        }

        return;
    }

    // Wait for a client message and then print it
    public static void getMessage() {
        try {
            clientInput = in.readLine();
            System.out.println(clientInput);
            // Check if client quit
            if(clientInput.contains("Connection closed by client")) {
                loop = false;   // break main loop
            }
        }
        catch(Exception e) {
         //   System.out.println("Exception caught while trying to close sockets.");
         //   System.out.println(e.getMessage());
        }

        return;
    }
    
    // Get message from server user and send it to client
    public static void sendMessage() {
        // Query server for message, check if server quit, otherwise print their message
        System.out.print(handle);
        serverInput = in_scan.nextLine();
        if(serverInput.contains("\\quit")) {
            out.println(handle + "Connection closed by server.");
            System.out.println(handle + "Connection closed by server.");
            loop = false;       // break main loop
        }
        else {
            out.println(handle + serverInput);
        }

        return;
    }
    
    // Be sure all sockets and buffers are closed
    public static void closeEverything() {
        try {
            // Be sure all sockets and buffers are closed
            if(serverSocket != null)
                serverSocket.close();
            if(clientSocket != null)
                serverSocket.close();
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
        catch(Exception e) {
            System.out.println("Exception caught while trying to close sockets.");
            System.out.println(e.getMessage());
        }

        return;
    }
    
    // Close all sockets & buffers and shutdown server if interupt signal (Ctrl+C) is received    
    public static class MyShutdown extends Thread {
        public void run() {
            System.out.println("\nServer shutting down.");
            if(out != null)
                out.println(handle + "Connection closed by server.");

            closeEverything();
        }
    }
    
    public static void main(String[] args) throws IOException {
        setupServer(args);
        while(loop) {   // loop until SIG_INT
            try {
                startServer();
                
                while(loop) {   // loop until client or server quits
                    
                    // Get and print client's message
                    getMessage();
                    // Check if client quit - if so, restart inner while loop
                    if(!loop) {
                        loop = true;    // continue outter while loop
                        break;
                    }
                    
                    // Get and send server's message
                    sendMessage();
                    // Check if server quit - if so, restart inner while loop
                    if(!loop) {
                        loop = true;    // continue outter while loop
                        break;
                    }
                }
            }
            catch(Exception e) {
                System.out.println("Exception caught when trying to listen on port " + serverPort);
                System.out.println(e.getMessage());
                break;
            }
            finally {
                closeEverything();
            }
        }
    }
}