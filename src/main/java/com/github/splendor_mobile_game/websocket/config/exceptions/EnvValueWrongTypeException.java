package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when the value of an environment variable is of the wrong type. */
public class EnvValueWrongTypeException extends InvalidConfigException {

    public EnvValueWrongTypeException() {
    }

    public EnvValueWrongTypeException(String message) {
        super(message);
    }

    public EnvValueWrongTypeException(Throwable cause) {
        super(cause);
    }

    public EnvValueWrongTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
