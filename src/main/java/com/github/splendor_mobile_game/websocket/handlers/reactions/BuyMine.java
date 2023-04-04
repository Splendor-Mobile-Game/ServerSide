package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Players send this request when it is their turn and they want to buy a mine card that is on the table.
 * The server responds with a message of type `BUY_MINE_ANNOUNCEMENT` to all players, announcing that the purchase has been made.
 * 
 * Example of user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *      }
 * }
 * 
 * Example of server announcement:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "userName": "John",
 *          "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *      }
 * }
 *
 * Implementation details:
 * - The player who sent the message is identified by their WebSocket's connectionHashCode.
 * - The UUID of the card to be purchased is the same as the one sent at the start of the game.
 * - The state of the game on the server should be updated to reflect the purchase (subtract the appropriate amount of tokens, add prestige points, and add the bonus point).
 * 
 * Invalid requests:
 * - If the player sends a message to buy a mine when it is not their turn, the server should respond with a message of type `BUY_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that they cannot buy a mine when it is not their turn.
 * - If the player sends a message to buy a mine but does not have enough tokens, the server should respond with a message of type `BUY_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that they do not have enough tokens to make the purchase.
 * - If the player sends a message to buy a mine that is not available on the table, the server should respond with a message of type `BUY_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that the requested mine is not available on the table.
 * 
 * Example of invalid request response (it should be sent only to the requester):
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot buy a mine when it is not your turn!"
 *      }
 * }
 */
@ReactionName("BUY_MINE")
public class BuyMine extends Reaction {

    public BuyMine(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}