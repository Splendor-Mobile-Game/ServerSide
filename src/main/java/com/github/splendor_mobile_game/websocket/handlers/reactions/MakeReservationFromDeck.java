package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * This request is sent by a player who wants to make a reservation from the deck during their turn.
 * Upon receiving this request, the server sends an announcement message of type `RESERVATION_FROM_DECK_ANNOUNCEMENT` to all players in the game, indicating that a reservation has been made.
 * 
 * Example of a user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_DECK",
 *      "data": {
 *          "cardTier": 1
 *      }
 * }
 * 
 * Since this move is hidden from other players, different messages must be sent to the requester and other players in the game.
 * 
 * Example of a server announcement to other players in the game:
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
 * Example of a server announcement to the requester:
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
 * Implementation considerations:
 * - The player who sent the message can be identified by their WebSocket's connectionHashCode.
 * - The game's state must be updated in the database.
 * 
 * It is also important to consider that a user may send a fraudulent request in an attempt to cheat. For example, they may send a message when it is not their turn, they are not in any game, or they have already taken another action. All such scenarios must be anticipated and handled appropriately.
 * 
 * In the case of an invalid request, a message should be sent only to the requester, such as:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_DECK_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot make a reservation if it's not your turn!"
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