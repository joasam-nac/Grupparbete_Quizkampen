package client;

import server.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private Session session;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(
                socket.getOutputStream(),
                true
        );
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void send(String msg) {
        System.out.println("Skickar till klient: " + msg);
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            System.out.println("ClientHandler kör för " + socket);
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
            } else {
                System.err.println("Session inte initialiserad för " +
                        socket);
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