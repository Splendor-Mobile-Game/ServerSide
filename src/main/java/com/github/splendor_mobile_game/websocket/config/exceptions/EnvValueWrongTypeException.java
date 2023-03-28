package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when the value of an environment variable is of the wrong type. */
public class EnvValueWrongTypeException extends InvalidConfigException {

    /** Constructs a new EnvValueWrongTypeException with no message. */
    public EnvValueWrongTypeException() {
    }

    /**
     * Constructs a new EnvValueWrongTypeException with the specified message.
     *
     * @param message the detail message
     */
    public EnvValueWrongTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new EnvValueWrongTypeException with the specified cause.
     *
     * @param cause the cause
     */
    public EnvValueWrongTypeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new EnvValueWrongTypeException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public EnvValueWrongTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
