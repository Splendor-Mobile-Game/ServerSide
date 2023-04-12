package com.github.splendor_mobile_game.websocket.config.exceptions;

/** Exception thrown when an unsupported environment variable value type is encountered. */
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
