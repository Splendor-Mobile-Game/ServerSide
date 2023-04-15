package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/** Too many tokens exception, used when user has more than 10 tokens after taking them */
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