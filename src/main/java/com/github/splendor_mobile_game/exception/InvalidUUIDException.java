package com.github.splendor_mobile_game.exception;

public class InvalidUUIDException extends Exception {

    public InvalidUUIDException() {
    }

    public InvalidUUIDException(String message) {
        super(message);
    }

    public InvalidUUIDException(Throwable cause) {
        super(cause);
    }

    public InvalidUUIDException(String message, Throwable cause) {
        super(message, cause);
    }

}
