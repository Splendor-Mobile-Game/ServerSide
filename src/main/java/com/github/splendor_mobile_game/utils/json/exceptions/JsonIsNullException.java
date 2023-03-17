package com.github.splendor_mobile_game.utils.json.exceptions;

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
