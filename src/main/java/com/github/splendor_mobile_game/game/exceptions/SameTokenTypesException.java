package com.github.splendor_mobile_game.game.exceptions;

public class SameTokenTypesException extends Exception {

    public SameTokenTypesException() {}

    public SameTokenTypesException(String message) {
        super(message);
    }

    public SameTokenTypesException(Throwable cause) {
        super(cause);
    }

    public SameTokenTypesException(String message, Throwable cause) {
        super(message, cause);
    }

}
