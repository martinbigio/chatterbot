package ar.edu.itba.tpf.chatterbot.exception;

public class ServerServiceException extends ChatterbotException {

    private static final long serialVersionUID = 1L;

    public ServerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
