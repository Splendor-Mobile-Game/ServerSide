package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;

public abstract class Reaction {

    protected int connectionHashCode;
    protected Messenger messenger;
    protected Database database;
    protected ReceivedMessage receivedMessage;

    public Reaction(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        this.connectionHashCode = connectionHashCode;
        this.receivedMessage = receivedMessage;
        this.messenger = messenger;
        this.database = database;
    }

    public abstract void react();
}
