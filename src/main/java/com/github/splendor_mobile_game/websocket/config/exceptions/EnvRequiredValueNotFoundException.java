package com.github.splendor_mobile_game.websocket.config.exceptions;

public class EnvRequiredValueNotFoundException extends InvalidConfigException {

    public EnvRequiredValueNotFoundException() {
    }

    public EnvRequiredValueNotFoundException(String message) {
        super(message);
    }

    public EnvRequiredValueNotFoundException(Throwable cause) {
        super(cause);
    }

    public EnvRequiredValueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
