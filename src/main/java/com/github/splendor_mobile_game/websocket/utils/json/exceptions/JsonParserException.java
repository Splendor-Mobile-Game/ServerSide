package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

import com.github.splendor_mobile_game.websocket.utils.CustomException;

/** Exception thrown when there is an error parsing JSON. */
public class JsonParserException extends CustomException {

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
