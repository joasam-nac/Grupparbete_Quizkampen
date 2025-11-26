package server;

public record GameAction(Type type, String messageFirst, String messageSecond) {
    public enum Type {
        SEND_TO_FIRST,
        SEND_TO_SECOND,
        SEND_TO_BOTH,
        SEND_DIFFERENT,
        NONE
    }

    public GameAction(Type type, String message) {
        this(type, message, message);
    }

    public static GameAction none() {
        return new GameAction(Type.NONE, null, null);
    }

    public static GameAction sendDifferent(String toFirst, String toSecond) {
        return new GameAction(Type.SEND_DIFFERENT, toFirst, toSecond);
    }
}