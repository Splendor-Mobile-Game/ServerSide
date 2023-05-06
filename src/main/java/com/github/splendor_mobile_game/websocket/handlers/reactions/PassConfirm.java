package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Regex;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Noble;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.*;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.*;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * This class handles the `PASS` reaction. Players can send this request only if they haven't done any other action and it's impossible by the rules of the game to do any action. This will mark the player that they did an action. In reaction, the server sends to all players a message of type `PASS_ANNOUNCEMENT` announcing that this has happened.
 *
 * Example of user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_CONFIRM",
 *      "data": {
 *         "userUuid": "9fc1845e-5469-458d-9893-07d390908479"
 *      }
 * }
 *
 *
 * Example of server announcement (where userUUID is uuid of next player's turn):
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "PASS_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "9fc1845e-5469-458d-9893-07d390908479"
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
@ReactionName("PASS_CONFIRM")
public class PassConfirm extends Reaction {

    public PassConfirm(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }


    @DataClass
    public static class DataDTO {
        public UUID userUuid;

        public DataDTO(UUID userUuid) {
            this.userUuid = userUuid;
        }
    }


    public class ResponseDataPass {
        public UUID userUuid;

        public ResponseDataPass(UUID userUuid) {
            this.userUuid = userUuid;
        }
    }


    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {
            validateData(dataDTO, database);

            User user = database.getUserByConnectionHashCode(connectionHashCode);
            Room room = database.getRoomWithUser(user.getUuid());


            room.changeTurn();

            // User can't do anything. Skip his turn
            ResponseDataPass responseData = new ResponseDataPass(room.getCurrentPlayer().getUuid());
            ServerMessage serverMessage = new ServerMessage(
                    userMessage.getContextId(),
                    ServerMessageType.PASS_ANNOUNCEMENT,
                    Result.OK,
                    responseData);

            for (User u : room.getAllUsers())
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);



        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    Result.FAILURE,
                    e.getMessage(),
                    ServerMessageType.PASS_CONFIRM_RESPONSE,
                    userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }



    private void validateData(DataDTO dataDTO, Database database) throws UserDoesntExistException, UserTurnException, InvalidUUIDException, UserNotAMemberException, GameNotStartedException {
        // Check if user's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.userUuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");


        User user = database.getUser(dataDTO.userUuid);
        // Check if user exists
        if (user == null) throw new UserDoesntExistException("There is no such user in the database");


        Room room = database.getRoomWithUser(user.getUuid());
        // Check if room exists
        if (room == null) throw new UserNotAMemberException("You are not a member of any room!");

        // Check if game is running
        if (room.getGame() == null) throw new GameNotStartedException("Game hasn't started yet");

        // Check if it is user's turn
        if (room.getCurrentPlayer() != user) throw new UserTurnException("It's not your turn");

        // Check if user did some action. If not, inform others that he didn't do anything this round.
        if (!user.hasPerformedAction()) throw new UserTurnException("You performed some actions. Can't pass the turn.");

    }
}
