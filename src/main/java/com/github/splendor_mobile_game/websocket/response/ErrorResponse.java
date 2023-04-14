package com.github.splendor_mobile_game.websocket.response;

import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.google.gson.Gson;

/** Represents an error response to be returned to the client. */
public class ErrorResponse {

    /** Represents the data portion of the error response. */
    public static class Data {
        public String error;

        Data(String error) {
            this.error = error;
        }
    }

    // TODO: Use UUID class
    /** The unique ID of the message context. */
    public String contextId;

    /** The type of the response. */
    public ServerMessageType type;

    /** The result of the response. */
    public Result result;

    /** The data of the response. */
    public Data data;

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     * @param serverMessageType the type of the response
     * @param messageContextId the unique ID of the message context
     */
    public ErrorResponse(Result result, String error, ServerMessageType serverMessageType, String messageContextId) {
        this.contextId = messageContextId;
        this.type = serverMessageType;
        this.result = result;
        this.data = new Data(error);
    }

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     * @param serverMessageType the type of the response
     */
    public ErrorResponse(Result result, String error, ServerMessageType serverMessageType) {
        this(result, error, serverMessageType, UUID.randomUUID().toString());
    }

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     */
    public ErrorResponse(Result result, String error) {
        this(result, error, ServerMessageType.ERROR);
    }

    /**
     * Converts the ErrorResponse object to a JSON string.
     * @return the JSON string representation of the object
     */
    public String ToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
