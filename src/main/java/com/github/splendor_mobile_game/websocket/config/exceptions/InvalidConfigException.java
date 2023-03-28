package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when a configuration is invalid. */
public class InvalidConfigException extends Exception {

    /** Constructs a new InvalidConfigException with no message. */
    public InvalidConfigException() {
    }

    /**
     * Constructs a new InvalidConfigException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidConfigException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidConfigException with the specified cause.
     *
     * @param cause the cause
     */
    public InvalidConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidConfigException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
