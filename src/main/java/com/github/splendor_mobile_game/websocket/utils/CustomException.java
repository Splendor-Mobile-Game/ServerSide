package com.github.splendor_mobile_game.websocket.utils;

import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import java.util.UUID;

/**
 * Custom exception class that extends RuntimeException.
 * It's used when you don't want many try catches or throws declarations,
 * because there is top level try catch
 */
public class CustomException extends RuntimeException {

    private Result result = Result.FAILURE;
    private UUID contextId;
    private ServerMessageType type;

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Result result) {
        super(message);
        this.result = result;
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(Throwable cause, Result result) {
        super(cause);
        this.result = result;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message, Throwable cause, Result result) {
        super(message, cause);
        this.result = result;
    }

    public CustomException(UUID contextId) {
        this.contextId=contextId;
    }

    public CustomException(ServerMessageType type) {
        this.type=type;
    }

    public CustomException(String message, ServerMessageType type) {
        super(message);
        this.type=type;
    }

    public CustomException(String message, Result result,  ServerMessageType type) {
        super(message);
        this.result = result;
        this.type=type;
    }

    public CustomException(Throwable cause, ServerMessageType type) {
        super(cause);
        this.type=type;
    }

    public CustomException(Throwable cause, Result result, ServerMessageType type) {
        super(cause);
        this.result = result;
        this.type=type;
    }

    public CustomException(String message, Throwable cause, ServerMessageType type) {
        super(message, cause);
        this.type=type;
    }

    public CustomException(String message, Throwable cause, Result result, ServerMessageType type) {
        super(message, cause);
        this.result = result;
        this.type=type;
    }

    public CustomException(String message, UUID contextId) {
        super(message);
        this.contextId=contextId;
    }

    public CustomException(String message, Result result, UUID contextId) {
        super(message);
        this.result = result;
        this.contextId=contextId;
    }

    public CustomException(Throwable cause, UUID contextId) {
        super(cause);
        this.contextId=contextId;
    }

    public CustomException(Throwable cause, Result result, UUID contextId) {
        super(cause);
        this.result = result;
        this.contextId=contextId;
    }

    public CustomException(String message, Throwable cause, UUID contextId) {
        super(message, cause);
        this.contextId=contextId;
    }

    public CustomException(String message, Throwable cause, Result result, UUID contextId) {
        super(message, cause);
        this.result = result;
        this.contextId=contextId;
    }

    public CustomException(String message, UUID contextId, ServerMessageType type) {
        super(message);
        this.contextId=contextId;
        this.type=type;
    }

    public CustomException(String message, Result result, UUID contextId, ServerMessageType type) {
        super(message);
        this.result = result;
        this.contextId=contextId;
        this.type=type;
    }

    public CustomException(Throwable cause, UUID contextId, ServerMessageType type) {
        super(cause);
        this.contextId=contextId;
        this.type=type;
    }

    public CustomException(Throwable cause, Result result, UUID contextId, ServerMessageType type) {
        super(cause);
        this.result = result;
        this.contextId=contextId;
        this.type=type;
    }

    public CustomException(String message, Throwable cause, UUID contextId, ServerMessageType type) {
        super(message, cause);
        this.contextId=contextId;
        this.type=type;
    }

    public CustomException(String message, Throwable cause, Result result, UUID contextId, ServerMessageType type) {
        super(message, cause);
        this.result = result;
        
    }

    
    

    @Override
    public String toString() {
        return this.getMessage();
    }

    /**
     * Returns a JSON string representation of an error response object containing the message and cause of this custom exception.
     *
     * @return a JSON string representation of an error response object
     */
    public String toJsonResponse() {
        return (new ErrorResponse(this.result, this.toString(),this.type,this.contextId.toString())).ToJson();
    }

}
