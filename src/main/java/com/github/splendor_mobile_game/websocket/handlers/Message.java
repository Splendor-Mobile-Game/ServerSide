package com.github.splendor_mobile_game.websocket.handlers;

/** Represents a message to be sent to a receiver identified by their websocket hashcode. */
public class Message {

    /** Each connection between User and Server is identified by some ID. This is its. */
    private int receiverHashcode;

    private String message;

    public Message(int receiverHashcode, String message) {
        this.receiverHashcode = receiverHashcode;
        this.message = message;
    }

    public int getReceiverHashcode() {
        return receiverHashcode;
    }

    public String getMessage() {
        return message;
    }

}
