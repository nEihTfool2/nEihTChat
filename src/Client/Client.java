package Client;

/**
 * Created by thien on 28.03.17.
 */
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Client implements Runnable{

    private String name;
    String ip = "127.0.0.1"; // localhost
    int port = 1995;

    public Client (String name) { this.name = name; }

    void test() throws IOException {
        Socket socket = new Socket(ip, port);
        //sendName(socket);

        new Thread().start();

        while (true) {
        Scanner sc = new Scanner(System.in);
        System.out.println("You: ");
        String message = sc.nextLine();

        writeMessage(socket, message);
        }
    }

    void sendName(Socket socket) throws IOException{
        PrintWriter printWriter =
                new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.print(this.name);
        printWriter.flush();
    }

    void writeMessage(Socket socket, String message) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.print(message);
        printWriter.flush();
    }

    String readMessage(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[256];
        int digitNumber = bufferedReader.read(buffer, 0, 256); //blocks until message received
        String message = new String(buffer, 0, digitNumber);
        return message;
    }

    void receive() throws IOException{
        Socket socket = new Socket(ip, port);

        String rec_message = readMessage(socket);
        System.out.println(rec_message);
    }

    public void run() {
        try {
            while (true) {
                receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
