package com.mawus.bot.exceptions;

public class TransportTypeNotFound extends RuntimeException {

    public TransportTypeNotFound(String message) {
        super(message);
    }

    public TransportTypeNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
