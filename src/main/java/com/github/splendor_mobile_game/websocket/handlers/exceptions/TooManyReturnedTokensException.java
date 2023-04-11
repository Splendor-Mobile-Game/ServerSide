package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class TooManyReturnedTokensException extends Exception {

    public TooManyReturnedTokensException() {
    }

    public TooManyReturnedTokensException(String message) {
        super(message);
    }

    public TooManyReturnedTokensException(Throwable cause) {
        super(cause);
    }

    public TooManyReturnedTokensException(String message, Throwable cause) {
        super(message, cause);
    }

}