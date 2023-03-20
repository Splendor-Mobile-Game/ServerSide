package com.github.splendor_mobile_game.websocket.handlers;

import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private List<Message> messages = new ArrayList<>();

    public void addMessageToSend(int receiverHashcode, String body) {
        messages.add(new Message(receiverHashcode, body));
    }

    public List<Message> getMessages() {
        return messages;
    }

}
