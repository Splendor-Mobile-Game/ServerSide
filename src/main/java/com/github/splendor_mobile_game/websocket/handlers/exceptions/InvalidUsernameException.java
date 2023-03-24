package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class InvalidUsernameException extends Exception {

    public InvalidUsernameException() {
    }

    public InvalidUsernameException(String message) {
        super(message);
    }

    public InvalidUsernameException(Throwable cause) {
        super(cause);
    }

    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

}
