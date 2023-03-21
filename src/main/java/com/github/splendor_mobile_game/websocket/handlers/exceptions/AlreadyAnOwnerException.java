package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class AlreadyAnOwnerException extends Exception {

    public AlreadyAnOwnerException() {
    }

    public AlreadyAnOwnerException(String message) {
        super(message);
    }

    public AlreadyAnOwnerException(Throwable cause) {
        super(cause);
    }

    public AlreadyAnOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

}
