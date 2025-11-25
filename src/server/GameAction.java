package server;

public record GameAction(Type type, String message) {

    public enum Type {
        SEND_TO_FIRST,
        SEND_TO_SECOND,
        GAME_FINISHED,
        NONE
    }

    public static GameAction none() {
        return new GameAction(Type.NONE, null);
    }
}
