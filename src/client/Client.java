package client;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    static void main() {
        try (Socket socket = new Socket(HOST, PORT)) {
            BufferedReader serverInput = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter serverOutput = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            Thread inputThread = startKeyboardInput(serverOutput);

            receiveMessages(serverInput);
            inputThread.interrupt();

        } catch (IOException e) {
            System.err.println("Fel: " + e.getMessage());
        }
    }

    private static Thread startKeyboardInput(PrintWriter output) {
        Thread thread = new Thread(() -> {
            try (BufferedReader keyboard = new BufferedReader(
                    new InputStreamReader(System.in)
            )) {
                String input;
                while ((input = keyboard.readLine()) != null) {
                    output.println(input);
                }
            } catch (IOException e) {
                System.err.println("Fel vid inmatning: " + e.getMessage());
            }
        });
        thread.start();
        return thread;
    }

    private static void receiveMessages(BufferedReader input)
            throws IOException {
        String response;
        while ((response = input.readLine()) != null) {
            System.out.println("Server: " + response);
        }
    }
}