package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when a JSON object is null. */
public class JsonIsNullException extends JsonParserException {

    public JsonIsNullException() {
    }

    public JsonIsNullException(String message) {
        super(message);
    }

    public JsonIsNullException(Throwable cause) {
        super(cause);
    }

    public JsonIsNullException(String message, Throwable cause) {
        super(message, cause);
    }

}
