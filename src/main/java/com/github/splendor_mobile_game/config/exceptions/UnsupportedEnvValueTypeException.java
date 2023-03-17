package com.github.splendor_mobile_game.config.exceptions;

public class UnsupportedEnvValueTypeException extends InvalidConfigException {
    public UnsupportedEnvValueTypeException() {
    }

    public UnsupportedEnvValueTypeException(String message) {
        super(message);
    }

    public UnsupportedEnvValueTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedEnvValueTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
