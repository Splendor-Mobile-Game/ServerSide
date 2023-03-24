package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomDoesntExistException extends Exception {

    public RoomDoesntExistException() {
    }

    public RoomDoesntExistException(String message) {
        super(message);
    }

    public RoomDoesntExistException(Throwable cause) {
        super(cause);
    }

    public RoomDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
