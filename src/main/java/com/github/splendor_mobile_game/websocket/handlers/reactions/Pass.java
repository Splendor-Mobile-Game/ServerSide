package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request only if they didn't do any other action and it's impossible 
 * by the rules of game to do any action. This will mark the player that they did an action.
 * In reaction server sends to all players message of type `PASS_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS"
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 * 
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - Probably there is need to implement some logic in the model package. 
 *   We need to store information if a player has done some action.
 * 
 * Also.. Consider user is sending dodgy request, because they wants to break the software.
 * They send message when this is not their turn right now or they aren't in any game,
 * trying to pass, but they can do other action.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot pass this turn, you can do some action!"
 *      }
 * }
 * 
 */
@ReactionName("PASS")
public class Pass extends Reaction {

    public Pass(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
