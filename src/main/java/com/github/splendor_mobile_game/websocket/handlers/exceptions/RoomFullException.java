package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomFullException extends Exception {

    public RoomFullException() {
    }

    public RoomFullException(String message) {
        super(message);
    }

    public RoomFullException(Throwable cause) {
        super(cause);
    }

    public RoomFullException(String message, Throwable cause) {
        super(message, cause);
    }

}
