package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class PerrmissionDeniedExeption extends Exception {

    public PerrmissionDeniedExeption() {
    }

    public PerrmissionDeniedExeption(String message) {
        super(message);
    }

    public PerrmissionDeniedExeption(Throwable cause) {
        super(cause);
    }

    public PerrmissionDeniedExeption(String message, Throwable cause) {
        super(message, cause);
    }

}
