/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.Server.clients;

/**
 *
 * @author swapn
 */
public class LogInHandler extends Thread{
    
    public Socket connectionSocket;

    public LogInHandler( Socket clientSocket ) {
        this.connectionSocket = clientSocket;
    }
    
    boolean validData( String userName, String password ){
        
       FileReader inputFile = null;
       
        try {
            inputFile = new FileReader("accounts.txt");
            
            Scanner parser = new Scanner(inputFile);

            while (parser.hasNextLine())
            {
                String line = parser.nextLine();
                
                if( "".equals(line) )continue;

                StringTokenizer tokens = new StringTokenizer( line, ":" );
                        
                String uname = tokens.nextToken();

                String pass = tokens.nextToken();
                
                if( (uname == null ? userName == null : uname.equals(userName)) && pass.equals(password) ){
                    
                    return true;
                }                
            }
            return false;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputFile.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    boolean validData( String userName ){
        
        FileReader inputFile = null;
        
        try {
            inputFile = new FileReader("accounts.txt");
            
            Scanner parser = new Scanner(inputFile);

            while (parser.hasNextLine())
            {
                String line = parser.nextLine();
                
                System.out.println("all data " + line );
                
                if( "".equals(line) ) continue;

                StringTokenizer tokens = new StringTokenizer( line, ":" );
                        
                String uname = tokens.nextToken();
                
                if( (uname == null ? userName == null : uname.equals(userName)) ) return true;
                                
            }
            
            return false;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputFile.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    String getName(String userName ){
        
        String name = "";
        
        FileReader inputFile = null;
        
        try {
            inputFile = new FileReader("accounts.txt");
            
            Scanner parser = new Scanner(inputFile);

            while (parser.hasNextLine())
            {
                String line = parser.nextLine();
                
                System.out.println("all data " + line );
                
                if( "".equals(line) ){
                    continue;
                }

                StringTokenizer tokens = new StringTokenizer( line, ":" );
                        
                String uname = tokens.nextToken();
                
                String pass = tokens.nextToken();
                
                name = tokens.nextToken();
                
                if( uname == null ? userName == null : uname.equals(userName) ){
                    return name;
                }               
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputFile.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return name;
    }
    
    void addAccounts( String name, String userName, String password ) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt", true));
        writer.newLine();
        writer.append(userName + ":" + password + ":" + name );
        writer.close();
    }
    
    public void run(){
        try {
            
            BufferedReader inFromClient = new BufferedReader( new InputStreamReader(connectionSocket.getInputStream()));
            
            String operation = inFromClient.readLine();
            
            System.out.println("operaton " + operation );
            
            if( "login".equals(operation) ){
                
                String data = inFromClient.readLine();
                
                StringTokenizer tokens = new StringTokenizer( data, ":" );

                String userName = tokens.nextToken();

                String password = tokens.nextToken();
                
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream() );
                
                if( validData(userName, password ) ){
                    
                    outToClient.writeBytes("ok" +  '\n' );
                    
                    String name = getName( userName );
                    
                    ClientHandler newclient = new ClientHandler(connectionSocket, name, userName);

                    newclient.start();
                    
                    clients.add( newclient );
                    
                }else{
                    outToClient.writeBytes("failed" +  '\n' );
                }
            }else if( "register".equals(operation) ){
                
                System.out.println("register e asche");
                
                String data = inFromClient.readLine();
                
                StringTokenizer tokens = new StringTokenizer( data, ":" );
                
                String userName = tokens.nextToken();

                String password = tokens.nextToken();
                
                String name = tokens.nextToken();
                
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream() );
                
                if( validData( userName ) ){
                    
                    outToClient.writeBytes("failed" +  '\n' );
                    
                }else{
                    
                    addAccounts(name, userName, password );
                    
                    outToClient.writeBytes("ok" +  '\n' );
                    
                    ClientHandler newclient = new ClientHandler(connectionSocket, name, userName);
                    
                    clients.add( newclient );
                    
                    newclient.start();
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(LogInHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
  
}
