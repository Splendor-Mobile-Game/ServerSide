package com.github.splendor_mobile_game.websocket.utils.reflection;
import com.github.splendor_mobile_game.websocket.utils.CustomException;

/** Exception thrown when an instance of a class cannot be created using reflection. */
public class CannotCreateInstanceException extends CustomException {
    /** Constructs a new CannotCreateInstanceException with no message. */
    public CannotCreateInstanceException() {
    }

    /**
     * Constructs a new CannotCreateInstanceException with the specified message.
     *
     * @param message the detail message
     */
    public CannotCreateInstanceException(String message) {
        super(message);
    }

    /**
     * Constructs a new CannotCreateInstanceException with the specified cause.
     *
     * @param cause the cause
     */
    public CannotCreateInstanceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CannotCreateInstanceException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CannotCreateInstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
