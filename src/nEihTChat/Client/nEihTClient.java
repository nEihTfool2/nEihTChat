package nEihTChat.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by ThiEn on 11.04.2017.
 */
public class nEihTClient  implements Runnable{
    @FXML private TextArea chat_protocol;
    @FXML private TextArea member_list;

    private static Socket clientSocket;
    private static PrintStream os;
    private static DataInputStream is;
    private String name;
    private static BufferedReader inputLine;
    private static boolean closed = false;
    private static int port = 1995;
    private static String ip = "127.0.0.1";


    public nEihTClient (String name, TextArea chat_protocol, TextArea member_list)
    {
        this.chat_protocol = chat_protocol;
        this.member_list = member_list;
        this.name = name;
        try {
            clientSocket = new Socket(ip, port);
    } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public void test() {

        try {
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream((clientSocket.getInputStream()));
            sendName(this.clientSocket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(clientSocket != null && os != null && is != null) {
            try {
                new Thread(this).start(); // Thread that reads from the server
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) throws IOException{
        PrintWriter printWriter =
                new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        printWriter.print(msg);
        printWriter.flush();
    }

    void sendName(Socket socket) throws IOException{
        PrintWriter printWriter =
                new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.print(this.name);
        printWriter.flush();
    }

    private String readMessage(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        char[] buffer = new char[256];
        int digitNumber = br.read(buffer, 0, 256); // blocks until message received
        String message = new String(buffer, 0, digitNumber);
        return message;
    }

    private String[] receiveContainer(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream os = new ObjectInputStream(socket.getInputStream());
        String[] rec = (String[])os.readObject();
        return rec;
    }


    private void sendContainer(Socket socket, String[] container) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(container);
    }

    public void run() {

        try {
            String[] response, entries;
            while(true)
            {
                response = receiveContainer(this.clientSocket);

                if (response[0].equals("setlbl")) {
                   entries = receiveContainer(this.clientSocket);
                   String members = "";

                   for (int i = 0; i < entries.length; i++) {
                       if(entries[i] == null){
                           break;
                       }
                       members = members + entries[i] + "\n";
                   }

                   member_list.setText(members);
                   chat_protocol.appendText(response[2]);
                } else {
                    chat_protocol.appendText(response[1]);
                    if (response[1].indexOf("You have left.") != -1) break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
