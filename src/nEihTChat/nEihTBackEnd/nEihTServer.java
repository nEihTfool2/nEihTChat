package nEihTChat.nEihTBackEnd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * Created by ThiEn on 11.04.2017.
 */

public class nEihTServer implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket;
    private boolean stopped = false;
    private static Socket clientSocket;

    private final int max_socket = 8;

    Client[] room = new Client[max_socket];

    public nEihTServer(int port) {
        this.serverPort = port;
    }

    public void run() {
        int c = 0;
        openSocket();
        System.out.println("Start Server...");
        while (!(this.stopped)) {
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("Found client!");

                if(c == max_socket) {
                    System.out.println("Room is full!");
                        toFull(clientSocket);
                        clientSocket.close();
                }
                if (room[c] == null){
                    (room[c] = new Client(clientSocket, room)).start();
                    System.out.println("Client connected");
                    c++;
                }

            } catch (IOException e) {
                if (this.stopped) {
                    System.out.println("Server stopped");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }

        }

        System.out.println("Server stopped");
    }

    private void toFull(Socket socket) throws IOException {
        OutputStreamWriter a = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter b = new PrintWriter(a);

        PrintWriter printWriter = new PrintWriter(b);
        printWriter.print("Server too busy, try again later");
        printWriter.flush();
    }


    private void openSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Can't open port " + this.serverPort, e);
        }
    }
}

class Client extends Thread {
    private DataInputStream is;
    private PrintStream os = null;
    private Socket client;
    private int maxClients;
    private final Client[] clients;

    public Client(Socket client, Client[] clients) {
        this.client = client;
        this.clients = clients;
        maxClients = clients.length;
    }

        public void run() {
            int maxClients = this.maxClients;
            Client[] clients = this.clients;

               try
               {
                   is = new DataInputStream(client.getInputStream());
                   os = new PrintStream(client.getOutputStream());

                   // Client's name
                   String name = readMessage(this.client);
                   System.out.println(name + " has entered the room.");

                   for (int i = 0; i < maxClients; i++) {
                       if (clients[i] != null) {
                           sendMessage(clients[i].client, "setlbl" + name + " has joined the room.\n");
                       }
                   }
                   // reading message from client and sending it to other clients in the room
                   while(true) {
                       String line = readMessage(this.client);

                       if (line.startsWith(":q")) break;

                       System.out.println(name + ": " + line);

                       // Sending the message to the chat room/everyone
                       for (int i = 0; i < maxClients; i++){
                           if(clients[i] != null && clients[i] != this) {
                               sendMessage(clients[i].client, line);
                               //clients[i].os.println( name + ": " + line);
                           }
                       }
                   }
                   os.println("You have left.");

                   // Clean up
                   for (int i = 0; i < maxClients; i++){
                       if (clients[i] == this) {
                           clients[i] = null;
                       }
                   }

                   is.close();
                   os.close();
                   client.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
        }


    private String readMessage(Socket socket) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            char[] buffer = new char[256];
            int digitNumber = br.read(buffer, 0, 256); // blocks until message received
            String message = new String(buffer, 0, digitNumber);
            return message;
        }

    public void sendMessage(Socket client, String msg) throws IOException{
        PrintWriter printWriter =
                new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
        printWriter.print(msg);
        printWriter.flush();
    }

}

