package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
    Klassen är en simpel version av klienten som bara kan ansluta till en server
    och kan kommunicera med serven
*/

public class ClientHandler {
    private final Socket socket;
    private final PrintWriter out;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String msg){
        out.println(msg);
    }

    static void main() {
        try (Socket socket = new Socket("localhost", 5000)){
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Ansluten till server. Väntar på motspelare");

            String line;
            while((line = in.readLine()) != null){
                System.out.println("Server: " + line);
            }
        } catch (IOException e) {
            System.out.println("Klientfel: " + e.getMessage());
        }
    }
}
