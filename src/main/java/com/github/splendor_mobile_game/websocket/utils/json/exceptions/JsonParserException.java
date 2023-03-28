package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when there is an error parsing JSON. */
public class JsonParserException extends Exception {

    /** Constructs a new JsonParserException with no message. */
    public JsonParserException() {
    }

    /**
     * Constructs a new JsonParserException with the specified detail message.
     *
     * @param message the detail message
     */
    public JsonParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new JsonParserException with the specified cause.
     *
     * @param cause the cause
     */
    public JsonParserException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JsonParserException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public JsonParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
