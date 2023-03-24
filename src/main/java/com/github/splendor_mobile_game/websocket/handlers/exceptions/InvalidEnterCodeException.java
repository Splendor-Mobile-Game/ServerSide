package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class InvalidEnterCodeException extends Exception {

    public InvalidEnterCodeException() {
    }

    public InvalidEnterCodeException(String message) {
        super(message);
    }

    public InvalidEnterCodeException(Throwable cause) {
        super(cause);
    }

    public InvalidEnterCodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
