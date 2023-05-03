package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.*;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

/**
 *
 *
 * Players sends this request if they are the host of the game and they want to
 * kick other player from the lobby.
 * In reaction server sends to all players message of type `KICK_ANNOUNCEMENT`
 * announcing that this has happend.
 * 
 * Example of user request
 * {
 * "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 * "type": "KICK",
 * "data": {
 * "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 * "kickedUserUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 * }
 * }
 * 
 * Example of server announcement
 * {
 * "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 * "type": "KICK_ANNOUNCEMENT",
 * "result": "OK",
 * "data": {
 * "kickedUserUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 * }
 * }
 * 
 * 
 */

@ReactionName("KICK")
public class Kick extends Reaction {

    public Kick(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @DataClass
    public static class DataDTO {
        private final UUID userUuid;
        private final UUID kickedUserUuid;

        public DataDTO(UUID userUuid, UUID kickedUserUuid) {
            this.userUuid = userUuid;
            this.kickedUserUuid = kickedUserUuid;
        }

    }

    // ----> EXAMPLE USER REQUEST <----

    // {
    // "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
    // "type": "KICK",
    // "data": {
    // "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
    // "kickedUserUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
    // }
    // }

    public class ResponseData {
        public UUID kickedUserUuid;

        public ResponseData(UUID kickedUserUuid) {
            this.kickedUserUuid = kickedUserUuid;
        }

    }

    // ----> EXAMPLE SERVER RESPONSE <----

    // {
    // "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
    // "type": "KICK_ANNOUNCEMENT",
    // "result": "OK",
    // "data": {
    // "kickedUserUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
    // }
    // }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {
            validateData(dataDTO, database);

            UUID userUuid = dataDTO.userUuid;
            UUID kickedUserUuid = dataDTO.kickedUserUuid;

            Room room = database.getRoomWithUser(userUuid);
            User userToBeKicked = database.getUser(kickedUserUuid);

            room.leaveGame(userToBeKicked);

            ResponseData responseData = new ResponseData(kickedUserUuid);
            ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(),
                    ServerMessageType.KICK_ANNOUNCEMENT,
                    Result.OK, responseData);

            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
            }

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(),
                    ServerMessageType.KICK_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }

    }

    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, PerrmissionDeniedExeption, UserNotAMemberException {
        // Check if user's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.userUuid.toString()))
            throw new InvalidUUIDException("Invalid user UUID format.");

        // Check if user's to be kicked UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.kickedUserUuid.toString()))
            throw new InvalidUUIDException("Invalid user to be kicked UUID format.");

        // checks if user-owner and user-to-be-kicked are not the same user
        if (dataDTO.userUuid.equals(dataDTO.kickedUserUuid))
            throw new InvalidUUIDException("You can not kick yourself mate.");


        // Check if room exists
        Room room = database.getRoomWithUser(dataDTO.userUuid);
        if (room == null)
            throw new UserNotAMemberException("You are not a member of any room!");


        // Check if owner is in fact owner
        User roomOwner = database.getUser(dataDTO.userUuid);
        if (room.getOwner() != roomOwner)
            throw new PerrmissionDeniedExeption("You need to be a room owner to kick players.");


        // Check if user to be kicked is in the same room as owner
        User userToBeKicked = database.getUser(dataDTO.kickedUserUuid);
        if (!room.userExists(userToBeKicked))
            throw new UserNotAMemberException("Specified user is not a member of your room!");
    }

}
