package com.github.splendor_mobile_game.handlers.connection;

import org.java_websocket.WebSocket;

public abstract class ConnectionHandler {

    protected WebSocket connection;

    public ConnectionHandler(WebSocket connection) {
        this.connection = connection;
    }

    public abstract void onConnectionCheck(Long timeSinceLastPongMs);

    public abstract void onConnectionClose();
}
