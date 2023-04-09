package com.github.splendor_mobile_game.websocket.handlers.connection;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.database.Database;

/** An abstract class for checking the health status of a WebSocket connection. */
public abstract class ConnectionChecker {

    /** The WebSocket connection to check. */
    protected WebSocket connection;
    
    protected Database database;

    public ConnectionChecker(WebSocket connection, Database database) {
        this.connection = connection;
        this.database = database;
    }

    public abstract void onConnectionCheck(Long timeSinceLastPongMs);

    public abstract void onConnectionClose();

}
