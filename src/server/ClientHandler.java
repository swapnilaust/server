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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    
    boolean isFriend( String user1, String user2 )throws IOException{
        
        FileReader inputFile = null;
        
        inputFile = new FileReader("friends_list.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
            
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String first = tokens.nextToken();
                        
            String second = tokens.nextToken();
            
            if( first.equals(user1) && second.equals(user2) ){
                
                return true;
                
            }else if( second.equals(user1) && first.equals(user2) ){
                
                return true;
            }
            
            
        }
        return false;
   
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
        StringTokenizer tokens = new StringTokenizer(name,":");
        while (tokens.hasMoreTokens()) {
            String next = tokens.nextToken();
            if( !isFriend( userName, next ) )continue;
            writer.write( "0:" + userName + ":" + next + ":" + msg  );
            writer.newLine(); 
        } 
        writer.flush();
        writer.close();
    }
    
    void sendMessage( String name, String msg, boolean isChat )throws IOException{
        
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        StringTokenizer tokens = new StringTokenizer(name,":");
        while (tokens.hasMoreTokens()) {
            String next = tokens.nextToken();
            if( !isFriend( userName, next ) && !isChat )continue;
            writer.write( "0:" + userName + ":" + next + ":" + msg  );
            writer.newLine(); 
        } 
        writer.flush();
        writer.close();
    }
    
    void createChatRoom( String name )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("chat_list.txt", true));
        writer.write(  name + ":" + userName );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    void broadcasting(String msg )throws IOException{
        
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        
        FileReader inputFile = null;
        
        inputFile = new FileReader("friends_list.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
            
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String first = tokens.nextToken();
                        
            String second = tokens.nextToken();
            
            if( first.equals(userName) ){
                
                writer.write( "0:" + first + ":" + second + ":" + msg  );
                
                writer.newLine();
                
            }else if( second.equals(userName) ){
                
                writer.write( "0:" + second + ":" + first + ":" + msg  );
                
                writer.newLine();
            }
        }
        
        writer.flush();
        writer.close();
    }
    
    void removeUser(){
        for( ClientHandler i: clients ){
            if( i.userName.equals(userName) ){
                clients.remove(i);
                break;
            }
        }
    }
    
    boolean findChatRoom( String name )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        
        FileReader inputFile = null;
        
        inputFile = new FileReader("chat_list.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
            
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String cname = tokens.nextToken();
            
            if( cname.equals(name) ){
                
                return true;
            }
        }
        
        return false;
    }
    
    void sendToChatRoom( String name, String msg )throws IOException{
        
        FileReader inputFile = null;
        
        inputFile = new FileReader("chat_list.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
            
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String cname = tokens.nextToken();
            
            if( cname.equals(name) ){
                
                while (tokens.hasMoreTokens()){
                    
                    String fnd = tokens.nextToken();
                    
                    if( fnd.equals(userName) ) continue; // comment uthaite hobe
                    
                    sendMessage( fnd, "From chat romm " + name + " -> " + msg, true );
                }
                    
            }
        }
    }
    
    void rewriteChatList( ArrayList<String> allMsg )throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("chat_list.txt", false));
        for( String i: allMsg ){
            writer.write( i );
            writer.newLine();
        }
        writer.flush();
        writer.close();
        
    }
    
    void addFriendToRoom( String chatName, String fname )throws IOException{
        
        ArrayList<String> allMsg = new ArrayList<String>();
        
        FileReader inputFile = null;
        
        inputFile = new FileReader("chat_list.txt");
        
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine()){
            
            String line = parser.nextLine();
            
            if( "".equals(line) )continue;
            
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            
            String cname = tokens.nextToken();
            
            if( cname.equals(chatName) ){
                line += ":" + fname;
            }
            
            allMsg.add( line );
        }
        
        rewriteChatList(allMsg);
    }
    

    
    public void run(){
        
            System.out.println(clientName);
        
            try {
                while( true ){
                    
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream() );
                    
                    BufferedReader inFromClient = new BufferedReader( new InputStreamReader(connectionSocket.getInputStream()));

                    String operation = inFromClient.readLine();
                    
                    System.out.println("opp " + operation);

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
                        
                        //outToClient.writeBytes("Message has been sent."  + '\n' );
                        
                    }else if( "Multicast".equals(operation) ){
                        
                        String user = inFromClient.readLine();
                        
                        String msg = inFromClient.readLine();
                        
                        sendMessage( user, msg );
                        
                        //outToClient.writeBytes("Message has been sent."  + '\n' );
   
                    }else if( "Broadcast".equals(operation) ){
                        
                        String msg = inFromClient.readLine();
                        
                        broadcasting( msg );
                        
                        //outToClient.writeBytes("Message has been sent."  + '\n' );
                    }else if( "log out".equals(operation) ){
                        
                        System.out.println("asche logout");
                        
                        removeUser();
                        
                        outToClient.writeBytes("Successfullt Logged Out."  + '\n' );
                        
                        break;
                        
                        
                    }else if( "Create Chat Room".equals(operation) ){
                        
                        String name = inFromClient.readLine();
                        
                        createChatRoom(name);
                        
                        outToClient.writeBytes("Created chat Room : "  + name +  '\n' );
                        
                    }else if( "Open Chat Room".equals(operation) ){
                        
                        String name = inFromClient.readLine();
                        
                        if( findChatRoom( name ) ){
                            
                            System.out.println("asche chat");

                            String msg = inFromClient.readLine();
                            
                            System.out.println("msg : " + msg   );
                            
                            while( !"Exit Chat Room".equals(msg) ){
                                
                                System.out.println("chat kori!");
      
                                sendToChatRoom( name, msg );
                                
                                msg = inFromClient.readLine();
                                
                                System.out.println("msg : " + msg   );
                            }
                            System.out.println("Chat Room Closed!" );
                            outToClient.writeBytes("chat room closed!" + '\n' );
                            
                        }else{
                            outToClient.writeBytes("Chat Room not found!" + '\n' );
                        }
                        
                    }else if( "Add Friend to Chat Room".equals(operation) ){
                       
                        
                        String cname = inFromClient.readLine();
                        
                        String name = inFromClient.readLine();
                        
                        System.out.println("aaa " + cname + " " + name  );
                        
                        addFriendToRoom( cname, name );
                        
                        outToClient.writeBytes(name + " added to " + cname + '\n' );
                        
                    }
                    

                }

            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

    }
    
}
