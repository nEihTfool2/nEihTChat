package Threaded_Server.nEihTBackend;

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
                    (room[c] = new Client(clientSocket, c, room)).start();
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
    private final int index;

    public Client(Socket client, int index, Client[] clients) {
        this.index = index;
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
                   while(true) {

                       String line = is.readLine();

                       for (int i = 0; i < maxClients; i++){
                           if(clients[i] != null && clients[i] != this) {
                               clients[i].os.println("client " + this.index + ": " + line);
                           }
                       }
                   }
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
}

