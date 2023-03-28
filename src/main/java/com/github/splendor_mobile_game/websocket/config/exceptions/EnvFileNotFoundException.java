package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when an environment file is not found. */
public class EnvFileNotFoundException extends InvalidConfigException {

    /** Constructs a new EnvFileNotFoundException with no message. */
    public EnvFileNotFoundException() {
    }

    /**
     * Constructs a new EnvFileNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public EnvFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new EnvFileNotFoundException with the specified cause.
     *
     * @param cause the cause
     */
    public EnvFileNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new EnvFileNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public EnvFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
