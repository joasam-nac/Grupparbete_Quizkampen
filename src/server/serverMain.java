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

            questionManager quiz = new questionManager(3);
            out.write("Välj område: " + quiz.getQuestionThemes());
            out.newLine();
            out.flush();

            String theme = in.readLine();
            int score = quiz.runQuestionGame(theme, in, out);

            out.write("Du fick " + score + " poäng!");
            out.newLine();
            out.flush();

        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
