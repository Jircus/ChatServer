/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.Message;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jircus
 */
public class ServerThread extends Thread {
    
    private Server server;
    private Socket socket;
    private String name;
    
    public ServerThread(Server server, Socket socket) {       
        this.server = server;
        this.socket = socket;
        
        start();
    }

    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message message = (Message)input.readObject();             
                System.out.println("Sending "+ message.getMessage());              
                server.sendToAll(message);
                name = message.getName();
            }
        }
        catch(EOFException e) {
            System.out.println("e");
        }
        catch(IOException e) {
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                server.sendToAll(new Message("Uživatel " + name + " se odpojil", ""));
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            server.removeConnection(socket);
        }
    }
    
}
