package com.github.splendor_mobile_game.config.exceptions;

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
