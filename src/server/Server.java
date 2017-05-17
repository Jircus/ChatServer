/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import protocol.Message;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author Jircus
 */
public class Server {
    
    private HashMap sockets;
    
    public Server(int port) throws IOException {
        sockets = new HashMap();
        listen(port);
    }
    
    private void listen(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);       
        System.out.println("Listening on " + serverSocket);
        
        while (true) {           
            Socket socket = serverSocket.accept();            
            System.out.println("Connection from " + socket);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            sockets.put(socket, output);
            System.out.println("The are currently " + sockets.size() + " conected users");
            new ServerThread(this, socket);
        }
    }
    
    public void sendToAll(Message message) throws IOException {
        synchronized(sockets) {
            message.setUserCount(sockets.size());
            for (Object obj : sockets.values()) {
                try {
                    ObjectOutputStream output = (ObjectOutputStream)obj;
                    output.writeObject(message);
                }
                catch(IOException ie) {
                    System.out.println(ie); 
                }
            }
        }
    }

    void removeConnection(Socket s) {
        synchronized(sockets) {
            sockets.remove(s);
            try {
                System.out.println(s + " has been closed");
                s.close();
                System.out.println("The are currently " + sockets.size() + " conected users");
            }
            catch( IOException ie ) {
                System.out.println("Error closing " + s);
                ie.printStackTrace();
            }
        }
    }
}
