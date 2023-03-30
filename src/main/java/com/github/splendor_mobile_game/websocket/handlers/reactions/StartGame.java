package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request if they are the host and wants to start the game.
 * In reaction server sends to all players message of type `GAME_STARTED_ANNOUNCEMENT` announcing that this has happend.
 * This announcement message have all the information about the initial state of the game.
 * For example all the cards that were drawed from the decks and now on the table, aristocrats, number of tokens on the table etc.
 * Quickly after that, server sends message `NEW_TURN_ANNOUNCEMENT` with the player which turn is first.
 * See `EndTurn` reaction class that has to implement the same message as well.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME"
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GAME_STARTED_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "tokens": {
 *              "red": 7,
 *              "green": 7,
 *              "blue": 7,
 *              "white": 7,
 *              "black": 7,
 *              "gold": 5,
 *          },
 *          "aristocrats": [
 *             {
 *                  "id": "81b7249e-d1f0-4030-a59d-0217ee3ac161",
 *                  "prestige": 3,
 *                  "redMinesRequired": 4,
 *                  "blueMinesRequired": 4
 *              },
 *              {
 *                  "id": "8bceab0a-d67f-44b2-ad4f-cda592cb4b13",
 *                  "prestige": 3,
 *                  "greenMinesRequired": 2,
 *                  "whiteMinesRequired": 3,
 *                  "blackMinesRequired": 3
 *              },
 *              ...
 *              ...
 *              ...
 *          ],
 *          "firstLevelMinesCards": [
 *              {
 *                  "id": "0ba9cba8-3bc0-42fe-b24f-25d7b52fcd2c",
 *                  "prestige": 2,
 *                  "bonusColor": "green",
 *                  "greenTokensRequired": 2,
 *                  "whiteTokensRequired": 3
 *              },
 *              {
 *                  "id": "e1df722d-a64c-424c-856e-431748bb358f",
 *                  "prestige": 0,
 *                  "bonusColor": "white",
 *                  "blackTokensRequired": 3
 *              }
 *              ...
 *              ...
 *              ...
 *          ],
 *          "secondLevelMinesCards": [], // The same as above
 *          "thirdLevelMinesCards": [], // The same as above
 *      }
 * }
 * 
 * Also.. Consider user is sending dodgy request, because they wants to break the software.
 * They send message when they aren't in any game or they aren't the host.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot start game if you're not the host of this game!"
 *      }
 * }
 * 
 */
@ReactionName("START_GAME")
public class StartGame extends Reaction {

    public StartGame(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
