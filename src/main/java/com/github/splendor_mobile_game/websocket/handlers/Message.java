package com.github.splendor_mobile_game.websocket.handlers;

public class Message {
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
