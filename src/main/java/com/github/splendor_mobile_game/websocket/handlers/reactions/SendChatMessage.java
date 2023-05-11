package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Regex;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotAMemberException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

import java.util.UUID;

/**
 *  ------------- IMPORTANT ------------------
 *  #### THIS CLASS IS ADDITIONAL. IT IS NOT REQUIRED TO KEEP GAME WORKING PROPERLY
 *  ------------- IMPORTANT ------------------
 *
 *
 * Player sends this request if they wants to send chat message to the other players in the same room.
 * In reaction server sends to all players message of type `CHAT_MESSAGE_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "SEND_CHAT_MESSAGE",
 *      "data": {
 *          "userUuid": "288e001c-6510-4c88-8580-2dbe7aa2bfff",
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
 *          "userUuid": "288e001c-6510-4c88-8580-2dbe7aa2bfff",
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

    public static class DataDTO{
        private UUID userUuid;
        private String message;

        public DataDTO(UUID userUuid, String message) {
            this.userUuid = userUuid;
            this.message = message;
        }
    }

    public static class ResponseData {
        private UUID userUuid;
        private String message;

        public ResponseData(UUID userUuid, String message) {
            this.userUuid = userUuid;
            this.message = message;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {
            validateData(dataDTO, database);

            Room room = database.getRoom(dataDTO.userUuid);

            room.getChat().addMessage(dataDTO.message, dataDTO.userUuid);

            ResponseData responseData = new ResponseData(dataDTO.userUuid, dataDTO.message);
            ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.SEND_CHAT_MESSAGE_ANNOUNCEMENT, Result.OK, responseData);

            // Send join information to all players
            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
            }
        }
        catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.SEND_CHAT_MESSAGE_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }

    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, UserDoesntExistException, UserNotAMemberException {
        // Check if user's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.userUuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");

        // Check if user exists
        User user = database.getUser(dataDTO.userUuid);
        if (user == null)
            throw new UserDoesntExistException("Couldn't find a user with given UUID.");

        //Check if user is in any room
        Room room = database.getRoomWithUser(user.getUuid());
        if (room == null)
            throw new UserNotAMemberException("You are not a member of any room!");
    }
    
}
