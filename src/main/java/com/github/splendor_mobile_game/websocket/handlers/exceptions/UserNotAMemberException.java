package com.github.splendor_mobile_game.websocket.handlers.exceptions;

public class UserNotAMemberException extends Exception {

    public UserNotAMemberException() {
    }

    public UserNotAMemberException(String message) {
        super(message);
    }

    public UserNotAMemberException(Throwable cause) {
        super(cause);
    }

    public UserNotAMemberException(String message, Throwable cause) {
        super(message, cause);
    }

}
