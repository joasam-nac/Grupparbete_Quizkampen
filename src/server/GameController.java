package server;

public class GameController {
    public enum Turn {
        FIRST_CHOOSE,
        SECOND_CHOOSE,
        FIRST_ANSWER,
        SECOND_ANSWER,
        FINISHED
    }

    private static final int QUESTIONS_PER_THEME = 3;
    private static final int THEMES_PER_GAME = 3;

    public GameAction processMessage(
            SessionState state,
            boolean fromFirst,
            String msg,
            ThemeValidator validator
    ) {
        System.out.println("Turn=" + state.turn + " msg=" + msg +
                " fromFirst=" + fromFirst);

        return switch (state.turn) {
            case FIRST_CHOOSE -> {
                if (fromFirst && validator.isValidTheme(msg)) {
                    state.currentTheme = msg;
                    state.questionsAnswered = 0;
                    state.turn = Turn.SECOND_ANSWER;
                    yield new GameAction(
                            GameAction.Type.SEND_TO_SECOND,
                            String.format(GameMessages.THEME_SELECTED,
                                    state.currentTheme) +
                                    GameMessages.ANSWER_QUESTION_1
                    );
                }
                yield invalidChoice(fromFirst);
            }
            case SECOND_ANSWER -> {
                if (!fromFirst && msg.startsWith("SVAR:")) {
                    state.questionsAnswered++;
                    if (state.questionsAnswered == QUESTIONS_PER_THEME) {
                        state.themesPlayed++;
                        state.turn = Turn.SECOND_CHOOSE;
                        yield new GameAction(
                                GameAction.Type.SEND_TO_SECOND,
                                GameMessages.CHOOSE_OPPONENT_THEME
                        );
                    }
                    yield new GameAction(
                            GameAction.Type.SEND_TO_SECOND,
                            String.format(GameMessages.NEXT_QUESTION,
                                    state.questionsAnswered + 1)
                    );
                }
                yield invalidChoice(fromFirst);
            }
            case SECOND_CHOOSE -> {
                if (!fromFirst && validator.isValidTheme(msg)) {
                    state.currentTheme = msg;
                    state.questionsAnswered = 0;
                    state.turn = Turn.FIRST_ANSWER;
                    yield new GameAction(
                            GameAction.Type.SEND_TO_FIRST,
                            String.format(GameMessages.THEME_SELECTED,
                                    state.currentTheme) +
                                    GameMessages.ANSWER_QUESTION_1
                    );
                }
                yield invalidChoice(fromFirst);
            }
            case FIRST_ANSWER -> {
                if (fromFirst && msg.startsWith("SVAR:")) {
                    state.questionsAnswered++;
                    if (state.questionsAnswered == QUESTIONS_PER_THEME) {
                        state.themesPlayed++;
                        if (state.themesPlayed == THEMES_PER_GAME) {
                            state.turn = Turn.FINISHED;
                            yield new GameAction(
                                    GameAction.Type.GAME_FINISHED,
                                    null
                            );
                        }
                        state.turn = Turn.FIRST_CHOOSE;
                        yield new GameAction(
                                GameAction.Type.SEND_TO_FIRST,
                                GameMessages.CHOOSE_OPPONENT_THEME
                        );
                    }
                    yield new GameAction(
                            GameAction.Type.SEND_TO_FIRST,
                            String.format(GameMessages.NEXT_QUESTION,
                                    state.questionsAnswered + 1)
                    );
                }
                yield invalidChoice(fromFirst);
            }
            default -> GameAction.none();
        };
    }

    private GameAction invalidChoice(boolean fromFirst) {
        GameAction.Type type = fromFirst
                ? GameAction.Type.SEND_TO_FIRST
                : GameAction.Type.SEND_TO_SECOND;
        return new GameAction(type, GameMessages.INVALID_CHOICE);
    }

    public static class SessionState {
        public String currentTheme;
        public int themesPlayed;
        public int questionsAnswered;
        public Turn turn = Turn.FIRST_CHOOSE;
    }
}