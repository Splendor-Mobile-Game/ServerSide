package com.github.splendor_mobile_game.websocket.utils;

import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

/**
 * Custom exception class that extends RuntimeException.
 * It's used when you don't want many try catches or throws declarations,
 * because there is top level try catch
 */
public class CustomException extends RuntimeException {

    private Result result = Result.ERROR;

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

    @Override
    public String toString() {
        return this.getMessage() + "\n" + ExceptionUtils.getStackTrace(this);
    }

    /**
     * Returns a JSON string representation of an error response object containing the message and cause of this custom exception.
     *
     * @return a JSON string representation of an error response object
     */
    public String toJsonResponse() {
        return (new ErrorResponse(this.result, this.toString())).ToJson();
    }

}
