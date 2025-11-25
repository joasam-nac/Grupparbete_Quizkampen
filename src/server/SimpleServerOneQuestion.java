package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServerOneQuestion {

    public static final int PORT = 12345;

    public static void main(String[] args) {

        System.out.println("SimpleServerOneQuestion startar...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Socket c = serverSocket.accept();
            System.out.println("Klient ansluten!");

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(c.getOutputStream())
            );

            // ---- FRÅGAN + 4 ALTERNATIV ----
            out.write("Vad är 2+2?");
            out.newLine();
            out.write("3");
            out.newLine();
            out.write("4");
            out.newLine();
            out.write("5");
            out.newLine();
            out.write("22");
            out.newLine();

            out.flush();

            System.out.println("Fråga + 4 svar skickade.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}