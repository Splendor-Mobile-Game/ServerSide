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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.CardDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserAlreadyInRoomException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotAMemberException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserTurnException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * Players send this request when it is their turn and they want to buy a mine card that is on the table.
 * The server responds with a message of type `BUY_REVEALED_MINE_ANNOUNCEMENT` to all players, announcing that the purchase has been made.
 * 
 * Example of user request:
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_REVEALED_MINE",
 *      "data": {
 *          "userDTO":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *          },
 *          "cardDTO":{
 *              "uuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *          }
 *      }
 * }
 * 
 * Example of server announcement:
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_REVEALED_MINE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "buyer":{
 *              "userUuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *              "tokens":{
 *                  "ruby": 2,
 *                  "emerald": 0,
 *                  "sapphire": 1,
 *                  "diamond": 3,
 *                  "onyx": 0,
 *                  "gold": 1
 *              },
 *              "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 *          },                     
 *          "newCardRevealed":{
 *              "uuid": "501ba578-f919-4488-b3ee-91b043abbc83",
 *              "cardTier": 1,
 *              "additionalToken": "RUBY",
 *              "points": 2,
 *              "rubyCost": 0,
 *              "emeraldCost": 0,
 *              "sapphireCost": 1,
 *              "diamondCost": 2,
 *              "onyxCost": 0
 *          }        
 *      }
 * }
 *
 * Implementation details:
 * - The player who sent the message is identified by their WebSocket's connectionHashCode.
 * - The UUID of the card to be purchased is the same as the one sent at the start of the game.
 * - The state of the game on the server should be updated to reflect the purchase (subtract the appropriate amount of tokens, add prestige points, and add the bonus point).
 * 
 * Invalid requests:
 * - If the player sends a message to buy a mine when it is not their turn, the server should respond with a message of type `BUY_REVEALED_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that they cannot buy a mine when it is not their turn.
 * - If the player sends a message to buy a mine but does not have enough tokens, the server should respond with a message of type `BUY_REVEALED_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that they do not have enough tokens to make the purchase.
 * - If the player sends a message to buy a mine that is not available on the table, the server should respond with a message of type `BUY_REVEALED_MINE_RESPONSE` with a result of "FAILURE" and an error message indicating that the requested mine is not available on the table.
 * 
 * Example of invalid request response (it should be sent only to the requester):
 * {
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "BUY_REVEALED_MINE_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot buy a mine when it is not your turn!"
 *      }
 * }
 */
@ReactionName("BUY_REVEALED_MINE")
public class BuyRevealedMine extends Reaction {

