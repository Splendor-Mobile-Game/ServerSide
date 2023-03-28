package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when an unsupported environment variable value type is encountered. */
public class UnsupportedEnvValueTypeException extends InvalidConfigException {

    /** Constructs a new UnsupportedEnvValueTypeException with no message. */
    public UnsupportedEnvValueTypeException() {
    }

    /**
     * Constructs a new UnsupportedEnvValueTypeException with the specified message.
     *
     * @param message the detail message
     */
    public UnsupportedEnvValueTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnsupportedEnvValueTypeException with the specified cause.
     *
     * @param cause the cause
     */
    public UnsupportedEnvValueTypeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnsupportedEnvValueTypeException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnsupportedEnvValueTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
