package Client;

/**
 * Created by thien on 28.03.17.
 */
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void test() throws IOException {
        String ip = "127.0.0.1"; // localhost
        int port = 11111;

        Socket socket = new Socket(ip, port); // connects to server

        while (true) {
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();

        writeMessage(socket, message);

        String rec_message = readMessage(socket);
        System.out.println(rec_message);
        }
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
}