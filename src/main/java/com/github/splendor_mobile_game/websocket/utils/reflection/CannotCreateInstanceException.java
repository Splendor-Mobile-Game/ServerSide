package com.github.splendor_mobile_game.websocket.utils.reflection;
import com.github.splendor_mobile_game.websocket.utils.CustomException;

/** Exception thrown when an instance of a class cannot be created using reflection. */
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
