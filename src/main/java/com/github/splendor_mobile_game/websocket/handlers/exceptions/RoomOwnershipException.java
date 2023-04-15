package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class RoomOwnershipException extends Exception {

    public RoomOwnershipException() {
    }

    public RoomOwnershipException(String message) {
        super(message);
    }

    public RoomOwnershipException(Throwable cause) {
        super(cause);
    }

    public RoomOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
