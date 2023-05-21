package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.ReservationResult;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.Regex;
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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.*;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

import java.util.ArrayList;
import java.util.UUID;

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
 *          "reserveData":{
    *          "userUuid": "9978b2ba-f3e5-4e23-818a-879b0adcfe9a",
    *          "cardUuid": "a59cabab-6dac-44c7-ae53-ad8e22936f2c"
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
 *              "onyxCost": 0,
 *              "graphicsID": 1
 *          },
 *          "gotGoldenToken":"true"        
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

    public static class ReserveeDataResponse {
        public UUID userUuid;
        public UUID reservedCardUuid;

        public ReserveeDataResponse(UUID userUuid, UUID reservedCardUuid) {
            this.userUuid = userUuid;
            this.reservedCardUuid = reservedCardUuid;
        }
    }

    //data about card for response
    public static class CardDataResponse{
        public UUID uuid;
        public CardTier cardTier;
        public TokenType additionalToken;
        public int points;
        
        public int rubyCost;
        public int emeraldCost;
        public int sapphireCost;
        public int diamondCost;
        public int onyxCost;
        public int graphicsID;

        public CardDataResponse(UUID uuid, CardTier cardTier, TokenType additionalToken, int points, int rubyCost, int emeraldCost, int sapphireCost, int diamondCost, int onyxCost, int graphicsID) {
            this.uuid = uuid;
            this.cardTier = cardTier;
            this.additionalToken = additionalToken;
            this.points = points;
            this.rubyCost = rubyCost;
            this.emeraldCost = emeraldCost;
            this.sapphireCost = sapphireCost;
            this.diamondCost = diamondCost;
            this.onyxCost = onyxCost;
            this.graphicsID = graphicsID;
        }              
    }

    //data how much cost card for response
    public static class TokensDataResponse{
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
    public static class ResponseData{
        public ReserveeDataResponse reservee;
        public CardDataResponse cardDataResponse;
        public boolean gotGoldenToken;


        public ResponseData(ReserveeDataResponse reservee, CardDataResponse cardDataResponse, boolean gotGoldenToken) {
            this.reservee = reservee;
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

            reservee.setPerformedAction(true);
            ReservationResult reservationResult = game.reserveCardFromTable(database.getCard(dataDTO.cardDTO.uuid),reservee);

            // newCard
            Card cardDrawn = reservationResult.getCard();
            boolean gotGoldenToken = reservationResult.getGoldenToken();

           // card.getCardTier() returns a card tier from new card, which is also card tier of old one
            Log.DEBUG("User " + reservee.getName() + " reserved card from table " + cardDrawn.getCardTier() + " and golden token: " + gotGoldenToken);


            CardDataResponse cardDataResponse = null;
            if (cardDrawn != null) {
                cardDataResponse = new CardDataResponse(
                    cardDrawn.getUuid(),
                    cardDrawn.getCardTier(),
                    cardDrawn.getAdditionalToken(),
                    cardDrawn.getPoints(),
                    cardDrawn.getCost(TokenType.RUBY),
                    cardDrawn.getCost(TokenType.EMERALD),
                    cardDrawn.getCost(TokenType.SAPPHIRE),
                    cardDrawn.getCost(TokenType.DIAMOND),
                    cardDrawn.getCost(TokenType.ONYX),
                    cardDrawn.getGraphicsID()
                );

                Log.DEBUG("New card drawn "+cardDrawn.getUuid());
            }else{
                cardDataResponse=null;
                Log.DEBUG("New card was not drawn ");
            }


            // creating a responseData which contains user uuid and all informations about new card which will replace the reserved one
            ResponseData responseData = new ResponseData(
                    new ReserveeDataResponse(
                        reservee.getUuid(),
                        dataDTO.cardDTO.uuid //reserved card
                        ),
                    cardDataResponse,
                    gotGoldenToken
            );
            ArrayList<User> players = room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                    userMessage.getContextId(),
                    ServerMessageType.MAKE_RESERVATION_FROM_TABLE_ANNOUNCEMENT,
                    Result.OK,
                    responseData
            );

            for (User player : players) {
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);
            }




        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.MAKE_RESERVATION_FROM_TABLE_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }

    }
    private void validateData(DataDTO dataDTO,Database database) throws InvalidUUIDException, UserDoesntExistException, UserNotAMemberException, RoomInGameException, UserTurnException, UserReservationException, CardNotRevealedException, TokenCountException {
        // Check if user's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.userDTO.uuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");

        // Check if card's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.cardDTO.uuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");


        // Check if user exists
        User user = database.getUser(dataDTO.userDTO.uuid);
        if (user == null)
            throw new UserDoesntExistException("Couldn't find a user with given UUID.");

        //Check if user is in any room
        Room room = database.getRoomWithUser(user.getUuid());
        if (room == null)
            throw new UserNotAMemberException("You are not a member of any room!");


        // Check if the game is started
        Game game = room.getGame();
        if (game == null)
            throw new RoomInGameException("The game hasn't started yet!");


        // Check if it is user's turn
        if (room.getCurrentPlayer() != user)
            throw new UserTurnException("It is not your turn!");

        // Check reservation count
        if (user.getReservationCount() >= 3)
            throw new UserReservationException("You have reached the current reserved cards limit.");

        // Check game reservation count
        if (game.getGameReservationCount() >= 5)
            throw new UserReservationException("You have reached the limit of reserved cards per game.");

        // Check if card is on table
        if(!game.isCardRevealed(dataDTO.cardDTO.uuid))
            throw new CardNotRevealedException("There is no this card on table");

        // Check if user has not maxed tokens
        if (user.getTokenCount() >= 10)
            throw new TokenCountException("You have reached the maximum token count on hand.");

    }

}

