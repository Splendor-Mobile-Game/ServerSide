package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when there is an error parsing JSON. */
public class JsonParserException extends Exception {

    public JsonParserException() {
    }

    public JsonParserException(String message) {
        super(message);
    }

    public JsonParserException(Throwable cause) {
        super(cause);
    }

    public JsonParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
