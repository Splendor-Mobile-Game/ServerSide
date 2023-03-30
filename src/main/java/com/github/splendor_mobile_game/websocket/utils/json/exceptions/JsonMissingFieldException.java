package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when a required field is missing in a JSON object. */
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
