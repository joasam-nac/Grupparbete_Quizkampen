package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class serverMain {
    static int PORT = 12345;
    static void main() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Lyssnar på port " + PORT);

            Socket c = serverSocket.accept();
            System.out.println(c.getInetAddress().getHostName() + " klient ansluten");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String question = "Kan du se detta meddelande?";
            String correctAnswer = "ja";

            out.write(question);
            out.newLine();
            out.flush();

            String answer = in.readLine();

            if(answer.equals(correctAnswer)){
                out.write("Rätt!");
            } else {
                out.write("Fel!");
            }

            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
