package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * This class handles the `PASS` reaction. Players can send this request only if they haven't done any other action and it's impossible by the rules of the game to do any action. This will mark the player that they did an action. In reaction, the server sends to all players a message of type `PASS_ANNOUNCEMENT` announcing that this has happened.
 *
 * Example of user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS"
 * }
 *
 * Example of server announcement:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "9fc1845e-5469-458d-9893-07d390908479"
 *      }
 * }
 *
 * Implementation details:
 * - The player who sent the message is identified by their WebSocket's connectionHashCode.
 * - You need to modify some game classes, so they store information if the players has taken some action
 *
 * Description of bad requests:
 * - If a player sends a message when it's not their turn or they aren't in any game, trying to pass, but they can do another action, the server should send a response only to the requester.
 *
 * Example of a bad request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot pass this turn, you can do some action!"
 *      }
 * }
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
