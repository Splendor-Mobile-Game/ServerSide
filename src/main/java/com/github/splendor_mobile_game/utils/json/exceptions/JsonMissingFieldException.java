package com.github.splendor_mobile_game.utils.json.exceptions;

public class JsonMissingFieldException extends JsonParserException {
    public JsonMissingFieldException() {
    }

    public JsonMissingFieldException(String message) {
        super(message);
    }

    public JsonMissingFieldException(Throwable cause) {
        super(cause);
    }

    public JsonMissingFieldException(String message, Throwable cause) {
        super(message, cause);
    }

}
