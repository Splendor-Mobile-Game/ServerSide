package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.DeckIsEmptyException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.TokenCountException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserReservationException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * This request is sent by a player who wants to make a reservation from the deck during their turn.
 * Upon receiving this request, the server sends an announcement message of type `RESERVATION_FROM_DECK_ANNOUNCEMENT` to all players in the game, indicating that a reservation has been made.
 * 
 * Example of a user request:
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_DECK",
 *      "data": {
 *          "userUuid": "01901b0e-a78b-4a65-bbd3-0065948dc127",
 *          "cardTier": 1
 *      }
 * }
 * 
 * 
 * Example of a server announcement to all users:
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_DECK_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "userUuid":"01901b0e-a78b-4a65-bbd3-0065948dc127",
 *          "card": {
 *              "id": "0ba9cba8-3bc0-42fe-b24f-25d7b52fcd2c",
 *              "prestige": 2,
 *              "bonusColor": "EMERALD",
 *              "tokensRequired":{
 *                  "ruby": 2,
 *                  "emerald": 0,
 *                  "sapphire": 1,
 *                  "diamond": 3,
 *                  "onyx": 0
 *              }
 *          }
 *      }
 * }
 *
 * Implementation considerations:
 * - The player who sent the message can be identified by their WebSocket's connectionHashCode.
 * - The game's state must be updated in the database.
 * 
 * It is also important to consider that a user may send a fraudulent request in an attempt to cheat. For example, they may send a message when it is not their turn, they are not in any game, or they have already taken another action. All such scenarios must be anticipated and handled appropriately.
 * 
 * In the case of an invalid request, a message should be sent only to the requester, such as:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_DECK_RESPONSE",
 *      "result": "FAILURE"
 *      "data": {
 *          "error": "You cannot make a reservation if it's not your turn!"
 *      }
 * }
 * 
 */
@ReactionName("MAKE_RESERVATION_FROM_DECK")
public class MakeReservationFromDeck extends Reaction {

    public MakeReservationFromDeck(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @DataClass
    public static class DataDTO{
        public UUID userUuid;
        public int tier;

        public DataDTO(UUID userUuid, int tier){
            this.userUuid=userUuid;
            this.tier=tier;
        }
    }

    public class CardDataResponse{
        public UUID uuid;
        public int prestige;
        public TokenType bonusColor;
        public TokensDataResponse tokensRequired;


        public CardDataResponse(UUID uuid, int prestige, TokenType bonusColor, TokensDataResponse tokensRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.bonusColor = bonusColor;
            this.tokensRequired = tokensRequired;
        }    
    }

    public class TokensDataResponse{
        public int ruby;
        public int emerald;
        public int sapphire;
        public int diamond;
        public int onyx;


        public TokensDataResponse(int ruby, int emerald, int sapphire, int diamond, int onyx) {
            this.ruby = ruby;
            this.emerald = emerald;
            this.sapphire = sapphire;
            this.diamond = diamond;
            this.onyx = onyx;
        }      
    }

    public class ResponseData{
        public UUID userUuid;
        public CardDataResponse card;


        public ResponseData(UUID userUuid, CardDataResponse card) {
            this.userUuid = userUuid;
            this.card = card;
        }
    }

    @Override
    public void react() {
        
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try{
            validateData(dataDTO, database);

            User reservee = database.getUser(dataDTO.userUuid);
            Room room = database.getRoomWithUser(reservee.getUuid());
            Game game = room.getGame();
            Card card = game.pickCardFromDeck(CardTier.fromInt(dataDTO.tier));

            if(card==null){
                throw new DeckIsEmptyException("There are no cards available");
            }
            reservee.reserveCard(card);
            Log.DEBUG("User "+reservee.getName()+" reserved card from deck "+card.getCardTier());
            
            ResponseData responseData = new ResponseData(
                reservee.getUuid(), 
                new CardDataResponse(
                    card.getUuid(), 
                    card.getPoints(), 
                    card.getAdditionalToken(), 
                    new TokensDataResponse(
                        card.getCost(TokenType.RUBY), 
                        card.getCost(TokenType.EMERALD), 
                        card.getCost(TokenType.SAPPHIRE), 
                        card.getCost(TokenType.DIAMOND), 
                        card.getCost(TokenType.ONYX)
                    )
                )
            );

            ArrayList<User> players = room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                userMessage.getContextId(), 
                ServerMessageType.MAKE_RESERVATION_FROM_DECK_ANNOUNCEMENT, 
                Result.OK, 
                responseData
            );

            for(User player : players){
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);        
            }

        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.MAKE_RESERVATION_FROM_DECK_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }

    private void validateData(DataDTO dataDTO, Database database) throws Exception{
        User user = database.getUser(dataDTO.userUuid);

        //Check if user exists
        if(user==null){
            //TO DO
            throw new Exception("KATASTROFA");
        }

        Room room = database.getRoomWithUser(user.getUuid());

        //Check if user is in any room
        if(room==null){
            throw new RoomDoesntExistException("User does not belong to any room");
        }

        Game game = room.getGame();
        
        //check if the room is in the game
        if(game==null){
             //TO DO
             throw new Exception("KATASTROFA");
        }

        //Check if it is user's turn
        if(game.getCurrentPlayer()!=user){
             //TO DO
             throw new Exception("KATASTROFA");
        }
        
        //Check reservation count
        if(user.getReservationCount()>=3){
            throw new UserReservationException("User has to many reserved cards in hand");
        }

        //Check throughout game reservation count
        if(user.getThroughoutGameReservationCount()>=5){
            throw new UserReservationException("User reached the limit of reserved cards per game");
        }

        //Check if user has not maxed tokens
        if(user.getTokenCount()>=10){
            throw new TokenCountException("Reached max token count on hand");
        }

        //Check if if gold tokens are available
        if(game.getTokenCount(TokenType.GOLD_JOKER)<=0){
            throw new TokenCountException("There is not enough golden joker on table");
        }

    }
    
}

















