package com.github.splendor_mobile_game.websocket.communication;

import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.json.JsonParser;
import com.github.splendor_mobile_game.websocket.utils.json.Optional;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;

/**
 * Represents a message sent by the server to the client.
 */
public class ServerMessage {
    
    private UUID contextId;
    
    private ServerMessageType type;
    
    private Result result;
    
    @Optional
    private Object data;

    /**
     * Constructs a ServerMessage object from a JSON string.
     * Heavly used on the client side.
     * 
     * @param json the JSON string representing the message
     * @throws InvalidReceivedMessage if the json string does not represent the ServerMessage object.
     */
    public ServerMessage(String json) throws InvalidReceivedMessage {
        ServerMessage msg = ServerMessage.fromJson(json);
        this.contextId = msg.contextId;
        this.type = msg.type;
        this.result = msg.result;
        this.data = msg.getData();
    }

    /**
     * Constructs a ServerMessage object with the specified parameters.
     * 
     * @param contextId the UUID of the message context
     * @param type the type of the message
     * @param result the result of the message
     * @param data the data of the message (optional)
     */
    public ServerMessage(UUID contextId, ServerMessageType type, Result result, Object data) {
        this.contextId = contextId;
        this.type = type;
        this.result = result;
        this.data = data;
    }

    /**
     * Parses a JSON string into a ServerMessage object.
     * 
     * @param json the JSON string to parse
     * @return the ServerMessage object
     * @throws InvalidReceivedMessage if the received message is invalid
     */
    private static ServerMessage fromJson(String json) throws InvalidReceivedMessage {
        try {
            return JsonParser.parseJson(json, ServerMessage.class);
        } catch (JsonParserException e) {
            throw new InvalidReceivedMessage("Received message is invalid!", e);
        }
    }

    public UUID getContextId() {
        return contextId;
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

    public Result getResult() {
        return result;
    }

    /**
     * Converts the current ServerMessage object to an ErrorResponse object.
     * 
     * @return the ErrorResponse object
     * @throws JsonParserException if the data object in this ServerMessage cannot be converted to an ErrorResponse object
     * @throws UnsupportedOperationException if the result of this ServerMessage object is Result.OK
     */
    public ErrorResponse toErrorResponse() throws JsonParserException {
        if (this.getResult() == Result.OK) {
            throw new UnsupportedOperationException("This ServerMessage is not the ErrorResponse, because its result is OK");
        }

        ErrorResponse.Data data = (ErrorResponse.Data) this.getData();
        return new ErrorResponse(this.getResult(), data.error, this.getType(), this.getContextId().toString());
    }

}
