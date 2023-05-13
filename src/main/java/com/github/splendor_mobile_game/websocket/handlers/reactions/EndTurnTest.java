package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Regex;
import com.github.splendor_mobile_game.game.exceptions.CanPerformAnActionException;
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
 *
 *
 * This reaction is also responsible for detecting when the game ends.
 * The game ends when one player has more than 15 points and the server needs to complete the current turn so that every player has done the same amount of actions.
 * The implementation should also include logic to store information about whose turn it is and the order of turns.
 *
 * If user wanted to end his turn, and he couldn't do an action during current round then the NEW_TURN_ANNOUNCEMENT will be sent.
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
 *
 *
 *
 * During the EndTurn process nobles might visit the current user automatically. Maximum amount of noble visits per round is equal to 1.
 * When user has enough points from cards to make a noble visit him - the announcement should be sent to all users inside the room.
 * Example NOBLE_RECEIVED_ANNOUNCEMENT:
 *
 * {
 *     "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *     "type": "NOBLE_RECEIVED_ANNOUNCEMENT",
 *     "result": "OK",
 *     "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "nobleUuid": "59913c86-bc7e-44a4-ad8e-2ffadd574df3"
 *     }
 * }
 *
 *
 *
 *
 * If a player sends an invalid request, such as when it is not their turn, or they are not in any game, the server sends a response only to the requester. For example:
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
@ReactionName("END_TURN_TEST")
public class EndTurnTest extends Reaction {

    public EndTurnTest(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @DataClass
    public static class DataDTO {
        public UUID userUuid;

        public DataDTO(UUID userUuid) {
            this.userUuid = userUuid;
        }
    }

    public class PlayerDataResponse {
        public UUID playerUUID;
        public int points;
        public int place;

        public PlayerDataResponse(UUID playerUUID, int points, int place){
            this.playerUUID = playerUUID;
            this.points = points;
            this.place = place;
        }
    }

    public class ResponseData {
        public UUID nextUserUuid;

        public ResponseData(UUID nextUserUuid) {
            this.nextUserUuid = nextUserUuid;
        }
    }

    public class ResponseDataEndGame {
        public ArrayList<PlayerDataResponse> playerRanking;

        public ResponseDataEndGame(ArrayList<PlayerDataResponse> playerRanking) {
            this.playerRanking = playerRanking;
        }
    }


    public class ResponseDataNobleReceived {
        public UUID userUuid;
        public UUID nobleUuid;

        public ResponseDataNobleReceived(UUID userUuid, UUID nobleUuid) {
            this.userUuid = userUuid;
            this.nobleUuid = nobleUuid;
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
            User user = database.getUserByConnectionHashCode(connectionHashCode);
            Room room = database.getRoomWithUser(user.getUuid());
            Game game = room.getGame();
            ServerMessage serverMessage;


            ArrayList<PlayerDataResponse> playerRanking = new ArrayList<PlayerDataResponse>();
            ArrayList<User> users = room.getAllUsers();
            Collections.sort(users);

            for (User player : users)
                playerRanking.add(new PlayerDataResponse(player.getUuid(), player.getPoints(), game.getUserRanking(user.getUuid())));

            ResponseDataEndGame responseData = new ResponseDataEndGame(playerRanking);
            serverMessage = new ServerMessage(
                userMessage.getContextId(),
                ServerMessageType.END_GAME_ANNOUNCEMENT,
                Result.OK,
                responseData);



            for (User u : room.getAllUsers())
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);


        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                Result.FAILURE,
                e.getMessage(),
                ServerMessageType.END_TURN_RESPONSE,
                userMessage.getContextId().toString());
                messenger.addMessageToSend(connectionHashCode, errorResponse);
        }         
    }

}
