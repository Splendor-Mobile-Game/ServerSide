package com.github.splendor_mobile_game.websocket.handlers;

import java.util.ArrayList;
import java.util.List;

import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.google.gson.Gson;

public class Messenger {
    private List<Message> messages = new ArrayList<>();

    public void addMessageToSend(int receiverHashcode, ServerMessage serverMessage) {
        this.addMessageToSend(receiverHashcode, (new Gson()).toJson(serverMessage));
    }

    public void addMessageToSend(int receiverHashcode, ErrorResponse errorResponse) {
        this.addMessageToSend(receiverHashcode, errorResponse.ToJson());
    }

    private void addMessageToSend(int receiverHashcode, String body) {
        messages.add(new Message(receiverHashcode, body));
    }

    public List<Message> getMessages() {
        return messages;
    }

}
