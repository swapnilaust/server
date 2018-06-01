/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.Server.clients;

/**
 *
 * @author swapn
 */
public class FriendRequestMessageHandler extends Thread{
    
    public void rewriteFriendRequest( ArrayList<String> allMsg )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("friend_request.txt", false));
        for( String i: allMsg ){
            System.out.println(i);
            writer.write( i );
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
    
    public void rewriteNotification( ArrayList<String> allMsg )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", false));
        for( String i: allMsg ){
            writer.write( i );
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
    
    public void rewriteMessage(ArrayList<String> allMsg)throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", false));
        for( String i: allMsg ){
            writer.write( i );
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
    
    void friend_request() throws IOException{
        boolean rw = false;
        
        ArrayList<String> allMsg = new ArrayList<String>();
        
        try {          
            FileReader inputFile = null;
            
            inputFile = new FileReader("friend_request.txt");
            
            Scanner parser = new Scanner(inputFile);
            
            while (parser.hasNextLine()){
                
                String line = parser.nextLine();
                            
                if( "".equals(line) )continue;
                            
                StringTokenizer tokens = new StringTokenizer( line, ":" );
                            
                String type = tokens.nextToken();
                            
                String from = tokens.nextToken();
                            
                String to = tokens.nextToken();
                            
                if( "0".equals(type) ){       
                    
                    for( ClientHandler i: clients ){

                        if( i.userName.equals(to) ){

                            type = "1";
                            
                            try {
                                DataOutputStream outToClient = new DataOutputStream(i.connectionSocket.getOutputStream() );
                                
                                outToClient.writeBytes("You got a friend request from " + from + '\n' );
                                
                            } catch (IOException ex) {
                                
                                Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            rw = true;
                        }
                    }
                }
                allMsg.add(type + ":" + from + ":" + to );
            }
                        
        } catch (FileNotFoundException ex) {
            
            Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if( rw == true ){
            rewriteFriendRequest( allMsg );
        }
    }
    
    void notifications() throws IOException{
        
        boolean rw = false;
        
        ArrayList<String> allNotification = new ArrayList<String>();

        FileReader inputFile = null;
        
        inputFile = new FileReader("notifications.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
                        
            StringTokenizer tokens = new StringTokenizer( line, ":" );
                            
            String type = tokens.nextToken();
                        
            String to = tokens.nextToken();
                        
            String notification = tokens.nextToken();
                        
            if( "0".equals(type) ){
                
                for( ClientHandler i: clients ){
                    
                    if( i.userName.equals(to) ){
                        
                        type = "1";
                        
                        try {
                            
                            DataOutputStream outToClient = new DataOutputStream(i.connectionSocket.getOutputStream() );
                            
                            outToClient.writeBytes("Notifications: " + notification + '\n' );
                            
                        } catch (IOException ex) {
                            
                            Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        rw = true;
                    }
                }
            }
            allNotification.add(type + ":" + to + ":" + notification );
        }
                    
        if( rw == true ){
            rewriteNotification( allNotification );
        }        
    }
    
    void message_sent() throws IOException {
        boolean rw = false;
        
        ArrayList<String> allMessage = new ArrayList<String>();

        FileReader inputFile = null;
        
        inputFile = new FileReader("message.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
                        
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String type = tokens.nextToken();
                        
            String from = tokens.nextToken();
            
            String to = tokens.nextToken();
                        
            String msg = tokens.nextToken();
            
            if( "0".equals(type) ){
                for( ClientHandler i: clients ){
                    
                    if( i.userName.equals(to) ){
                        
                        type = "1";
                        
                        try {
                            
                            DataOutputStream outToClient = new DataOutputStream(i.connectionSocket.getOutputStream() );
                            
                            outToClient.writeBytes("Message from " + from + " : " + msg +  '\n' );
                            
                        } catch (IOException ex) {
                            
                            Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        rw = true;
                    }
                }
            }
            allMessage.add(type + ":" + from + ":" + to + ":" + msg );
        }
        if( rw == true ){
            rewriteMessage( allMessage );
        }
    }
    
    public void run(){
       
        while( true ){
            try {
                friend_request();
                    
                notifications();
     
                message_sent();
                    
            } catch (IOException ex) {
                Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
