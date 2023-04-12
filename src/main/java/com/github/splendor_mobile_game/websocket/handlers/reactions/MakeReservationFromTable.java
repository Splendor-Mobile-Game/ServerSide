package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Reaction for handling player's request to make a reservation from the table. The player can only make a reservation if it is their turn.
 * Upon receiving a valid request, the server sends an announcement to all players in the game.
 *
 * Example of a valid request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE",
 *      "data": {
 *          "userUuid": "01901b0e-a78b-4a65-bbd3-0065948dc127",
 *          "cardUuid": "b38df21a-6e7b-4537-a20b-ad797a394350"
 *      }
 * }
 *
 * Example of a successful server announcement:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_TABLE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "9978b2ba-f3e5-4e23-818a-879b0adcfe9a",
 *          "cardUuid": "a59cabab-6dac-44c7-ae53-ad8e22936f2c"
 *      }
 * }
 *
 * Implementation details:
 * - The player's WebSocket connectionHashCode is used to identify the player.
 * - The game state in the database needs to be updated.
 *
 * If the request is invalid (e.g. player is not in a game, it is not their turn, etc.), the server should only send a response to the requester.
 *
 * Example of an invalid request response:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot make a reservation if it is not your turn!"
 *      }
 * }
 */
@ReactionName("MAKE_RESERVATION_FROM_TABLE")
public class MakeReservationFromTable extends Reaction {

    public MakeReservationFromTable(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
