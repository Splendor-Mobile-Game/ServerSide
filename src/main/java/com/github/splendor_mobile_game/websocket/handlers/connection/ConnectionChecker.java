package com.github.splendor_mobile_game.websocket.handlers.connection;

import org.java_websocket.WebSocket;

/** An abstract class for checking the health status of a WebSocket connection. */
public abstract class ConnectionChecker {

    /** The WebSocket connection to check. */
    protected WebSocket connection;

    /**
     * Creates a new ConnectionChecker instance.
     *
     * @param connection the WebSocket connection to check
     */
    public ConnectionChecker(WebSocket connection) {
        this.connection = connection;
    }

    /**
     * Called when a connection check is performed.
     *
     * @param timeSinceLastPongMs the time since the last pong message was received, in milliseconds
     */
    public abstract void onConnectionCheck(Long timeSinceLastPongMs);

    /** Called when the WebSocket connection is closed. */
    public abstract void onConnectionClose();
}
