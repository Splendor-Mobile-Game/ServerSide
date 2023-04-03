package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class UserDoesntExistException extends Exception{

    public UserDoesntExistException() {
    }

    public UserDoesntExistException(String message) {
        super(message);
    }

    public UserDoesntExistException(Throwable cause) {
        super(cause);
    }

    public UserDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
