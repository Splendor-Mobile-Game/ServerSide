package com.github.splendor_mobile_game.websocket.communication;

import com.github.splendor_mobile_game.websocket.utils.json.JsonParser;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;
import com.google.gson.JsonObject;

public class ReceivedMessage {
    private String messageContextId;
    private String type;
    private JsonObject data;

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

    public JsonObject getData() {
        return data;
    }

}
