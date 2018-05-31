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
    public int operations;

    public FriendRequestMessageHandler(int operations) {
        this.operations = operations;
    }
    
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
    
    public void run(){
        if( operations == 1 ){
            while( true ){
                boolean rw = false;
                try {
                    
                    //friend request
                    
                    ArrayList<String> allMsg = new ArrayList<String>();
                    try {
                        
                        FileReader inputFile = null;
                        inputFile = new FileReader("friend_request.txt");
                        Scanner parser = new Scanner(inputFile);
                        while (parser.hasNextLine())
                        {
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
                            if( !"0".equals(type) ){
                                //System.out.println("change hoise ");
                            }
                            allMsg.add(type + ":" + from + ":" + to );
                            
                        }
                        
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if( rw == true ){
                        rewriteFriendRequest( allMsg );
                    }
                    
                    //notifications
                    ArrayList<String> allNotification = new ArrayList<String>();
                    rw = false;
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
                        
                    
                } catch (IOException ex) {
                    Logger.getLogger(FriendRequestMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
}
