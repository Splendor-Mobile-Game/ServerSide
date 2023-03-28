package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when a required field is missing in a JSON object. */
public class JsonMissingFieldException extends JsonParserException {

    /** Constructs a new JsonMissingFieldException with no message. */
    public JsonMissingFieldException() {
    }

    /**
     * Constructs a new JsonMissingFieldException with the specified message.
     *
     * @param message the detail message
     */
    public JsonMissingFieldException(String message) {
        super(message);
    }

    /**
     * Constructs a new JsonMissingFieldException with the specified cause.
     *
     * @param cause the cause
     */
    public JsonMissingFieldException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JsonMissingFieldException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public JsonMissingFieldException(String message, Throwable cause) {
        super(message, cause);
    }

}
