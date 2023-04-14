package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomPlayerCountException extends Exception{
    public RoomPlayerCountException() {
    }

    public RoomPlayerCountException(String message) {
        super(message);
    }

    public RoomPlayerCountException(Throwable cause) {
        super(cause);
    }

    public RoomPlayerCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
