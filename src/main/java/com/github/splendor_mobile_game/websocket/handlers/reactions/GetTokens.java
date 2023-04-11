package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Color;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.response.Result;

/**
 * Players send this request if now is their turn and they want to get tokens from the table.
 * In reaction server sends to all players message of type `GET_TOKENS_ANNOUNCEMENT` announcing that this has happend.
 * 
 * Example of user request
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS",
 *      "data": {
 *          "tokensChangeDTO": {
 *              "RUBY": 1,
 *              "SAPPHIRE": 1,
 *              "EMERALD": 1,
 *              "DIAMOND": 0,
 *              "ONYX": 0
 *          }
 *      }
 * }
 * or something like this
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS",
 *      "data": {
 *          "tokensChangeDTO": {
 *              "RUBY": 0,
 *              "SAPPHIRE": 2,
 *              "EMERALD": 0,
 *              "DIAMOND": 0,
 *              "ONYX": 0
 *          }
 *      }
 * }
 * if the situation arises that player would get more than 10 tokens in total,
 * then player have to give back some other tokens, so the following might be possible.
 * Consider a player has 9 tokens. They get 3 more and give back some 2, so the balance is 10 at max.
  {
       "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
       "type": "GET_TOKENS",
       "data": {
           "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
           "tokensChangeDTO": {
               "RUBY": 1,
*              "SAPPHIRE": 1,
*              "EMERALD": 1,
*              "DIAMOND": -2,
*              "ONYX": 0
           }
       }
  }
 * 
 * Example of server announcement
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS_RESPONSE",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "tokensChangeDTO": {
 *             "RUBY": 1,
*              "SAPPHIRE": 1,
*              "EMERALD": 1,
*              "DIAMOND": -2,
*              "ONYX": 0
 *          }
 *      }
 * }
 *
 * Some points about implementation:
 * - We know what player has sent this message because we have their WebSocket's connectionHashCode.
 * - Of course, we should update state of the game on the server (subtract appropriate amount of tokens for the purchase, add prestige points and add the bonus point)
 * - The way of representation of this data in json message is arbitrary, you as developer decides. Then android client will have to adjust. What's showed here is exemplary.
 * 
 * Also.. Consider user is sending dodgy request, because they wants to cheat. 
 * They send message to get tokens when there is turn of the other player.
 * they want to get only one token, but games allows only 2 the same ones or 3 different each (and exchange when above 10)
 * Please consider all possible scenarios, because this will be tested by testers.
 * 
 * In such invalid request you should send message only to the requester. For example
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot get tokens when this is not your turn!"
 *      }
 * }
 * 
 */
@ReactionName("GET_TOKENS")
public class GetTokens extends Reaction {

    public GetTokens(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public class TokensChangeDTO {
        public int red;
        public int blue;
        public int green;
        public int white;
        public int black;

        public TokensChangeDTO(int red, int blue, int green, int white, int black) {
            this.red = red;
            this.blue = blue;
            this.green = green;
            this.white = white;
            this.black = black;
        }
    }

    @DataClass
    public static class DataDTO {
        public UUID userUuid;
        public TokensChangeDTO tokensChangeDTO;

        public DataDTO(UUID userUuid, TokensChangeDTO tokensChangeDTO) {
            this.userUuid = userUuid;
            this.tokensChangeDTO = tokensChangeDTO;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();
       
        try {
            validateData(dataDTO, database);
            // Room room = database.getRoomWithUser(dataDTO.userUuid);
            // User user = room.getUserByUuid(dataDTO.userUuid);
            // System.out.println(user.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void validateData(DataDTO dataDTO, Database database) throws RoomDoesntExistException {
        if(database.getRoomWithUser(dataDTO.userUuid) == null) throw new RoomDoesntExistException("Room with this user not found");
    }
    
}
