package server;

import shared.serverProtocol;

import java.util.List;
import shared.Question;

public record GameController(QuestionRepository questionRepo) {

    public GameAction processMessage(SessionState state, boolean fromFirst, String msg) {
        return switch (state.getPhase()) {
            case FIRST_CHOOSING_THEME -> handleThemeChoice(state, fromFirst, msg, true);
            case SECOND_CHOOSING_THEME -> handleThemeChoice(state, fromFirst, msg, false);
            case BOTH_ANSWERING -> handleAnswer(state, fromFirst, msg);
            case GAME_FINISHED -> GameAction.none();
        };
    }

    private GameAction handleThemeChoice(
            SessionState state, boolean fromFirst, String msg, boolean expectFirst) {

        if (fromFirst != expectFirst) {
            return notYourTurn(fromFirst);
        }

        if (!msg.startsWith(serverProtocol.CHOOSE_THEME)) {
            return invalidMessage(fromFirst);
        }

        String theme = msg.substring(serverProtocol.CHOOSE_THEME.length());
        if (!questionRepo.isValidTheme(theme)) {
            return invalidMessage(fromFirst);
        }

        List<Question> questions =
                questionRepo.getQuestions(theme, SessionState.QUESTIONS_PER_ROUND);
        state.startRound(theme, questions);
        state.setPhase(SessionState.Phase.BOTH_ANSWERING);

        // skickar tema och första frågan
        String firstQuestion = state.getCurrentQuestionFor(true).toProtocolString();
        String secondQuestion = state.getCurrentQuestionFor(false).toProtocolString();

        String messageFirst = serverProtocol.THEME_CHOSEN + theme + "\n" +
                serverProtocol.QUESTION + firstQuestion;
        String messageSecond = serverProtocol.THEME_CHOSEN + theme + "\n" +
                serverProtocol.QUESTION + secondQuestion;

        return GameAction.sendDifferent(messageFirst, messageSecond);
    }

    private GameAction handleAnswer(SessionState state, boolean fromFirst, String msg) {
        if (state.hasPlayerFinished(fromFirst)) {
            return new GameAction(
                    fromFirst ? GameAction.Type.SEND_TO_FIRST : GameAction.Type.SEND_TO_SECOND,
                    "ALREADY_FINISHED"
            );
        }

        if (!msg.startsWith(serverProtocol.ANSWER)) {
            return invalidMessage(fromFirst);
        }

        int answerIndex;
        try {
            answerIndex = Integer.parseInt(msg.substring(serverProtocol.ANSWER.length()));
        } catch (NumberFormatException e) {
            return invalidMessage(fromFirst);
        }

        Question current = state.getCurrentQuestionFor(fromFirst);
        boolean correct = current.isCorrect(answerIndex);
        state.recordAnswer(fromFirst, correct);

        int opponentScore = state.getScoreFor(!fromFirst);
        String result = serverProtocol.RESULT + (correct ? "CORRECT" : "WRONG")
                + ":" + opponentScore;

        GameAction.Type target = fromFirst
                ? GameAction.Type.SEND_TO_FIRST
                : GameAction.Type.SEND_TO_SECOND;

        state.nextQuestionFor(fromFirst);

        if (!state.hasMoreQuestionsFor(fromFirst)) {
            if (state.bothPlayersFinished()) {
                return handleRoundComplete(state, fromFirst, result);
            } else {
                return new GameAction(target, result + "\nWAIT_FOR_OPPONENT");
            }
        }

        Question nextQ = state.getCurrentQuestionFor(fromFirst);
        String nextQuestion = serverProtocol.QUESTION + nextQ.toProtocolString();

        return new GameAction(target, result + "\n" + nextQuestion);
    }

    private GameAction handleRoundComplete(
            SessionState state, boolean fromFirst, String lastResult) {
        state.completeRound();

        String roundScore = serverProtocol.ROUND_SCORE + state.getRoundScoreString();

        if (state.isGameFinished()) {
            state.setPhase(SessionState.Phase.GAME_FINISHED);
            String gameOver = serverProtocol.GAME_OVER + state.getTotalScoreString();

            String finisherMsg = lastResult + "\n" + roundScore + "\n" + gameOver;
            String waiterMsg = roundScore + "\n" + gameOver;

            return fromFirst
                    ? GameAction.sendDifferent(finisherMsg, waiterMsg)
                    : GameAction.sendDifferent(waiterMsg, finisherMsg);
        }

        // bestämmer vem som väljer tema
        boolean firstChoosesNext = state.getRoundsPlayed() % 2 == 0;
        if (firstChoosesNext) {
            state.setPhase(SessionState.Phase.FIRST_CHOOSING_THEME);
        } else {
            state.setPhase(SessionState.Phase.SECOND_CHOOSING_THEME);
        }

        String themes = getAvailableThemes();

        // skapar frågorna
        String baseFirst = (fromFirst ? lastResult + "\n" : "") + roundScore + "\n" + themes;
        String baseSecond = (fromFirst ? "" : lastResult + "\n") + roundScore + "\n" + themes;

        String msgFirst = baseFirst + "\n" +
                (firstChoosesNext ? "YOUR_TURN_CHOOSE" : "OPPONENT_CHOOSING");
        String msgSecond = baseSecond + "\n" +
                (firstChoosesNext ? "OPPONENT_CHOOSING" : "YOUR_TURN_CHOOSE");

        return GameAction.sendDifferent(msgFirst, msgSecond);
    }

    public String getAvailableThemes() {
        return serverProtocol.THEMES_LIST + String.join(",", questionRepo.getThemeNames());
    }

    private GameAction notYourTurn(boolean fromFirst) {
        GameAction.Type target = fromFirst
                ? GameAction.Type.SEND_TO_FIRST
                : GameAction.Type.SEND_TO_SECOND;
        return new GameAction(target, "NOT_YOUR_TURN");
    }

    private GameAction invalidMessage(boolean fromFirst) {
        GameAction.Type target = fromFirst
                ? GameAction.Type.SEND_TO_FIRST
                : GameAction.Type.SEND_TO_SECOND;
        return new GameAction(target, "INVALID");
    }
}