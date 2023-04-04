package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;

// TODO: Java doc required
public abstract class Reaction {

    protected int connectionHashCode;
    protected Messenger messenger;
    protected Database database;
    protected UserMessage userMessage;

    public Reaction(int connectionHashCode, UserMessage receivedMessage, Messenger messenger, Database database) {
        this.connectionHashCode = connectionHashCode;
        this.userMessage = receivedMessage;
        this.messenger = messenger;
        this.database = database;
    }

    public abstract void react();
}
