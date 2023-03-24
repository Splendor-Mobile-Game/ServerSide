package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomAlreadyExistsException extends Exception {

    public RoomAlreadyExistsException() {
    }

    public RoomAlreadyExistsException(String message) {
        super(message);
    }

    public RoomAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public RoomAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
