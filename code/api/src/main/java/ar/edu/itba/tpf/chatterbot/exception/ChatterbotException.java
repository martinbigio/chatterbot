package ar.edu.itba.tpf.chatterbot.exception;

public class ChatterbotException extends Exception {
    private static final long serialVersionUID = 1L;

    public ChatterbotException(String message, Throwable cause) {
        super(message, cause);
    }
}
