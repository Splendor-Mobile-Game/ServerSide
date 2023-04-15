package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.GameNotStartedException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.NotThisUserTurnException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.TooManyReturnedTokensException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.TooManyTokensException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotAMemberException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotFoundException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.WrongTokenChoiceException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

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
           "userUuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
           "tokensChangeDTO": {
                "ruby": 2,
                "sapphire": 0,
                "emerald": 0,
                "diamond": 0,
                "onyx": 0
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
            if(sapphire > 0) result += sapphire;
            if(emerald > 0) result += emerald;
            if(diamond > 0) result += diamond;
            if(onyx > 0) result += onyx;

            return result;
        }

        public int getReturnedTokenCount() {
            int result = 0;
            if(ruby < 0) result += ruby;
            if(sapphire < 0) result += sapphire;
            if(emerald < 0) result += emerald;
            if(diamond < 0) result += diamond;
            if(onyx < 0) result += onyx;

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

    public class ResponseData {
        DataDTO data;

        public ResponseData(DataDTO data) {
            this.data = data;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();
       
        try {
            Map<TokenType, Integer> tokenMap = new HashMap<TokenType, Integer>();
            tokenMap.put(TokenType.RUBY, dataDTO.tokensChangeDTO.ruby);
            tokenMap.put(TokenType.SAPPHIRE, dataDTO.tokensChangeDTO.sapphire);
            tokenMap.put(TokenType.EMERALD, dataDTO.tokensChangeDTO.emerald);
            tokenMap.put(TokenType.DIAMOND, dataDTO.tokensChangeDTO.diamond);
            tokenMap.put(TokenType.ONYX, dataDTO.tokensChangeDTO.onyx);

            validateData(dataDTO, database, tokenMap);
            
            Room room = database.getRoomWithUser(dataDTO.userUuid);
            User user = database.getUser(dataDTO.userUuid);

            // System.out.println("ruby on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.RUBY));

            // System.out.println("sapphire on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.SAPPHIRE));

            // System.out.println("emerald on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.EMERALD));

            // System.out.println("diamond on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.DIAMOND));

            // System.out.println("onyx on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.ONYX));

            // System.out.println("---------------------------------------------------");

            // System.out.println("ruby on user");
            // System.out.println(user.getTokenCount(TokenType.RUBY));

            // System.out.println("sapphire on user");
            // System.out.println(user.getTokenCount(TokenType.SAPPHIRE));

            // System.out.println("emerald on user");
            // System.out.println(user.getTokenCount(TokenType.EMERALD));

            // System.out.println("diamond on user");
            // System.out.println(user.getTokenCount(TokenType.DIAMOND));

            // System.out.println("onyx on user");
            // System.out.println(user.getTokenCount(TokenType.ONYX));


            // System.out.println();
            // System.out.println();
            // System.out.println();

            changeTokens(user, room, tokenMap);

            // System.out.println("ruby on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.RUBY));

            // System.out.println("sapphire on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.SAPPHIRE));

            // System.out.println("emerald on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.EMERALD));

            // System.out.println("diamond on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.DIAMOND));

            // System.out.println("onyx on table");
            // System.out.println(room.getGame().getTokenCount(TokenType.ONYX));

            // System.out.println("---------------------------------------------------");

            // System.out.println("ruby on user");
            // System.out.println(user.getTokenCount(TokenType.RUBY));

            // System.out.println("sapphire on user");
            // System.out.println(user.getTokenCount(TokenType.SAPPHIRE));

            // System.out.println("emerald on user");
            // System.out.println(user.getTokenCount(TokenType.EMERALD));

            // System.out.println("diamond on user");
            // System.out.println(user.getTokenCount(TokenType.DIAMOND));

            // System.out.println("onyx on user");
            // System.out.println(user.getTokenCount(TokenType.ONYX));

            ResponseData responseData = new ResponseData(dataDTO);

            ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.GET_TOKENS_RESPONSE, Result.OK, responseData);
            
            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
            }
        } catch (Exception e) {
            Log.ERROR(e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.GET_TOKENS_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(this.connectionHashCode, errorResponse);
        }
    }

    private void validateData(DataDTO dataDTO, Database database, Map<TokenType, Integer> tokenMap) throws RoomDoesntExistException, TooManyTokensException, TooManyReturnedTokensException, WrongTokenChoiceException, InvalidUUIDException, NotThisUserTurnException, GameNotStartedException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userUuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern

        if(database.getRoomWithUser(dataDTO.userUuid) == null) throw new RoomDoesntExistException("Room with this user not found");

        Room room = database.getRoomWithUser(dataDTO.userUuid);
        User user = database.getUser(dataDTO.userUuid);

        if(user.hasPerformedAction()) throw new NotThisUserTurnException("It's not your turn");

        //only for testing, will be removed when start game reaction will be done
        if(room.getGame() == null) {
            room.startGame(); 
        }
        //end only for testing

        if(room.getGame() == null) throw new GameNotStartedException("Game hasn't started yet");

        // System.out.println("USER TOKENS: ");
        // System.out.println(user.getTokenCount());

        // System.out.println("GET ADDED TOKEN COUNT: ");
        // System.out.println(dataDTO.tokensChangeDTO.getAddedTokenCount());

        int tokensWithAdded = user.getTokenCount() + dataDTO.tokensChangeDTO.getAddedTokenCount();
        int tokensWithAddedAndReturned = tokensWithAdded - dataDTO.tokensChangeDTO.getReturnedTokenCount();

        if(tokensWithAddedAndReturned > 10) throw new TooManyTokensException("You can't have more than 10 tokens");
        if(tokensWithAdded > 10 && tokensWithAddedAndReturned < 10)  throw new TooManyReturnedTokensException("If you return tokens, you have to finish on 10");

        if(!tokenAmountCheck(user, tokenMap, room)) throw new WrongTokenChoiceException("You haven't choosen tokens correctly");

    }

    private boolean tokenAmountCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {

        //checking if user has enough tokens for possible return
        for(Map.Entry<TokenType, Integer> set : tokenMap.entrySet()) {
            if(set.getValue() < 0 && set.getValue()*(-1) > user.getTokenCount(set.getKey())) return false;
        }

        boolean isUserTakingTwo = twoTokensTakenCheck(user, tokenMap, room);
        boolean isUserTakingThree = threeTokensTakenCheck(user, tokenMap, room);

        //checking if user tries to take two tokens
        if(isUserTakingTwo && !isUserTakingThree) return true;
        
        //checking if user tries to take three tokens
        if(!isUserTakingTwo && isUserTakingThree) return true;

        //if user isn't trying to take two or three tokens returning false
        return false;
    }

    private boolean twoTokensTakenCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {

        ArrayList<TokenType> twoTokensTypes = new ArrayList<TokenType>();
        ArrayList<TokenType> oneTokenTypes = new ArrayList<TokenType>();
        int negativeTokenAmount = 0;

        for(Map.Entry<TokenType, Integer> set : tokenMap.entrySet()) {
            //if user tries to take more than 2 tokens of some type, he definitelly doesn't try to take 2
            if(set.getValue() > 2) return false;

            //checking what types user tries to take 2 tokens of
            if(set.getValue() == 2) {
                if(room.getGame().getTokenCount(set.getKey()) <= 0) return false;
                twoTokensTypes.add(set.getKey());
            }

            //checking same thing considering user might try to already return one of taken tokens
            if(set.getValue() == 1) {
                if(room.getGame().getTokenCount(set.getKey()) <= 0) return false;
                oneTokenTypes.add(set.getKey());
            }

            //checking how many types user is trying to return
            if(set.getValue() < 0) negativeTokenAmount++;
        }

        //checking if user tries to return some tokens when he can't return anything
        if(user.getTokenCount() <= 8 && negativeTokenAmount > 0) return false;

        if(twoTokensTypes.size() == 1 && oneTokenTypes.size() == 0) {
            //checking if user can take two tokens of type he is trying to
            if(room.getGame().getTokenCount(twoTokensTypes.get(0)) < 4) return false;
            return true;
        }
        else if(user.getTokenCount() == 9 && oneTokenTypes.size() == 1 && twoTokensTypes.size() == 0) {
            //as above but considering user is trying to already return one of taken tokens
            if(room.getGame().getTokenCount(oneTokenTypes.get(0)) < 4) return false;
            return true;
        }
        //now we are sure ( I hope :) ) user isn't trying to take two tokens so we can return false
        else return false;
    }

    private boolean threeTokensTakenCheck(User user, Map<TokenType, Integer> tokenMap, Room room) {
        int oneTokenAmount = 0;
        int negativeTokenAmount = 0;

        for(Map.Entry<TokenType, Integer> set : tokenMap.entrySet()) {
            //if user is trying more than one token of specific type he definitelly doesn't try to take three different tokens
            if(set.getValue() > 1) return false;

            //checking which types user is trying to take
            if(set.getValue() == 1) {
                if(room.getGame().getTokenCount(set.getKey()) <= 0) return false;
                oneTokenAmount++;
            }

            //checking if user tries to return some tokens
            if(set.getValue() < 0) negativeTokenAmount++;
        }

        if(user.getTokenCount() <= 7 && negativeTokenAmount > 0) return false;

        if(negativeTokenAmount == 0) {
            if(user.getTokenCount() <= 7 && oneTokenAmount == 3) return true;
            if(user.getTokenCount() == 8 && oneTokenAmount == 2) return true;
            if(user.getTokenCount() == 9 && oneTokenAmount == 1) return true;
        } else if(negativeTokenAmount == 1) {
            if(user.getTokenCount() == 8 && oneTokenAmount == 3) return true;
            if(user.getTokenCount() == 9 && oneTokenAmount == 2) return true;
        } else if(negativeTokenAmount == 2) {
            if(user.getTokenCount() == 9 && oneTokenAmount == 3) return true;
        }

        return false;
    }
    
    private void changeTokens(User user, Room room, Map<TokenType, Integer> tokenMap) {
        user.changeTokens(tokenMap);
        room.getGame().changeTokens(tokenMap);
    }
}
