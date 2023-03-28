package com.github.splendor_mobile_game.websocket.response;

import java.util.UUID;

import com.google.gson.Gson;

/** Represents an error response to be returned to the client. */
public class ErrorResponse {

    /** Represents the data portion of the error response. */
    private static class Data {
        @SuppressWarnings("unused")
        String error;

        /**
         * Initializes a new instance of the Data class.
         * @param error the error message
         */
        Data(String error) {
            this.error = error;
        }
    }

    // TODO: Use UUID class
    /** The unique ID of the message context. */
    public String messageContextId;

    /** The type of the response. */
    public ResponseType type;

    /** The result of the response. */
    public Result result;

    /** The data of the response. */
    public Data data;

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     * @param responseType the type of the response
     * @param messageContextId the unique ID of the message context
     */
    public ErrorResponse(Result result, String error, ResponseType responseType, String messageContextId) {
        this.messageContextId = messageContextId;
        this.type = responseType;
        this.result = result;
        this.data = new Data(error);
    }

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     * @param responseType the type of the response
     */
    public ErrorResponse(Result result, String error, ResponseType responseType) {
        this(result, error, responseType, UUID.randomUUID().toString());
    }

    /**
     * Initializes a new instance of the ErrorResponse class.
     * @param result the result of the response
     * @param error the error message
     */
    public ErrorResponse(Result result, String error) {
        this(result, error, ResponseType.UNKNOWN);
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
