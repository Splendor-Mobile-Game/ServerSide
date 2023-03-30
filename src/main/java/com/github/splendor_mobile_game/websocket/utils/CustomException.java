package com.github.splendor_mobile_game.websocket.utils;

import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

/**
 * Custom exception class that extends RuntimeException.
 * It's used when you don't want many try catches or throws declarations,
 * because there is top level try catch
 */
public class CustomException extends RuntimeException {

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

    /**
     * Returns a string representation of this custom exception, including the message and cause.
     *
     * @return a string representation of this custom exception
     */
    @Override
    public String toString() {
        return this.getMessage() + "\n" + this.getCause().getMessage();
    }

    /**
     * Returns a JSON string representation of an error response object containing the message and cause of this custom exception.
     *
     * @return a JSON string representation of an error response object
     */
    public String toJsonResponse() {
        return (new ErrorResponse(Result.FAILURE, this.toString())).ToJson();
    }

}
