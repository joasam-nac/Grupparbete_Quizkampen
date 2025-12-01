package server;

public class Session {
    private static int NEXT_ID = 1;
    private final int id;

    private final ClientHandler firstClient;
    private final ClientHandler secondClient;
    private final GameController controller;
    private final SessionState state = new SessionState();

    public Session(ClientHandler first, ClientHandler second, QuestionRepository repo) {
        this.id = NEXT_ID++;
        this.firstClient = first;
        this.secondClient = second;
        this.controller = new GameController(repo);
    }

    public int getId() { return id; }

    public void start() {
        String themes = controller.getAvailableThemes();

        // Skicka motståndarnas namn till varandra
        firstClient.send(shared.serverProtocol.OPPONENT_NAME + secondClient.getPlayerName());
        secondClient.send(shared.serverProtocol.OPPONENT_NAME + firstClient.getPlayerName());

        // Starta spelet som vanligt
        firstClient.send(themes);
        firstClient.send("YOUR_TURN_CHOOSE");
        secondClient.send(themes);
        secondClient.send("OPPONENT_CHOOSING");
    }

    public synchronized void handleMessage(ClientHandler sender, String msg) {
        boolean fromFirst = sender == firstClient;
        GameAction action = controller.processMessage(state, fromFirst, msg);
        executeAction(action);
    }

    private void executeAction(GameAction action) {
        switch (action.type()) {
            case SEND_TO_FIRST -> firstClient.send(action.messageFirst());
            case SEND_TO_SECOND -> secondClient.send(action.messageSecond());
            case SEND_TO_BOTH -> {
                firstClient.send(action.messageFirst());
                secondClient.send(action.messageFirst());
            }
            case SEND_DIFFERENT -> {
                firstClient.send(action.messageFirst());
                secondClient.send(action.messageSecond());
            }
            case NONE -> {}
        }
    }
}