/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static server.Server.clients;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.Server.clients;

/**
 *
 * @author swapn
 */
public class ClientHandler extends Thread{
    /**
     * @param args the command line arguments
     */
    
    public  Socket connectionSocket;
    public  String clientName;
    public  String userName;

    public ClientHandler( Socket connectionSocket, String clientName, String userName ) {
        this.connectionSocket = connectionSocket;
        this.clientName = clientName;
        this.userName = userName;
    }
    
    void addFrineds( String friendName )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("friends_list.txt", true));
        writer.write( userName + ":" + friendName );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    void addFrinedRequest( String friendName )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("friend_request.txt", true));
        writer.write( "0:" + userName + ":" + friendName );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    void sendConfirmation( String name ) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true));
        writer.write( "0:" + name + ":" + userName + " Accecpted Your Friend Request."  );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    void sendMessage( String name, String msg )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        writer.write( "0:" + userName + ":" + name + ":" + msg  );
        writer.newLine();
        writer.flush();
        writer.close();
    }

    
    public void run(){
        
            System.out.println(clientName);
        
            try {
                while( true ){
                    
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream() );
                    
                    BufferedReader inFromClient = new BufferedReader( new InputStreamReader(connectionSocket.getInputStream()));

                    String operation = inFromClient.readLine();
                    
                  
                    
                    if( "Online User Lists".equals(operation)){
                        
                        outToClient.writeBytes("Online User Lists" + '\n' );

                         ArrayList<String> allUsers = new ArrayList<String>();
                        for( ClientHandler i: clients ){
                            String names =  "Name : " + i.clientName + ", UserName : " + i.userName ;
                            System.out.println("Server theke jacce " + names );
                            allUsers.add( names );
                        }
                        
                        ObjectOutputStream objectOutput = new ObjectOutputStream(connectionSocket.getOutputStream());
                        
                        objectOutput.writeObject(allUsers);
                        
                    }else if("Accecpt Friend Request".equals(operation) ){
                        System.out.println("asche balsal");
                        String name = inFromClient.readLine();
                        System.out.println("name " + name);
                        addFrineds( name );
                        outToClient.writeBytes("You are now friend with " + name + '\n' );
                        sendConfirmation( name );
                    }else if( "Send Friend Request".equals(operation) ){
                        String name = inFromClient.readLine();
                        addFrinedRequest( name );
                    }else if( "Unicast".equals(operation) ){
                        String user = inFromClient.readLine();
                        String msg = inFromClient.readLine();
                        sendMessage( user, msg );
                        outToClient.writeBytes("Message has been sent."  + '\n' );
                    }

                    //StringTokenizer tokens = new StringTokenizer( operation, ":" );

                    //String msg = tokens.nextToken();

                    //String client = tokens.nextToken();

                    //System.out.println( msg + " " + client ) ;
                    
                    //System.out.println( "Send msg from " + clientName + " to " + client ) ;
                    
                    

//                    for( ClientHandler c: clients ){
//                        if(  c.clientName.equals(client) ){
//
//                            DataOutputStream outTo = new DataOutputStream(c.connectionSocket.getOutputStream() );
//
//                            outTo.writeBytes("From " + clientName + " : " + msg +  '\n' );
//                        }
//                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
}
