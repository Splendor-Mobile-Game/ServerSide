package com.github.splendor_mobile_game.handlers;

import com.github.splendor_mobile_game.websocket.ReceivedMessage;

public abstract class Reaction {

    protected int connectionHashCode;

    public Reaction(int connectionHashCode) {
        this.connectionHashCode = connectionHashCode;
    }

    public abstract String getReply(ReceivedMessage parsedMessage);
}
