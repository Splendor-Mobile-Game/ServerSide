package com.github.splendor_mobile_game.websocket.utils;

import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

public class CustomException extends Exception {
    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return this.getMessage() + "\n" + this.getCause().getMessage();
    }

    public String toJsonResponse() {
        return (new ErrorResponse(Result.FAILURE, this.toString())).ToJson();
    }

}
