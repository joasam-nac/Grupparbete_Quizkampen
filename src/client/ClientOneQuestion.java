package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientOneQuestion {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Ansluten till servern!");
            System.out.println("--- Tar emot fråga ---");

            String question = in.readLine();
            String a1 = in.readLine();
            String a2 = in.readLine();
            String a3 = in.readLine();
            String a4 = in.readLine();

            System.out.println();
            System.out.println(question);
            System.out.println("1) " + a1);
            System.out.println("2) " + a2);
            System.out.println("3) " + a3);
            System.out.println("4) " + a4);

        } catch (IOException e) {
            System.out.println("Kunde inte ansluta till servern.");
            e.printStackTrace();
        }
    }
}