package com.github.splendor_mobile_game.websocket.handlers.connection;

import java.util.Map;
import java.util.UUID;

import org.java_websocket.WebSocket;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.reactions.EndTurn;
import com.github.splendor_mobile_game.websocket.handlers.reactions.LeaveRoom;
import com.github.splendor_mobile_game.websocket.response.Result;
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
    public SimpleConnectionChecker(WebSocket connection, Database database, Map<Integer, WebSocket> connections) {
        super(connection, database, connections);
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

        // Get the user associated with the closed connection
        User user = database.getUserByConnectionHashCode(connection.hashCode());

        if (user == null) {
            return;
        }
        
        // If the user was in a room, remove them from the room
        Room room = database.getRoomWithUser(user.getUuid());
        if (room != null) {
            room.leaveGame(user);
            Log.DEBUG("User `" + user.getConnectionHashCode() + "` has been removed from its room, because connection has been lost.");
            
            //Remove room if it's empty
            if(room.getAllUsers().size()==0){
                database.getAllRooms().remove(room);
                Log.DEBUG("Room `" + room.getName() + "` has been removed from entire database, because all players have left.");
            }
            else if(room.getGame()!=null && room.getCurrentPlayer()==user){
                room.changeTurn();

                // Create a message to inform other players that is new turn
                EndTurn.ResponseData responseData = new EndTurn.ResponseData(room.getCurrentPlayer().getUuid());
                ServerMessage serverMessage = new ServerMessage(UUID.randomUUID(), ServerMessageType.NEW_TURN_ANNOUNCEMENT, Result.OK, responseData);

                // Send leave information to other players
                for (User u : room.getAllUsers()) {
                    WebSocket userConnection = connections.get(u.getConnectionHashCode());
                    if (userConnection != null) {
                        String message = serverMessage.toJson();
                        userConnection.send(message);
                        Log.DEBUG("Message sent to (" +
                            u.getConnectionHashCode() + ":" + userConnection.getRemoteSocketAddress() + "): " + message
                        );
                    }
                }
            }

            // Create a message to inform other players that the user has left the room
            LeaveRoom.UserDataResponse userDataResponse = new LeaveRoom.UserDataResponse(user.getUuid(), user.getName());
            LeaveRoom.ResponseData responseData = new LeaveRoom.ResponseData(userDataResponse);
            ServerMessage serverMessage = new ServerMessage(UUID.randomUUID(), ServerMessageType.LEAVE_ROOM_RESPONSE, Result.OK, responseData);                

            // Send leave information to other players
            for (User u : room.getAllUsers()) {
                WebSocket userConnection = connections.get(u.getConnectionHashCode());
                if (userConnection != null) {
                    String message = serverMessage.toJson();
                    userConnection.send(message);
                    Log.DEBUG("Message sent to (" +
                        u.getConnectionHashCode() + ":" + userConnection.getRemoteSocketAddress() + "): " + message
                    );
                }
            }    
        }

        // Remove the user from the database
        database.getAllUsers().remove(user);
        Log.DEBUG("User `" + user.getConnectionHashCode() + "` has been removed from entire database, because connection has been lost.");
    }

}
