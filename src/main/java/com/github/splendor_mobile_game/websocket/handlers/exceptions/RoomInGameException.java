package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomInGameException extends Exception{

    public RoomInGameException() {
    }

    public RoomInGameException(String message) {
        super(message);
    }

    public RoomInGameException(Throwable cause) {
        super(cause);
    }

    public RoomInGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
