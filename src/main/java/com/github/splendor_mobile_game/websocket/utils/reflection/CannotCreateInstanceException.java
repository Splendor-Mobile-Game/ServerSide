package com.github.splendor_mobile_game.websocket.utils.reflection;

import com.github.splendor_mobile_game.websocket.utils.CustomException;

public class CannotCreateInstanceException extends CustomException {
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
