package com.github.splendor_mobile_game.websocket.communication;

import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.UserRequestType;
import com.github.splendor_mobile_game.websocket.utils.json.JsonParser;
import com.github.splendor_mobile_game.websocket.utils.json.Optional;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;
import com.google.gson.Gson;

/**
 * Represents a message sent by a user.
 */
public class UserMessage {

    private UUID contextId;

    private UserRequestType type;

    @Optional
    private Object data;

    /**
     * Creates a new UserMessage by parsing the provided JSON message.
     * Heavly used on the server side.
     *
     * @param json the JSON message to parse
     * @throws JsonParserException if the provided message is invalid
     */
    public UserMessage(String json) throws JsonParserException {
        UserMessage msg = UserMessage.fromJson(json);

        this.contextId = msg.contextId;
        this.type = msg.type;
        this.data = msg.getData();
    }

    /**
     * Creates a new UserMessage with the provided message context ID, type, and data.
     *
     * @param contextId the message context ID
     * @param type the request type
     * @param data the data associated with the message
     */
    public UserMessage(UUID contextId, UserRequestType type, Object data) {
        this.contextId = contextId;
        this.type = type;
        this.data = data;
    }

    /**
     * Parses the data object to the provided class using JSON serialization.
     *
     * @param clazz the class to which the data should be parsed
     * @throws InvalidReceivedMessage if the data cannot be parsed to the provided class
     */
    public void parseDataToClass(Class<?> clazz) throws InvalidReceivedMessage {
        try {
            // TODO: Perfomance loss because of redundant json parsing
            this.data = JsonParser.parseJson((new Gson()).toJson(this.data), clazz);
        } catch (JsonParserException e) {
            throw new InvalidReceivedMessage("Received message is invalid!", e);
        }
    }

    /**
     * Parses a JSON string to a UserMessage object.
     *
     * @param json the JSON string to parse
     * @return the UserMessage object
     * @throws JsonParserException if the provided message is invalid
     */
    private static UserMessage fromJson(String json) throws JsonParserException  {
        return JsonParser.parseJson(json, UserMessage.class);
    }

    public UUID getContextId() {
        return contextId;
    }

    public UserRequestType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserMessage other = (UserMessage) obj;
        if (contextId == null) {
            if (other.contextId != null)
                return false;
        } else if (!contextId.equals(other.contextId))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

}
