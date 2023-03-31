package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request if now is their turn and they want to make reservation.
 * In reaction server sends to all players message of type `RESERVATION_FROM_TABLE_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE",
 *      "data": {
 *          "cardUuid": "b38df21a-6e7b-4537-a20b-ad797a394350"
 *      }
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_TABLE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "9978b2ba-f3e5-4e23-818a-879b0adcfe9a",
 *          "cardUuid": "a59cabab-6dac-44c7-ae53-ad8e22936f2c"
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - Of course we need to update game's state in database.
 * 
 * Also.. Consider user is sending dodgy request, because they wants to cheat.
 * They send message when this is not their turn right now, they aren't in any game,
 * they already did some other action etc.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot make reservation if it's not your turn!"
 *      }
 * }
 * 
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
