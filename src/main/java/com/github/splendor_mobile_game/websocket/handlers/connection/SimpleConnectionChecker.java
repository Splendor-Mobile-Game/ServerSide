package com.github.splendor_mobile_game.websocket.handlers.connection;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * This class extends the abstract ConnectionChecker class and provides a simple implementation
 * of the onConnectionCheck and onConnectionClose methods.
 */
public class SimpleConnectionChecker extends ConnectionChecker {


    /**
     * Constructor for SimpleConnectionChecker.
     * @param connection The WebSocket connection to check.
     * @param database The database with users, rooms and games
     */
    public SimpleConnectionChecker(WebSocket connection, Database database) {
        super(connection, database);
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

        User user = database.getUserByConnectionHashCode(connection.hashCode());
        
        if (user != null) {
            Room room = database.getRoomWithUser(user.getUuid());
            if (room != null) {
                room.leaveGame(user);
                Log.DEBUG("User `" + user.getConnectionHashCode() + "` has been removed from its room, because connection has been lost.");    
            }
            database.getAllUsers().remove(user);
            Log.DEBUG("User `" + user.getConnectionHashCode() + "` has been removed from entire database, because connection has been lost.");
        }
        
    }

}
