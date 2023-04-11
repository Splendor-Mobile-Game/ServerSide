package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class TooManyTokensException extends Exception {

    public TooManyTokensException() {
    }

    public TooManyTokensException(String message) {
        super(message);
    }

    public TooManyTokensException(Throwable cause) {
        super(cause);
    }

    public TooManyTokensException(String message, Throwable cause) {
        super(message, cause);
    }

}