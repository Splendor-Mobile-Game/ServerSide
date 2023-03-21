package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;

public abstract class Reaction {

    protected int connectionHashCode;
    protected Messenger messenger;
    protected Database database;

    public Reaction(int connectionHashCode, Messenger messenger, Database database) {
        this.connectionHashCode = connectionHashCode;
        this.messenger = messenger;
        this.database = database;
    }

    public abstract void react(ReceivedMessage parsedMessage);
}
