package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.Exceptions.NotEnoughTokensException;
import com.github.splendor_mobile_game.game.ReservationResult;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUsernameException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserReservationException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reaction for handling player's request to make a reservation from the table. The player can only make a reservation if it is their turn.
 * Upon receiving a valid request, the server sends an announcement to all players in the game.
 *
 * Example of a valid request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE",
 *      "data": {
 *          "userUuid": "01901b0e-a78b-4a65-bbd3-0065948dc127",
 *          "cardUuid": "b38df21a-6e7b-4537-a20b-ad797a394350"
 *      }
 * }
 *
 * Example of a successful server announcement:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "RESERVATION_FROM_TABLE_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "userUuid": "9978b2ba-f3e5-4e23-818a-879b0adcfe9a",
 *          "cardUuid": "a59cabab-6dac-44c7-ae53-ad8e22936f2c"
 *      }
 * }
 *
 * Implementation details:
 * - The player's WebSocket connectionHashCode is used to identify the player.
 * - The game state in the database needs to be updated.
 *
 * If the request is invalid (e.g. player is not in a game, it is not their turn, etc.), the server should only send a response to the requester.
 *
 * Example of an invalid request response:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "MAKE_RESERVATION_FROM_TABLE_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot make a reservation if it is not your turn!"
 *      }
 * }
 */
@ReactionName("MAKE_RESERVATION_FROM_TABLE")
public class MakeReservationFromTable extends Reaction {

    public MakeReservationFromTable(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class CardDTO {
        public UUID uuid;

        public CardDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }
    public static class UserDTO {
        public UUID uuid;
        public UserDTO(UUID uuid)
        {
            this.uuid = uuid;
        }
    }
    public static class DataDTO{
        private UserDTO userDTO;
        private CardDTO cardDTO;
        public DataDTO(CardDTO cardDTO,UserDTO userDTO) {
            this.cardDTO = cardDTO;
            this.userDTO = userDTO;
        }
    }

    //data about card for response
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

    //data how much cost card for response
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

    //class that stores data to make a response
    public class ResponseData{
        public UUID userUuid;
        public CardDataResponse cardDataResponse;
        public boolean gotGoldenToken;


        public ResponseData(UUID userUuid, CardDataResponse cardDataResponse, boolean gotGoldenToken) {
            this.userUuid = userUuid;
            this.cardDataResponse = cardDataResponse;
            this.gotGoldenToken = gotGoldenToken;
        }
    }



    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) userMessage.getData();


        try{
            validateData(dataDTO,database);
            User reservee = database.getUser(dataDTO.userDTO.uuid);
            Room room = database.getRoomWithUser(dataDTO.userDTO.uuid);
            Game game = room.getGame();

            ReservationResult reservationResult = game.reserveCardFromTable(database.getCard(dataDTO.cardDTO.uuid),reservee);

            //newcard
            Card card = reservationResult.getCard();
            boolean gotGoldenToken = reservationResult.getGoldenToken();

           //card.getCardTier() returns a card tier from new card, which is also card tier of old one
            Log.DEBUG("User "+reservee.getName()+" reserved card from table "+card.getCardTier()+" and golden token: "+gotGoldenToken);

            //creating a responseData which contains user uuid and all informations about new card which will replace the reserved one
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
                    ),
                    gotGoldenToken
            );
            ArrayList<User> players = room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                    userMessage.getContextId(),
                    ServerMessageType.MAKE_RESERVATION_FROM_TABLE_ANNOUNCEMENT,
                    Result.OK,
                    responseData
            );

            for(User player : players){
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);
            }




        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.MAKE_RESERVATION_FROM_TABLE_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }

    }
    private void validateData(DataDTO dataDTO,Database database) throws Exception {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");


        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find()) throw new InvalidUUIDException("Invalid UUID format.");

        // Check if card UUID matchers the pattern
        Matcher uuidMatcherCard = uuidPattern.matcher(dataDTO.cardDTO.uuid.toString());
        if (!uuidMatcherCard.find()) throw new InvalidUUIDException("Invalid UUID format.");

        //Check if it's this user turn
        if(database.getRoomWithUser(dataDTO.userDTO.uuid).getGame().getCurrentPlayer().getUuid() != dataDTO.userDTO.uuid)
            throw new InvalidUsernameException("It's not this user turn");
        //TODO make exception

        //Check if limit of reserved card is reached for player
        if(database.getUser(dataDTO.userDTO.uuid).getReservationCount()>=3)
            throw new UserReservationException("User has to many reserved cards in hand");

        //Check if limit of reserved card is reached for game
        if(database.getUser(dataDTO.userDTO.uuid).getThroughoutGameReservationCount()>=5)
            throw new UserReservationException("User reached the limit of reserved cards per game");

        //Check if card is on table
        if(database.getRoom(dataDTO.userDTO.uuid).getGame().isCardRevealed(dataDTO.cardDTO.uuid) == false)
            throw new Exception("There is no this card on table");
        //TODO make exception

    }

}

