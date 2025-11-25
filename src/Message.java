import java.io.Serializable;

public class Message implements Serializable {

    public enum Type {
        PLAYER_NAME,
        WAITING,
        GAME_START,
        QUESTION,
        ANSWER,
        RESULT
    }

    private Type type;
    private Object data;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}