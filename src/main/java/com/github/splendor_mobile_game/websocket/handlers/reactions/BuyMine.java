package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Players sends this request if now is their turn and they want to buy mine (card) that is on the table.
 * In reaction server sends to all players message of type `BUY_MINE_ANNONUCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE",
 *      "data": {
 *          "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *      }
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE_ANNONUCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - uuid of card is the same that should have be send at the start of the game
 * - Of course, we should update state of the game on the server (subtract appropriate amount of tokens for the purchase, add prestige points and add the bonus point)
 * 
 * Also.. Consider user is sending dodgy request, because they wants to cheat. 
 * They send message to buy mine while is a turn of the other player.
 * Or they send message to buy, but they don't have enough amount of tokens.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_MINE_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot buy mine when this is not you turn!"
 *      }
 * }
 * 
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