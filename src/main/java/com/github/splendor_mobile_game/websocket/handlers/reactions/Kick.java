package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Players sends this request if they are the host of the game and they want to kick other player from the lobby.
 * In reaction server sends to all players message of type `KICK_ANNONUCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "KICK",
 *      "data": {
 *          "playerUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *      }
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "KICK_ANNONUCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *      }
 * }
 * 
 * Also.. Consider user is sending dodgy request, because they wants to cheat. 
 * They send message to kick someone, but they are not the host
 * or maybe they are not in any game or they are the host, but specified player
 * they want to kick isn't in their room.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "KICK_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot kick the player in the game you're not the host of!"
 *      }
 * }
 * 
 */
@ReactionName("KICK")
public class Kick extends Reaction {

    public Kick(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
