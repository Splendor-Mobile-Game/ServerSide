package com.github.splendor_mobile_game.websocket.communication;

import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.json.JsonParser;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;

public class ServerMessage {
    private UUID messageContextId;
    private ServerMessageType type;
    private Result result;
    // @Optional
    private Object data;

    public ServerMessage(String message) throws InvalidReceivedMessage {
        ServerMessage msg = ServerMessage.fromJson(message);
        this.messageContextId = msg.messageContextId;
        this.type = msg.type;
        this.result = msg.result;
        this.data = msg.getData();
    }

    public ServerMessage(UUID messageContextId, ServerMessageType type, Result result, Object data) {
        this.messageContextId = messageContextId;
        this.type = type;
        this.result = result;
        this.data = data;
    }

    public static ServerMessage fromJson(String inputJson) throws InvalidReceivedMessage {
        try {
            return JsonParser.parseJson(inputJson, ServerMessage.class);
        } catch (JsonParserException e) {
            throw new InvalidReceivedMessage("Received message is invalid!", e);
        }
    }

    public UUID getMessageContextId() {
        return messageContextId;
    }

    public ServerMessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
