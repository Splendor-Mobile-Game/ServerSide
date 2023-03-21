package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class UserAlreadyInRoomException extends Exception {

    public UserAlreadyInRoomException() {
    }

    public UserAlreadyInRoomException(String message) {
        super(message);
    }

    public UserAlreadyInRoomException(Throwable cause) {
        super(cause);
    }

    public UserAlreadyInRoomException(String message, Throwable cause) {
        super(message, cause);
    }

}
