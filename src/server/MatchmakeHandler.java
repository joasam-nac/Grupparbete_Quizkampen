package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import client.ClientHandler;

/*
    Hanterar endast att två klienter paras ihop
 */

public class MatchmakeHandler {
    private final ServerSocket server;
    // Skapar en kölista som kan spara uppkopplade klienter
    private final BlockingQueue<ClientHandler> waitingClients = new LinkedBlockingQueue<>();

    public MatchmakeHandler(int port) throws IOException {
        server = new ServerSocket(port);
    }

    /*
        acceptLoop väntar på nyanslutna klienter, alltså inkommande anslutningar
    matchmakerLoop väntar på minst två anslutningar och parar ihop de två och två, just nu slumpvist
     */
    public void start() {
        new Thread(this::acceptLoop).start();
        new Thread(this::matchmakingLoop).start();
    }

    private void acceptLoop() {
        while (true) {
            try {
                Socket socket = server.accept();
                waitingClients.add(new ClientHandler(socket));
            } catch (IOException e) {
                System.err.println("Fel vid att ta emot klient: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    private void matchmakingLoop() {
        Random random = new Random();

        while (true) {
            try {
                ClientHandler first = waitingClients.take();
                ClientHandler second = waitingClients.take();

                //hanterar slumpmässiga motståndare
                if (random.nextBoolean()){
                    ClientHandler temp = first;
                    first = second;
                    second = temp;
                }

                first.send("Spelare 1 hittade en motspelare");
                second.send("Spelare 2 hittade en motspelare");

                } catch (Exception ignored){
                    Thread.currentThread().interrupt();
                    return;
            }
        }
    }

    public static void main() throws IOException{
        MatchmakeHandler m = new MatchmakeHandler(5000);
        m.start();
        System.out.println("Kör server på port 5000");
    }
}
