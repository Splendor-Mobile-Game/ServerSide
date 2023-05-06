package com.github.splendor_mobile_game.game.exceptions;

public class CanPerformAnActionException extends Exception {

    public CanPerformAnActionException() {}

    public CanPerformAnActionException(String message) {
        super(message);
    }

    public CanPerformAnActionException(Throwable cause) {
        super(cause);
    }

    public CanPerformAnActionException(String message, Throwable cause) {
        super(message, cause);
    }

}
