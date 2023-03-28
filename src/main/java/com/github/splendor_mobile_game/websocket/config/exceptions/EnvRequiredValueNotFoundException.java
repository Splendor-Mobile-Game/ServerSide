package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when a required environment variable is not found. */
public class EnvRequiredValueNotFoundException extends InvalidConfigException {

    /** Constructs a new EnvRequiredValueNotFoundException with no detail message. */
    public EnvRequiredValueNotFoundException() {
    }

    /**
     * Constructs a new EnvRequiredValueNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public EnvRequiredValueNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new EnvRequiredValueNotFoundException with the specified cause.
     *
     * @param cause the cause
     */
    public EnvRequiredValueNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new EnvRequiredValueNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public EnvRequiredValueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
