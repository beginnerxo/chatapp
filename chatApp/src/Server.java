import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
The Server class handles incoming client connections and manages multiple clients concurrently.
 */



public class Server {
    private ServerSocket serverSocket;
    private int activeCount = 0;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * StartServer method continuously accepts incoming client connections and creates a new thread for each client.
     * It also prints information about new connections and the current online count.
     */
    public void StartServer() {
        try {
            while (!serverSocket.isClosed()) {

                    ////Accept new client
                Socket socket = serverSocket.accept();
                activeCount++;

                //// Broadcast information about the new connection
                System.out.println("A new kid joined the server");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

                //// Print the active users/ online count
                System.out.println("Online: "+ activeCount);
            }

        } catch (IOException e){

        }

    }

    /**
     * closeServerSocket method closes the ServerSocket and prints a message indicating that the server has left the chat.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("has left the chat!");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////MAIN
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1434);
        Server server = new Server (serverSocket);
        server.StartServer();
    }
}
//End Of class

