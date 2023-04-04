package com.github.splendor_mobile_game.websocket.utils.json.exceptions;

/** Exception thrown when the provided JSON is not a valid JSON object. */
public class JsonIsNotValidJsonObject extends JsonParserException {

    public JsonIsNotValidJsonObject() {
    }

    public JsonIsNotValidJsonObject(String message) {
        super(message);
    }

    public JsonIsNotValidJsonObject(Throwable cause) {
        super(cause);
    }

    public JsonIsNotValidJsonObject(String message, Throwable cause) {
        super(message, cause);
    }

}
