package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when a JSON object is null. */
public class JsonIsNullException extends JsonParserException {

    /** Constructs a new JsonIsNullException with no message. */
    public JsonIsNullException() {
    }

    /**
     * Constructs a new JsonIsNullException with the specified detail message.
     *
     * @param message the detail message
     */
    public JsonIsNullException(String message) {
        super(message);
    }

    /**
     * Constructs a new JsonIsNullException with the specified cause.
     *
     * @param cause the cause
     */
    public JsonIsNullException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JsonIsNullException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public JsonIsNullException(String message, Throwable cause) {
        super(message, cause);
    }
}
