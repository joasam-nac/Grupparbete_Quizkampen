package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchmakeHandler {
    private final ServerSocket server;
    private final BlockingQueue<ClientHandler> waitingClients =
            new LinkedBlockingQueue<>();
    private final QuestionRepository questionRepo;
    private final List<Session> activeSessions = new ArrayList<>();

    public MatchmakeHandler(int port, String questionsFile) throws IOException {
        server = new ServerSocket(port);
        questionRepo = new QuestionRepository(questionsFile);
    }

    public void start() {
        new Thread(this::acceptLoop).start();
        new Thread(this::matchmakingLoop).start();
    }

    private void acceptLoop() {
        while (true) {
            try {
                Socket socket = server.accept();
                ClientHandler clientHandler =
                        new ClientHandler(socket, waitingClients, this);
                new Thread(clientHandler).start();

            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    private void matchmakingLoop() {
        while (true) {
            try {
                ClientHandler first = waitingClients.take();
                ClientHandler second = waitingClients.take();

                Session session = new Session(first, second, questionRepo);

                first.setSession(session);
                second.setSession(session);

                synchronized (activeSessions) {
                    activeSessions.add(session);
                }

                System.out.println("Session " + session.getId() + " started");
                session.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public synchronized void handleSessionEnd(
            Session session,
            ClientHandler disconnectedClient
    ) {
        synchronized (activeSessions) {
            if (!activeSessions.remove(session)) {
                return; // redan snyggt
            }
        }

        System.out.println("Session " + session.getId() + " ending");

        // visar till spelare
        session.notifyDisconnect(disconnectedClient);

        // rensar timer
        session.cleanup();
    }

    public static void main(String[] args) throws IOException {
        String questionsFile = args.length > 0 ? args[0] : "questions.txt";
        int port = 5000;

        MatchmakeHandler m = new MatchmakeHandler(port, questionsFile);
        m.start();
        System.out.println("Server running on port " + port);
    }
}