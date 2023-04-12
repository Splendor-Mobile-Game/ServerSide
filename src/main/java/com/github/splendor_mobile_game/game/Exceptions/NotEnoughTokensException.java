package com.github.splendor_mobile_game.game.Exceptions;

public class NotEnoughTokensException extends Exception {

    public NotEnoughTokensException() {
    }

    public NotEnoughTokensException(String message) {
        super(message);
    }

    public NotEnoughTokensException(Throwable cause) {
        super(cause);
    }

    public NotEnoughTokensException(String message, Throwable cause) {
        super(message, cause);
    }

}
