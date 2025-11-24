package server;

import client.ClientHandler;

public class Session {
    private static int NEXT_ID = 1;
    private final int id;

    private final ClientHandler firstClient;
    private final ClientHandler secondClient;

    public Session(ClientHandler firstClient, ClientHandler secondClient) {
        this.id = NEXT_ID++;
        this.firstClient = firstClient;
        this.secondClient = secondClient;
    }

    public void notifyClients() {
        String text = "Du är ansluten till en andra spelare i en session";
        firstClient.send(text);
        secondClient.send(text);
    }

    public int getId() {
        return id;
    }
}
