import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/////CLIENT OR TEST CODE


public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    //// Constructor to initialise the client with the Username and The Socket
    public Client(Socket socket, String username){
        try{

            ////Setting up input and output streams for communication
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything (socket, bufferedReader, bufferedWriter);
        }
    }


    // Method for sending the message to the server to be broadcasted
    public void sendMessage(){
        try{
            //// The following 3 lines send the username to the server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scnr = new Scanner (System.in);

            // Continuouslly reading user input while the server is connected.
            while (socket.isConnected()){
                String messageToSend = scnr.nextLine();
                bufferedWriter.write(username.toLowerCase() + " :"+ messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        }catch (IOException e){
            closeEverything (socket, bufferedReader,bufferedWriter);
        }
    }


    public void listenForMessage(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               String msgFromGroupChat;

               while(socket.isConnected()){
                   try{
                       msgFromGroupChat = bufferedReader.readLine();
                       if (msgFromGroupChat == null) {
                           break;
                       }
                       System.out.println(msgFromGroupChat);
                   }catch (IOException e){
                       closeEverything(socket, bufferedReader,bufferedWriter);
                   }
               }
           }
       }).start();
    }


    //// Method for closing all opened resources
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null ){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    ///MAIN method, initializing the client

    public static void main(String[] args) throws IOException {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Enter your nickname to join server?");
        String username = scnr.nextLine();


        //Creating a socket and initialize the client

        Socket socket = new Socket("localhost",1434);
        Client client = new Client(socket,username);

        // Listening for messages and allowing the user to send messages as well.
        client.listenForMessage();
        client.sendMessage();

    }
}
