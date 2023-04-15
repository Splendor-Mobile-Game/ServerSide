package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class NotThisUserTurnException extends Exception {

    public NotThisUserTurnException() {
    }

    public NotThisUserTurnException(String message) {
        super(message);
    }

    public NotThisUserTurnException(Throwable cause) {
        super(cause);
    }

    public NotThisUserTurnException(String message, Throwable cause) {
        super(message, cause);
    }

}