    public BuyRevealedMine(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class UserDTO{
        public UUID uuid;

        public UserDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static class CardDTO{
        public UUID uuid;

        public CardDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }

    @DataClass
    public static class DataDTO{
        public UserDTO userDTO;
        public CardDTO cardDTO;

        public DataDTO(UserDTO userDTO, CardDTO cardDTO) {
            this.userDTO = userDTO;
            this.cardDTO = cardDTO;
        }    
    }

    public class TokensDataResponse{
        public int ruby;
        public int emerald;
        public int sapphire;
        public int diamond;
        public int onyx;
        public int gold;


        public TokensDataResponse(int ruby, int emerald, int sapphire, int diamond, int onyx, int gold) {
            this.ruby = ruby;
            this.emerald = emerald;
            this.sapphire = sapphire;
            this.diamond = diamond;
            this.onyx = onyx;
            this.gold = gold;
        }      
    }

    public class BuyerDataResponse{
        public UUID userUuid;
        public TokensDataResponse tokens;
        public UUID cardUuid;

        public BuyerDataResponse(UUID userUuid, TokensDataResponse tokens, UUID cardUuid) {
            this.userUuid = userUuid;
            this.tokens = tokens;
            this.cardUuid = cardUuid;
        }
    }

    public class CardDataResponse{
        public UUID uuid;
        public CardTier cardTier;
        public TokenType additionalToken;
        public int points;
        
        public int rubyCost;
        public int emeraldCost;
        public int sapphireCost;
        public int diamondCost;
        public int onyxCost;


        public CardDataResponse(UUID uuid, CardTier cardTier, TokenType additionalToken, int points, int rubyCost, int emeraldCost,
                int sapphireCost, int diamondCost, int onyxCost) {
            this.uuid = uuid;
            this.cardTier = cardTier;
            this.additionalToken = additionalToken;
            this.points = points;
            this.rubyCost = rubyCost;
            this.emeraldCost = emeraldCost;
            this.sapphireCost = sapphireCost;
            this.diamondCost = diamondCost;
            this.onyxCost = onyxCost;
        }              
    }


    public class ResponseData{
        public BuyerDataResponse buyer;
        public CardDataResponse newCardRevealed;

        public ResponseData(BuyerDataResponse buyer,CardDataResponse newCardRevealed) {
            this.buyer = buyer;
            this.newCardRevealed = newCardRevealed;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try{
            validateData(dataDTO, database);

            User buyer = database.getUser(dataDTO.userDTO.uuid);
            Card boughtCard = database.getCard(dataDTO.cardDTO.uuid);
            Room room = database.getRoomWithUser(buyer.getUuid());
            Game game = room.getGame();

            buyer.buyCard(boughtCard);
            Card cardDrawn = game.takeCardFromRevealed(boughtCard);
            
            Log.DEBUG("User "+buyer.getName()+" has bought card ("+boughtCard.getUuid()+")");
            Log.DEBUG("New card drawn "+cardDrawn.getUuid());

            ResponseData responseData = new ResponseData(
                new BuyerDataResponse(
                    buyer.getUuid(), 
                    new TokensDataResponse(
                        buyer.getTokenCount(TokenType.RUBY), 
                        buyer.getTokenCount(TokenType.EMERALD), 
                        buyer.getTokenCount(TokenType.SAPPHIRE), 
                        buyer.getTokenCount(TokenType.DIAMOND), 
                        buyer.getTokenCount(TokenType.ONYX), 
                        buyer.getTokenCount(TokenType.GOLD_JOKER)
                    ), 
                    boughtCard.getUuid()
                ), 
                new CardDataResponse(
                    cardDrawn.getUuid(),
                    cardDrawn.getCardTier(),
                    cardDrawn.getAdditionalToken(),
                    cardDrawn.getPoints(),
                    cardDrawn.getCost(TokenType.RUBY),
                    cardDrawn.getCost(TokenType.EMERALD),
                    cardDrawn.getCost(TokenType.SAPPHIRE),
                    cardDrawn.getCost(TokenType.DIAMOND),
                    cardDrawn.getCost(TokenType.ONYX))
            );

            ArrayList<User> players = room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                userMessage.getContextId(), 
                ServerMessageType.BUY_REVEALED_MINE_ANNOUNCEMENT, 
                Result.OK, 
                responseData
            );

            for(User player : players){
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);        
            }


        }catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(
                Result.FAILURE, 
                e.getMessage(), 
                ServerMessageType.BUY_REVEALED_MINE_RESPONSE, 
                userMessage.getContextId().toString()
            );
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }

    private void validateData(DataDTO dataDTO,Database database) throws 
    UserNotAMemberException, UserAlreadyInRoomException, RoomDoesntExistException, UserTurnException, CardDoesntExistException{
        
        User player = database.getUser(dataDTO.userDTO.uuid);
        
        //Check if user exits    
        if(player == null){
            //TO DO 
        }

        Room room = database.getRoomWithUser(player.getUuid());

        // Check if room exists whose user is a member of
        if(room == null){
            throw new RoomDoesntExistException("Room does not exist whose user is a member of");
        }

        Game game = room.getGame();

        //Check if user is in game
        if(game==null){
            //TO DO
        }

        //Check if it is user's turn
        if(game.getCurrentPlayer()!=player){
            throw new UserTurnException("It is not a user's turn");
        }

        Card card = database.getCard(dataDTO.cardDTO.uuid);

        //Check if card exists
        if(card==null){
            throw new CardDoesntExistException("There is no such card in the database");
        }

        //Check if the card is avaiable
        if(!game.revealedCardExists(card.getUuid())){
            throw new CardDoesntExistException("The card is not in the revealed deck");
        }
    }
}