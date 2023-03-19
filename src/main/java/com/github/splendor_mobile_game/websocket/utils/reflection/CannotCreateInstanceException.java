package com.github.splendor_mobile_game.websocket.utils.reflection;

public class CannotCreateInstanceException extends Exception {
    public CannotCreateInstanceException() {
    }

    public CannotCreateInstanceException(String message) {
        super(message);
    }

    public CannotCreateInstanceException(Throwable cause) {
        super(cause);
    }

    public CannotCreateInstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
