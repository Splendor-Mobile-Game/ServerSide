package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class GameNotStartedException extends Exception {

    public GameNotStartedException() {
    }

    public GameNotStartedException(String message) {
        super(message);
    }

    public GameNotStartedException(Throwable cause) {
        super(cause);
    }

    public GameNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

}
