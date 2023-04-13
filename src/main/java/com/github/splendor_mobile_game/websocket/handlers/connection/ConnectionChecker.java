package com.github.splendor_mobile_game.websocket.handlers.connection;

import java.util.Map;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.database.Database;

/** An abstract class for checking the health status of a WebSocket connection. */
public abstract class ConnectionChecker {

    /** The WebSocket connection to check. */
    protected WebSocket connection;
    
    protected Database database;

    /** All connections server has with clients */
    protected Map<Integer, WebSocket> connections;

    public ConnectionChecker(WebSocket connection, Database database, Map<Integer, WebSocket> connections) {
        this.connection = connection;
        this.database = database;
        this.connections = connections;
    }

    public abstract void onConnectionCheck(Long timeSinceLastPongMs);

    public abstract void onConnectionClose();

}
