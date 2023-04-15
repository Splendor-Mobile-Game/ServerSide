package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/** Wrong token choice exception, used when user's token choice is against the rules of Splendor*/
public class WrongTokenChoiceException extends Exception {

    public WrongTokenChoiceException() {
    }

    public WrongTokenChoiceException(String message) {
        super(message);
    }

    public WrongTokenChoiceException(Throwable cause) {
        super(cause);
    }

    public WrongTokenChoiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
