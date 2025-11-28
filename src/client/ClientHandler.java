package client;

import server.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue; // Ny import

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final BlockingQueue<ClientHandler> queue; // Ny variabel
    private Session session;
    private String playerName = "Okänd"; // Ny variabel

    public ClientHandler(Socket socket, BlockingQueue<ClientHandler> queue) throws IOException {
        this.socket = socket;
        this.queue = queue;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void send(String msg) {
        System.out.println("Skickar till klient (" + playerName + "): " + msg);
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            // 1. Läs namnet FÖRST
            String nameInput = in.readLine();
            if (nameInput != null) {
                this.playerName = nameInput;
            }
            System.out.println("Spelare ansluten med namn: " + playerName);

            // 2. I kön för att hitta match
            queue.add(this);

            // 3. Starta vanliga loopen
            handleMessages();
        } catch (IOException e) {
            System.err.println("Klient avkopplades: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleMessages() throws IOException {
        String msg;
        while ((msg = in.readLine()) != null) {
            if (session != null) {
                session.handleMessage(this, msg);
            }
        }
    }

    private void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Fel vid stängning: " + e.getMessage());
        }
    }
}