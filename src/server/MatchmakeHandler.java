package server;

import client.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchmakeHandler {
    private final ServerSocket server;
    private final BlockingQueue<ClientHandler> waitingClients =
            new LinkedBlockingQueue<>();
    private final ThemeValidator validator =
            new ThemeValidator(Set.of("Tema1", "Tema2", "Tema3", "Tema4"));
    private final List<Session> activeSessions = new ArrayList<>();

    public MatchmakeHandler(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public void start() {
        new Thread(this::acceptLoop).start();
        new Thread(this::matchmakingLoop).start();
    }

    private void acceptLoop() {
        while (true) {
            try {
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
                waitingClients.add(clientHandler);
            } catch (IOException e) {
                System.err.println("Fel vid att ta emot klient: " +
                        e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    private void matchmakingLoop() {
        while (true) {
            try {
                ClientHandler first = waitingClients.take();
                ClientHandler second = waitingClients.take();

                Session session = new Session(first, second, validator);

                first.setSession(session);
                second.setSession(session);

                activeSessions.add(session);

                System.out.println("Session " + session.getId() +
                        " startades");
                System.out.println("Aktiva sessioner " +
                        activeSessions.size());

                session.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    static void main() throws IOException {
        MatchmakeHandler m = new MatchmakeHandler(5000);
        m.start();
        System.out.println("Kör server på port 5000");
    }
}