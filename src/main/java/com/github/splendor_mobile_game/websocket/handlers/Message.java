package com.github.splendor_mobile_game.websocket.handlers;

/** Represents a message to be sent to a receiver identified by their websocket hashcode. */
public class Message {

    /** The websocket hashcode of the receiver of the message. */
    private int receiverHashcode;

    /** The content of the message. */
    private String message;

    /**
     * Constructs a new Message object with the given receiver websocket hashcode and message content.
     * @param receiverHashcode the websocket hashcode of the receiver of the message
     * @param message the content of the message
     */
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
