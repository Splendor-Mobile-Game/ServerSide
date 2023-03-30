package com.github.splendor_mobile_game.websocket.handlers;

// TODO: Java doc required
public class MissingFieldsException extends Exception {
    public String jsonReply;

    public MissingFieldsException(String errorMessage, String jsonReply) {
        super(errorMessage);
        this.jsonReply = jsonReply;
    }

}
