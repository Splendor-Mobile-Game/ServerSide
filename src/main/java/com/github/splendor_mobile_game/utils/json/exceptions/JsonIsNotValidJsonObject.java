package com.github.splendor_mobile_game.utils.json.exceptions;

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
