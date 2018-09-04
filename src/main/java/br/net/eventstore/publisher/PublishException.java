package br.net.eventstore.publisher;

public class PublishException extends RuntimeException {
    public PublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
