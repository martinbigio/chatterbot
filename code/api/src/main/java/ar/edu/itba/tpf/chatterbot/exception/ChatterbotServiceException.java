package ar.edu.itba.tpf.chatterbot.exception;

public class ChatterbotServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public ChatterbotServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
