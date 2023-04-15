package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/** Game not started exception, used when user is trying to do some action in the game but game hasn't started yet */
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
