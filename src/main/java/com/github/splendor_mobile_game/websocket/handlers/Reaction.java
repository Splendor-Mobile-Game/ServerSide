package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;

public abstract class Reaction {

    protected int connectionHashCode;

    public Reaction(int connectionHashCode) {
        this.connectionHashCode = connectionHashCode;
    }

    public abstract void react(ReceivedMessage parsedMessage, Messenger messenger, Database database);
}
