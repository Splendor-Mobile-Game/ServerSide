package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request if now is their turn and they want to make reservation.
 * In reaction server sends to all players message of type `RESERVATION_FROM_DECK_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_DECK",
 *      "data": {
 *          "cardTier": 1
 *      }
 * }
 * 
 * We have to send different messages to the requester and to the other players
 * because this move is the only thing in the game that is hidden from other players
 * 
 * Example of server announcement to the other players in the room
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_TABLE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "01901b0e-a78b-4a65-bbd3-0065948dc127",
 *          "card": {
 *              "uuid": "cbbea744-2772-4acd-ad01-9029240654dc",
 *              "tier": 1
 *          }
 *      }
 * }
 * 
 * Example of server announcement to the requester 
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_TABLE_RESPONSE",
 *      "result": "OK",
 *      "data": {
 *          "card": {
 *              "id": "0ba9cba8-3bc0-42fe-b24f-25d7b52fcd2c",
 *              "prestige": 2,
 *              "bonusColor": "green",
 *              "greenTokensRequired": 2,
 *              "whiteTokensRequired": 3
 *          }
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
 *      "type": "MAKE_RESERVATION_FROM_DECK_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot make reservation if it's not your turn!"
 *      }
 * }
 * 
 */
@ReactionName("MAKE_RESERVATION_FROM_DECK")
public class MakeReservationFromDeck extends Reaction {

    public MakeReservationFromDeck(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}