package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomInGameException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserTurnException;
import com.github.splendor_mobile_game.websocket.handlers.reactions.CreateRoom.DataDTO;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;

/**
 * This reaction handles the request sent by a player to end their turn. The server sends a message of type `NEW_TURN_ANNOUNCEMENT` to all players, announcing the end of the current turn and selecting the player for the next turn. If a player sends an invalid request, the server sends a response only to the requester.
 * 
 * Example of a valid user request:
 * 
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "END_TURN",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *      }
 * }
 * 
 * Example of a successful server announcement:
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "NEW_TURN_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "nextUserUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *      }
 * }
 *
 * Implementation details:
 * - The player who sent the message is identified by their WebSocket's connectionHashCode.
 * - The implementation should include logic to store information about whose turn it is and the order of turns.
 * 
 * This reaction is also responsible for detecting when the game ends. The game ends when one player has more than 15 points and the server needs to complete the current turn so that every player has done the same amount of actions.
 * 
 * If a player sends an invalid request, such as when it is not their turn or they are not in any game, the server sends a response only to the requester. For example:
 *
 * {
 *     "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *     "type": "END_TURN_RESPONSE",
 *     "result": "FAILURE",
 *     "data": {
 *         "error": "You cannot end turn if it's not your turn!"
 *     }
 * }
 * 
 */
@ReactionName("END_TURN")
public class EndTurn extends Reaction {

    public EndTurn(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @DataClass
    public static class DataDTO {
        public UUID userUuid;

        public DataDTO(UUID userUuid) {
            this.userUuid = userUuid;
        }
    }

    public class ResponseData {
        public UUID nextUserUuid;

        public ResponseData(UUID nextUserUuid) {
            this.nextUserUuid = nextUserUuid;
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
            if (user.getPoints() >= 15){
                room.setLastTurn(true);
            }
            if (room.getLastTurn() && room.isPlayersMovesEqual()) {
                room.endGame();
            }                

            UUID nextUserUUID = room.getCurrentPlayer().getUuid();
            ResponseData responseData = new ResponseData(nextUserUUID);
            ServerMessage serverMessage = new ServerMessage(
                userMessage.getContextId(), 
                ServerMessageType.NEW_TURN_ANNOUNCEMENT, 
                Result.OK, 
                responseData);

                for(User u : room.getAllUsers()){
                    messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage); 
                }

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                Result.FAILURE,
                e.getMessage(),
                ServerMessageType.END_TURN_RESPONSE,
                userMessage.getContextId().toString());
                messenger.addMessageToSend(connectionHashCode, errorResponse);
        }         
    }
    
    private void validateData(DataDTO dataDTO, Database database) 
    throws UserDoesntExistException, RoomDoesntExistException, RoomInGameException,
    UserTurnException, InvalidUUIDException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"); 

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userUuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); 
        
        User player = database.getUser(dataDTO.userUuid);

        // Check if user exists
        if (player == null) {
            throw new UserDoesntExistException("There is no such user in the database");
        }

        Room room = database.getRoomWithUser(player.getUuid());

        // check if room exists
        if (room == null) {
            throw new RoomDoesntExistException("Room does not exist whose user is a member of");
        }

        // checks if player has performed an action
        if (!player.hasPerformedAction()) {
            throw new UserDoesntExistException("User has to perform an action before ending a turn");
        }

        Game game = room.getGame();

        // Check if user is in game
        if (game == null) {
            throw new RoomInGameException("The room is not in game state");
        }

        // Check if it is user's turn
        if (room.getCurrentPlayer() != player) {
            throw new UserTurnException("It is not a user's turn");
        }

    }
}
