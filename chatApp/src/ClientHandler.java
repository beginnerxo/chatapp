import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * The ClientHandler class manages communication with a specific client, handling input and output streams.
 */


public class ClientHandler implements Runnable{


    //Setting up input and Output streams for communication.
    public static ArrayList < ClientHandler > clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public static int onlineUsers = 0;

    private String clientUsername;

    public void welcomeMessage(int numofUsers){
        broadcastMessage(onlineUsers+" online!");
    }
    public ClientHandler (Socket socket ){
        try{

            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            // Increment the online users count and send a welcome message
            onlineUsers++;
            welcomeMessage(onlineUsers);
            //broadcastMessage("Welcome to the chat"+clientUsername+"! "+onlineUsers+" online");

            broadcastMessage("SERVER: "+  clientUsername.toLowerCase()+" has entered the chat!");

        }catch (IOException e){
            closeEverything (socket, bufferedReader, bufferedWriter);
        }

    }
    private void sendWelcomeMessage(String welcomeMessage){
        try {
            bufferedWriter.write(welcomeMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }


    }




    @Override
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient == null) {
                    break;
                }

                broadcastMessage(messageFromClient);
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } finally {
            // Call removeClientHandler when the connection is closed
            removeClientHandler();
        }
    }


    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try{


            if (messageToSend != null && !clientHandler.clientUsername.equals(clientUsername)){
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything (socket , bufferedReader, bufferedWriter);


            }
        }
    }


    public void  removeClientHandler(){
        clientHandlers.remove(this );
        onlineUsers =  onlineUsers -1;
        broadcastMessage( "SERVER: "+ clientUsername.toLowerCase()+" has  left the chat");
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter ){
        removeClientHandler();
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
}
