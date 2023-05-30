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
import com.github.splendor_mobile_game.game.enums.Regex;
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
 *          "userUuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
 *          "tokensTakenDTO": {
 *              "ruby": 1,
 *              "sapphire": 1,
 *              "emerald": 1,
 *              "diamond": 0,
 *              "onyx": 0
 *          },
 *          "tokensReturnedDTO": {
 *              "ruby": 0,
 *              "sapphire": 1,
 *              "emerald": 0,
 *              "diamond": 0,
 *              "onyx": 0
 *          }
 *      }
 * }
 * or something like this
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS",
 *      "data": {
 *          "userUuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
 *          "tokensTakenDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 2,
 *              "onyx": 0
 *          },
 *          "tokensReturnedDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 0,
 *              "onyx": 0
 *          }
 *      }
 * }
 * if the situation arises that player would get more than 10 tokens in total,
 * then player have to give back some other tokens, so the following might be possible.
 * Consider a player has 9 tokens. They get 2 more and give 1, so the balance is 10 at max.
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS",
 *      "data": {
 *          "userUuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
 *          "tokensTakenDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 2,
 *              "onyx": 0
 *          },
 *          "tokensReturnedDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 1,
 *              "onyx": 0
 *          }
 *      }
 * }
 * 
 * Example of server announcement
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GET_TOKENS_RESPONSE",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *          "tokensTakenDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 2,
 *              "onyx": 0
 *          },
 *          "tokensReturnedDTO": {
 *              "ruby": 0,
 *              "sapphire": 0,
 *              "emerald": 0,
 *              "diamond": 1,
 *              "onyx": 0
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
 *          "error": "It's not your turn!"
 *      }
 * }
 * 
 */
@ReactionName("GET_TOKENS")
public class GetTokens extends Reaction {

