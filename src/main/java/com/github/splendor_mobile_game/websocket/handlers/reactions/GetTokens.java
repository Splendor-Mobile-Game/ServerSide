package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.TooManyReturnedTokensException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.TooManyTokensException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotAMemberException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotFoundException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.WrongTokenChoiceException;
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
        public int ruby;
        public int sapphire;
        public int emerald;
        public int diamond;
        public int onyx;

        public TokensChangeDTO(int red, int blue, int green, int white, int black) {
            this.ruby = red;
            this.sapphire = blue;
            this.emerald = green;
            this.diamond = white;
            this.onyx = black;
        }

        public int getTokenCount() {
            return ruby + sapphire + emerald + diamond + onyx;
        }

        public int getAddedTokenCount() {
            int result = 0;
            if(ruby > 0) result += ruby;
            if(sapphire > 0) result += ruby;
            if(emerald > 0) result += ruby;
            if(diamond > 0) result += ruby;
            if(onyx > 0) result += ruby;

            return result;
        }

        public int getReturnedTokenCount() {
            int result = 0;
            if(ruby < 0) result += ruby;
            if(sapphire < 0) result += ruby;
            if(emerald < 0) result += ruby;
            if(diamond < 0) result += ruby;
            if(onyx < 0) result += ruby;

            return result * (-1);
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

    private void validateData(DataDTO dataDTO, Database database) throws RoomDoesntExistException, TooManyTokensException, TooManyReturnedTokensException, WrongTokenChoiceException {
        if(database.getRoomWithUser(dataDTO.userUuid) == null) throw new RoomDoesntExistException("Room with this user not found");

        Room room = database.getRoomWithUser(dataDTO.userUuid);
        User user = room.getUserByUuid(dataDTO.userUuid);

        Map<TokenType, Integer> tokenMap = new HashMap<TokenType, Integer>();

        tokenMap.put(TokenType.RUBY, dataDTO.tokensChangeDTO.ruby);
        tokenMap.put(TokenType.SAPPHIRE, dataDTO.tokensChangeDTO.sapphire);
        tokenMap.put(TokenType.EMERALD, dataDTO.tokensChangeDTO.emerald);
        tokenMap.put(TokenType.DIAMOND, dataDTO.tokensChangeDTO.diamond);
        tokenMap.put(TokenType.ONYX, dataDTO.tokensChangeDTO.onyx);

        int tokensWithAdded = user.getTokenCount() + dataDTO.tokensChangeDTO.getAddedTokenCount();
        int tokensWithAddedAndReturned = tokensWithAdded - dataDTO.tokensChangeDTO.getReturnedTokenCount();

        if(user.getTokenCount() + dataDTO.tokensChangeDTO.getTokenCount() > 10) throw new TooManyTokensException("You can't have more than 10 tokens");
        if(tokensWithAdded > 10 && tokensWithAddedAndReturned < 10)  throw new TooManyReturnedTokensException("If you return tokens, you have to finish on 10");

        if(!tokenAmountCheck(user, tokenMap, room)) throw new WrongTokenChoiceException("You haven't choosen tokens correctly");

    }

    private boolean tokenAmountCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {
        if(twoTokensTakenCheck(user, tokenMap, room) || threeTokensTakenCheck(user, tokenMap, room)) return true;
        return false;
    }

    private boolean twoTokensTakenCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {

        ArrayList<TokenType> twoTokensTypes = new ArrayList<TokenType>();
        ArrayList<TokenType> oneTokenTypes = new ArrayList<TokenType>();

        for(Map.Entry<TokenType, Integer> set : tokenMap.entrySet()) {
            if(set.getValue() > 2) return false;
            if(set.getValue() == 2) twoTokensTypes.add(set.getKey());
            if(set.getValue() == 1) oneTokenTypes.add(set.getKey());
        }

        if(twoTokensTypes.size() == 1 && oneTokenTypes.size() == 0) {
            if(room.getGame().getTokenCount(twoTokensTypes.get(0)) < 4) return false;
            return true;
        }
        else if(user.getTokenCount() == 9 && oneTokenTypes.size() == 1 && twoTokensTypes.size() == 0) {
            if(room.getGame().getTokenCount(oneTokenTypes.get(0)) < 4) return false;
            return true;
        }
        else return false;
    }

    private boolean threeTokensTakenCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {
        int oneTokenAmount = 0;

        for(Map.Entry<TokenType, Integer> set : tokenMap.entrySet()) {
            if(set.getValue() > 1) return false;
            if(set.getValue() == 1) oneTokenAmount++;
        }

        if(oneTokenAmount == 3) return true;
        if(oneTokenAmount == 2 && user.getTokenCount() == 8) return true;
        if(oneTokenAmount == 1 && user.getTokenCount() == 9) return true;
        return false;
    }
    
}
