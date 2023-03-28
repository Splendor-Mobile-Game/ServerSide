package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when the provided JSON is not a valid JSON object. */
public class JsonIsNotValidJsonObject extends JsonParserException {

    /** Constructs a new JsonIsNotValidJsonObject with no detail message. */
    public JsonIsNotValidJsonObject() {
    }

    /**
     * Constructs a new JsonIsNotValidJsonObject with the specified detail message.
     *
     * @param message the detail message.
     */
    public JsonIsNotValidJsonObject(String message) {
        super(message);
    }

    /**
     * Constructs a new JsonIsNotValidJsonObject with the specified cause.
     *
     * @param cause the cause.
     */
    public JsonIsNotValidJsonObject(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JsonIsNotValidJsonObject with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public JsonIsNotValidJsonObject(String message, Throwable cause) {
        super(message, cause);
    }
}
