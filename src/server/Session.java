package server;

import client.ClientHandler;

public class Session {
    private static int NEXT_ID = 1;
    private final int id;

    private final ClientHandler firstClient;
    private final ClientHandler secondClient;
    private final GameController controller = new GameController();
    private final GameController.SessionState state =
            new GameController.SessionState();

    private final ThemeValidator validator;

    public Session(
            ClientHandler firstClient,
            ClientHandler secondClient,
            ThemeValidator validator
    ) {
        this.id = NEXT_ID++;
        this.firstClient = firstClient;
        this.secondClient = secondClient;
        this.validator = validator;
    }

    public int getId() {
        return id;
    }

    public void start() {
        System.out.println("Session " + id +
                " startar, det är första klientens tur");
        firstClient.send(GameMessages.CHOOSE_OPPONENT_THEME);
    }

    public void handleMessage(ClientHandler sender, String msg) {
        boolean fromFirst = sender == firstClient;
        GameAction action = controller.processMessage(
                state,
                fromFirst,
                msg,
                validator
        );

        executeAction(action);
    }

    private void executeAction(GameAction action) {
        switch (action.type()) {
            case SEND_TO_FIRST:
                firstClient.send(action.message());
                break;
            case SEND_TO_SECOND:
                secondClient.send(action.message());
                break;
            case GAME_FINISHED:
                firstClient.send(GameMessages.GAME_OVER);
                secondClient.send(GameMessages.GAME_OVER);
                break;
            default:
                break;
        }
    }
}