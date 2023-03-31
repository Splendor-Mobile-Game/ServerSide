package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Player sends this request if they wants to send chat message to the other players in the same room.
 * In reaction server sends to all players message of type `CHAT_MESSAGE_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "SEND_CHAT_MESSAGE",
 *      "data": {
 *          "message": "Hello my friends! How are you?"
 *      }
 * }
 * 
 * Example of server announcement
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "CHAT_MESSAGE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "playerUuid": "288e001c-6510-4c88-8580-2dbe7aa2bfff",
 *          "message": "Hello my friends! How are you?"
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - Probably there is need to implement some logic in the model package. 
 *   We need to store information about messages.
 * 
 * Also.. Consider user is sending dodgy request, because they wants to break the software.
 * They send message when they aren't in any game for example.
 * Please think of every situation, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "SEND_CHAT_MESSAGE_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot send chat message when you aren't in any room!"
 *      }
 * }
 * 
 */
@ReactionName("SEND_CHAT_MESSAGE")
public class SendChatMessage extends Reaction {

    public SendChatMessage(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
