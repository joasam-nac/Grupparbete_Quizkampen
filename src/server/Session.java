package server;

import java.util.concurrent.*;

public class Session {
    private static int NEXT_ID = 1;
    private static final int ANSWER_TIMEOUT_SECONDS = 15;

    private final int id;
    private final ClientHandler firstClient;
    private final ClientHandler secondClient;
    private final GameController controller;
    private final SessionState state;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> firstPlayerTimer;
    private ScheduledFuture<?> secondPlayerTimer;
    private volatile boolean isCleanedUp = false;

    public Session(ClientHandler first, ClientHandler second, QuestionRepository repo, GameConfig config) {
        this.id = NEXT_ID++;
        this.firstClient = first;
        this.secondClient = second;
        this.controller = new GameController(repo, config);
        this.state = new SessionState(config);
    }

    public int getId() { return id; }

    public void start() {
        String themes = controller.getAvailableThemes();
        firstClient.send(shared.serverProtocol.OPPONENT_NAME + secondClient.getPlayerName());
        secondClient.send(shared.serverProtocol.OPPONENT_NAME + firstClient.getPlayerName());
        firstClient.send(themes);
        firstClient.send("YOUR_TURN_CHOOSE");
        secondClient.send(themes);
        secondClient.send("OPPONENT_CHOOSING");
    }

    public synchronized void handleMessage(ClientHandler sender, String msg) {
        if (isCleanedUp) {
            return;
        }

        boolean fromFirst = sender == firstClient;

        if (msg.startsWith(shared.serverProtocol.ANSWER)) {
            cancelTimer(fromFirst);
        }

        GameAction action = controller.processMessage(state, fromFirst, msg);
        executeAction(action);

        if (state.getPhase() == SessionState.Phase.GAME_FINISHED) {
            scheduler.schedule(this::cleanup, 2, TimeUnit.SECONDS);
        }
    }

    private void executeAction(GameAction action) {
        switch (action.type()) {
            case SEND_TO_FIRST -> {
                firstClient.send(action.messageFirst());
                startTimerIfQuestion(action.messageFirst(), true);
            }
            case SEND_TO_SECOND -> {
                secondClient.send(action.messageSecond());
                startTimerIfQuestion(action.messageSecond(), false);
            }
            case SEND_TO_BOTH -> {
                firstClient.send(action.messageFirst());
                secondClient.send(action.messageFirst());
                startTimerIfQuestion(action.messageFirst(), true);
                startTimerIfQuestion(action.messageFirst(), false);
            }
            case SEND_DIFFERENT -> {
                firstClient.send(action.messageFirst());
                secondClient.send(action.messageSecond());
                startTimerIfQuestion(action.messageFirst(), true);
                startTimerIfQuestion(action.messageSecond(), false);
            }
            case NONE -> {}
        }
    }

    private void startTimerIfQuestion(String message, boolean forFirst) {
        if (isCleanedUp) {
            return;
        }

        if (message.contains(shared.serverProtocol.QUESTION) &&
                state.getPhase() == SessionState.Phase.BOTH_ANSWERING) {

            cancelTimer(forFirst);

            ScheduledFuture<?> timer = scheduler.schedule(
                    () -> handleTimeout(forFirst),
                    ANSWER_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (forFirst) {
                firstPlayerTimer = timer;
            } else {
                secondPlayerTimer = timer;
            }
        }
    }

    private synchronized void handleTimeout(boolean forFirst) {
        if (isCleanedUp || state.hasPlayerFinished(forFirst)) {
            return;
        }

        state.recordAnswer(forFirst, false);
        state.nextQuestionFor(forFirst);

        int opponentScore = state.getScoreFor(!forFirst);
        String result = shared.serverProtocol.RESULT + "TIMEOUT:" + opponentScore;
        ClientHandler client = forFirst ? firstClient : secondClient;

        if (!state.hasMoreQuestionsFor(forFirst)) {
            if (state.bothPlayersFinished()) {
                GameAction action = controller.processMessage(
                        state, forFirst, shared.serverProtocol.ANSWER + "0"
                );
                executeAction(action);
            } else {
                client.send(result + "\nWAIT_FOR_OPPONENT");
            }
        } else {
            shared.Question nextQ = state.getCurrentQuestionFor(forFirst);
            String nextQuestion = shared.serverProtocol.QUESTION +
                    nextQ.toProtocolString();
            client.send(result + "\n" + nextQuestion);
            startTimerIfQuestion(nextQuestion, forFirst);
        }
    }

    private void cancelTimer(boolean forFirst) {
        ScheduledFuture<?> timer = forFirst ? firstPlayerTimer : secondPlayerTimer;
        if (timer != null && !timer.isDone()) {
            timer.cancel(false);
        }
    }

    public void notifyDisconnect(ClientHandler disconnected) {
        if (isCleanedUp) {
            return;
        }

        ClientHandler other = (disconnected == firstClient) ? secondClient : firstClient;
        try {
            other.send("OPPONENT_DISCONNECTED");
        } catch (Exception e) {
            System.err.println("Could not notify opponent of disconnect: " + e.getMessage());
        }
    }

    public synchronized void cleanup() {
        if (isCleanedUp) {
            return;
        }
        isCleanedUp = true;

        System.out.println("Cleaning up session " + id);

        cancelTimer(true);
        cancelTimer(false);

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}