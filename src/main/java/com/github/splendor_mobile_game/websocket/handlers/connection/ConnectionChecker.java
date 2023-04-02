package com.github.splendor_mobile_game.websocket.handlers.connection;

import org.java_websocket.WebSocket;

/** An abstract class for checking the health status of a WebSocket connection. */
public abstract class ConnectionChecker {

    /** The WebSocket connection to check. */
    protected WebSocket connection;

    public ConnectionChecker(WebSocket connection) {
        this.connection = connection;
    }

    public abstract void onConnectionCheck(Long timeSinceLastPongMs);

    public abstract void onConnectionClose();

}
