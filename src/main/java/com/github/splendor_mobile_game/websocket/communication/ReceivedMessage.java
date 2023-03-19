package com.github.splendor_mobile_game.websocket.communication;

import com.github.splendor_mobile_game.websocket.utils.json.JsonParser;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;
import com.google.gson.Gson;

public class ReceivedMessage {
    private String messageContextId;
    private String type;
    private Object data;

    public ReceivedMessage(String message) throws InvalidReceivedMessage {
        ReceivedMessage msg = ReceivedMessage.fromJson(message);
        this.messageContextId = msg.messageContextId;
        this.type = msg.type;
        this.data = msg.getData();
    }

    public void parseDataToClass(Class<?> clazz) throws InvalidReceivedMessage {
        try {
            // TODO: Perfomance loss because of redundant json parsing
            this.data = JsonParser.parseJson((new Gson()).toJson(this.data), clazz);
        } catch (JsonParserException e) {
            throw new InvalidReceivedMessage("Received message is invalid!", e);
        }
    }

    public static ReceivedMessage fromJson(String inputJson) throws InvalidReceivedMessage {
        try {
            return JsonParser.parseJson(inputJson, ReceivedMessage.class);
        } catch (JsonParserException e) {
            throw new InvalidReceivedMessage("Received message is invalid!", e);
        }
    }

    public String getMessageContextId() {
        return messageContextId;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
