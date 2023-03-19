package com.github.splendor_mobile_game.websocket.config.exceptions;

public class EnvFileNotFoundException extends InvalidConfigException {

    public EnvFileNotFoundException() {
    }

    public EnvFileNotFoundException(String message) {
        super(message);
    }

    public EnvFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public EnvFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
