package com.github.splendor_mobile_game.handlers.connection;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.utils.Log;

public class ConnectionHandlerImpl extends ConnectionHandler {

    // This field is available due to abstract class
    // protected WebSocket connection;

    public ConnectionHandlerImpl(WebSocket connection) {
        super(connection);
    }

    @Override
    public void onConnectionCheck(Long timeSinceLastPongMs) {
        Log.DEBUG("Connection `" + connection.hashCode() + "`, last pong " + timeSinceLastPongMs + "ms ago.");

        if (timeSinceLastPongMs > 60000) {
            Log.DEBUG("Powyżej 30 sekund!");
            connection.close();
            // Do something
        }

        if (timeSinceLastPongMs > 15000) {
            Log.DEBUG("Powyżej 15 sekund!");
            // Do something
        }
    }

    @Override
    public void onConnectionClose() {
        Log.DEBUG("No chyba ty!");
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'onConnectionClose'");
    }

}
