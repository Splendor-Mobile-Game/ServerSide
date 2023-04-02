package com.github.splendor_mobile_game.websocket.handlers.connection;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * This class extends the abstract ConnectionChecker class and provides a simple implementation
 * of the onConnectionCheck and onConnectionClose methods.
 */
public class SimpleConnectionChecker extends ConnectionChecker {


    /**
     * Constructor for SimpleConnectionChecker.
     * @param connection The WebSocket connection to check.
     */
    public SimpleConnectionChecker(WebSocket connection) {
        super(connection);
    }

    /**
     * This method is called periodically to check the status of the WebSocket connection.
     * @param timeSinceLastPongMs The time since the last pong message was received, in milliseconds.
     */
    @Override
    public void onConnectionCheck(Long timeSinceLastPongMs) {
        //Log.DEBUG("Connection `" + connection.hashCode() + "`, last pong " + timeSinceLastPongMs + "ms ago.");

        if (timeSinceLastPongMs > 60000) {
            Log.DEBUG("Powyżej 60 sekund!");
            connection.close();
            // Do something
        }

        if (timeSinceLastPongMs > 15000) {
            Log.DEBUG("Powyżej 15 sekund!");
            // Do something
        }
    }

    /** This method is called when the WebSocket connection is closed. */
    @Override
    public void onConnectionClose() {
        Log.TRACE(this.connection.hashCode() + " has been closed!");
    }

}