    public GetTokens(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class TokensChangeDTO {
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
    }

    @DataClass
    public static class DataDTO {
        public UUID userUuid;
        public TokensChangeDTO tokensTakenDTO;
        public TokensChangeDTO tokensReturnedDTO;

        public DataDTO(UUID userUuid, TokensChangeDTO tokensTakenDTO, TokensChangeDTO tokensReturnedDTO) {
            this.userUuid = userUuid;
            this.tokensTakenDTO = tokensTakenDTO;
            this.tokensReturnedDTO = tokensReturnedDTO;
        }
    }

    public static class ResponseData {
        public DataDTO data;

        public ResponseData(DataDTO data) {
            this.data = data;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();
       
        try {
            Map<TokenType, Integer> tokensTaken = new HashMap<TokenType, Integer>();
            tokensTaken.put(TokenType.RUBY, dataDTO.tokensTakenDTO.ruby);
            tokensTaken.put(TokenType.SAPPHIRE, dataDTO.tokensTakenDTO.sapphire);
            tokensTaken.put(TokenType.EMERALD, dataDTO.tokensTakenDTO.emerald);
            tokensTaken.put(TokenType.DIAMOND, dataDTO.tokensTakenDTO.diamond);
            tokensTaken.put(TokenType.ONYX, dataDTO.tokensTakenDTO.onyx);

            Map<TokenType, Integer> tokensReturned = new HashMap<TokenType, Integer>();
            tokensReturned.put(TokenType.RUBY, Math.abs(dataDTO.tokensReturnedDTO.ruby));
            tokensReturned.put(TokenType.SAPPHIRE, Math.abs(dataDTO.tokensReturnedDTO.sapphire));
            tokensReturned.put(TokenType.EMERALD, Math.abs(dataDTO.tokensReturnedDTO.emerald));
            tokensReturned.put(TokenType.DIAMOND, Math.abs(dataDTO.tokensReturnedDTO.diamond));
            tokensReturned.put(TokenType.ONYX, Math.abs(dataDTO.tokensReturnedDTO.onyx));

            validateData(dataDTO, database, tokensTaken, tokensReturned);
            
            Room room = database.getRoomWithUser(dataDTO.userUuid);
            User user = database.getUser(dataDTO.userUuid);

            changeTokens(user, room, tokensTaken, tokensReturned);
            user.setPerformedAction(true);

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

    private void validateData(DataDTO dataDTO, Database database, Map<TokenType, Integer> tokensTaken, Map<TokenType, Integer> tokensReturned) throws RoomDoesntExistException, TooManyTokensException, TooManyReturnedTokensException, WrongTokenChoiceException, InvalidUUIDException, NotThisUserTurnException, GameNotStartedException {
        if(database.getUser(dataDTO.userUuid) == null) throw new InvalidUUIDException("User with this UUID not found");
        if(database.getRoomWithUser(dataDTO.userUuid) == null) throw new RoomDoesntExistException("This user isn't in any room");

        Room room = database.getRoomWithUser(dataDTO.userUuid);
        User user = database.getUser(dataDTO.userUuid);

        if(room.getGame() == null) room.startGame(); //to delete!!!!!!
        
        if(room.getGame() == null) throw new GameNotStartedException("You can't take tokens when game didn't start");

        if(!room.getCurrentPlayer().getUuid().equals(user.getUuid())) throw new NotThisUserTurnException("It's not your turn");

        if(user.hasPerformedAction()) throw new NotThisUserTurnException("You've already made an action this round");

        if(!mustUserReturnTokens(room, user, tokensTaken) && isUserTryingToReturnTokens(room, user, tokensReturned)) {
            throw new TooManyReturnedTokensException("You are trying to return tokens when you already have less than 10");
        }

        if(isUserTryingToReturnTokens(room, user, tokensReturned) && !canUserReturnChosenTokens(room, user, tokensReturned)) {
            throw new WrongTokenChoiceException("You don't have enough tokens to return");
        }

        if(mustUserReturnTokens(room, user, tokensTaken) && finalTokenAmount(room, user, tokensTaken, tokensReturned) < 10) {
            throw new TooManyReturnedTokensException("You are trying to return too many tokens");
        }

        if(finalTokenAmount(room, user, tokensTaken, tokensReturned) > 10) {
            throw new TooManyTokensException("You would have too many tokens");
        }

        isTokensCombinationRight(room, user, tokensTaken);
    }

    
    //helper functions
    private boolean mustUserReturnTokens(Room room, User user, Map<TokenType, Integer> tokensTaken) {
        int tokenSum = user.getTokenCount();

        for(Map.Entry<TokenType, Integer> set : tokensTaken.entrySet()) {
            tokenSum += set.getValue();
        }

        if(tokenSum > 10) return true;
        return false;
    }

    private boolean isUserTryingToReturnTokens(Room room, User user, Map<TokenType, Integer> tokensReturned) {
        for(Map.Entry<TokenType, Integer> set : tokensReturned.entrySet()) {
            if(set.getValue() > 0) return true;
        }

        return false;
    }

    private boolean canUserReturnChosenTokens(Room room, User user, Map<TokenType, Integer> tokensReturned) {
        for(Map.Entry<TokenType, Integer> set : tokensReturned.entrySet()) {
            if(set.getValue() > user.getTokenCount(set.getKey())) return false;
        }

        return true;
    }

    private int finalTokenAmount(Room room, User user, Map<TokenType, Integer> tokensTaken, Map<TokenType, Integer> tokensReturned) {
        int tokenSum = user.getTokenCount();

        for(Map.Entry<TokenType, Integer> set : tokensTaken.entrySet()) {
            tokenSum += set.getValue();
        }

        for(Map.Entry<TokenType, Integer> set : tokensReturned.entrySet()) {
            tokenSum -= set.getValue();
        }

        return tokenSum;
    }

    private boolean isTokensCombinationRight(Room room, User user, Map<TokenType, Integer> tokensTaken) throws WrongTokenChoiceException {
        ArrayList<TokenType> twoTokenTypes = new ArrayList<TokenType>();
        ArrayList<TokenType> oneTokenTypes = new ArrayList<TokenType>();

        for(Map.Entry<TokenType, Integer> set : tokensTaken.entrySet()) {
            if(set.getValue() > 2){
                throw new WrongTokenChoiceException(String.format("You've choosen too many %s tokens", set.getKey()));
            }
            if(set.getValue() < 0) {
                throw new WrongTokenChoiceException(String.format("You've choosen not enough %s tokens", set.getKey()));
            }
            if(set.getValue() == 2) twoTokenTypes.add(set.getKey());
            if(set.getValue() == 1) oneTokenTypes.add(set.getKey());
        }

        if(twoTokenTypes.size() == 1 && oneTokenTypes.size() == 0) {
            if(room.getGame().getTokenCount(twoTokenTypes.get(0)) >= 4) return true;
            else throw new WrongTokenChoiceException(String.format("There are not enough %s tokens on the table", twoTokenTypes.get(0)));
        }

        for (TokenType type : oneTokenTypes) {
            if(room.getGame().getTokenCount(type) <= 0) throw new WrongTokenChoiceException(String.format("There are not enough %s tokens on the table", type));
        }

        if(oneTokenTypes.size() == 3 && twoTokenTypes.size() == 0) return true;

        Map<TokenType, Integer> tokensOnTableAmount = new HashMap<TokenType, Integer>();

        for(Map.Entry<TokenType, Integer> set : tokensTaken.entrySet()) {
            tokensOnTableAmount.put(set.getKey(), room.getGame().getTokenCount(set.getKey()));
        }

        if(oneTokenTypes.size() == 2 && twoTokenTypes.size() == 0) {
            for(Map.Entry<TokenType, Integer> set : tokensOnTableAmount.entrySet()) {
                if(set.getKey() != oneTokenTypes.get(0) && set.getKey() != oneTokenTypes.get(1) && set.getValue() != 0) {
                    throw new WrongTokenChoiceException("You can take 3x1 tokens");
                }
            }
            return true;
        }

        if(oneTokenTypes.size() == 1 && twoTokenTypes.size() == 0) {
            for(Map.Entry<TokenType, Integer> set : tokensOnTableAmount.entrySet()) {
                if(set.getKey()!= oneTokenTypes.get(0) && set.getValue() != 0) {
                    throw new WrongTokenChoiceException("You can take 3x1 tokens");
                }
            }
            return true;
        }

        throw new WrongTokenChoiceException("Your token choice is wrong");
    }
    
    private void changeTokens(User user, Room room, Map<TokenType, Integer> tokensTaken, Map<TokenType, Integer> tokensReturned) {
        Map<TokenType, Integer> tokensChange = new HashMap<TokenType, Integer>();

        for(Map.Entry<TokenType, Integer> set : tokensTaken.entrySet()) {
            tokensChange.put(set.getKey(), set.getValue() - tokensReturned.get(set.getKey()));
        }
        
        user.changeTokens(tokensChange);
        room.getGame().changeTokens(tokensChange);
    }
}
