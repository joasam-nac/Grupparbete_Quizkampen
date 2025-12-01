package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final BlockingQueue<ClientHandler> queue;
    private final MatchmakeHandler matchmaker;
    private Session session;
    private String playerName = "Okänd";

    public ClientHandler(
            Socket socket,
            BlockingQueue<ClientHandler> queue,
            MatchmakeHandler matchmaker
    ) throws IOException {
        this.socket = socket;
        this.queue = queue;
        this.matchmaker = matchmaker;
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
            String nameInput = in.readLine();
            if (nameInput != null) {
                this.playerName = nameInput;
            }
            System.out.println("Spelare ansluten med namn: " + playerName);

            queue.add(this);
            handleMessages();
        } catch (IOException e) {
            System.err.println("Klient avkopplades: " + e.getMessage());
        } finally {
            handleDisconnect();
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

    private void handleDisconnect() {
        queue.remove(this);
        if (session != null) {
            matchmaker.handleSessionEnd(session, this);
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