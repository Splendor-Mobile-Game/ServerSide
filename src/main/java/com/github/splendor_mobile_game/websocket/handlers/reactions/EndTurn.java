package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request if now is their turn, they did some action and they now want to end the turn.
 * In reaction server sends to all players message of type `NEW_TURN_ANNOUNCEMENT` announcing that this has happend and selecting player for the next turn.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "END_TURN"
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "NEW_TURN_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "nextPlayer": {
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *          }
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - Probably there is need to implement some logic in the model package. 
 *   We need to store information about who's turn's right now and what's the order.
 * 
 * Also.. Consider user is sending dodgy request, because they wants to break the software.
 * They send message when this is not their turn right now or they aren't in any game.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "END_TURN_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot end turn if it's not your turn!"
 *      }
 * }
 * 
 */
@ReactionName("END_TURN")
public class EndTurn extends Reaction {

    public EndTurn(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'react'");
    }
    
}
