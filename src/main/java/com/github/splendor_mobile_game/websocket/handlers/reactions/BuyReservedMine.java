package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Regex;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.*;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * Players send this request when it is their turn and they want to buy a mine
 * card that they previously reserved.
 * The server responds with a message of type `BUY_RESERVED_MINE_ANNOUNCEMENT`
 * to all players, announcing which player has but what card.
 * 
 * 
 * Example of user request:
 * {
 * "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 * "type": "BUY_RESERVED_MINE",
 * "data": {
 * "userDTO":{
 * "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 * },
 * "cardDTO":{
 * "uuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 * }
 * }
 * }
 * 
 * In server announcement "tokens" means the new set of tokens of that player
 * after they bought a mine.
 * Example of server announcement:
 * {
 * "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 * "type": "BUY_RESERVED_MINE_ANNOUNCEMENT",
 * "result": "OK",
 * "data": {
 * "buyer":{
 * "userUuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7",
 * "tokens":{
 * "ruby": 2,
 * "emerald": 0,
 * "sapphire": 1,
 * "diamond": 3,
 * "onyx": 0,
 * "gold": 1
 * },
 * "cardUuid": "521ba578-f989-4488-b3ee-91b043abbc83"
 * },
 * }
 *
 * Example of invalid request response (it should be sent only to the
 * requester):
 * {
 * "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 * "type": "BUY_RESERVED_MINE_RESPONSE",
 * "result": "FAILURE",
 * "data": {
 * "error": "You cannot buy a mine when it is not your turn!"
 * }
 * }
 */
@ReactionName("BUY_RESERVED_MINE")
public class BuyReservedMine extends Reaction {

    public BuyReservedMine(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class UserDTO {
        public UUID uuid;

        public UserDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static class CardDTO {
        public UUID uuid;

        public CardDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }

    @DataClass
    public static class DataDTO {
        public UserDTO userDTO;
        public CardDTO cardDTO;

        public DataDTO(UserDTO userDTO, CardDTO cardDTO) {
            this.userDTO = userDTO;
            this.cardDTO = cardDTO;
        }
    }

    public class TokensDataResponse {
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

    public class BuyerDataResponse {
        public UUID userUuid;
        public TokensDataResponse tokens; // The new set of tokens of that player after they bought a mine
        public UUID cardUuid;

        public BuyerDataResponse(UUID userUuid, TokensDataResponse tokens, UUID cardUuid) {
            this.userUuid = userUuid;
            this.tokens = tokens;
            this.cardUuid = cardUuid;
        }
    }

    public class ResponseData {
        public BuyerDataResponse buyer;

        public ResponseData(BuyerDataResponse buyer) {
            this.buyer = buyer;
        }
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {
            validateData(dataDTO, database);

            User buyer = database.getUser(dataDTO.userDTO.uuid);
            Card boughtCard = database.getCard(dataDTO.cardDTO.uuid);
            Room room = database.getRoomWithUser(buyer.getUuid());

            buyer.setPerformedAction(true);
            buyer.buyCard(boughtCard);
            buyer.removeCardFromReserved(boughtCard);
            room.getGame().decreaseGameReservationCount();

            Log.DEBUG("User " + buyer.getName() + " has bought card (" + boughtCard.getUuid() + ")");

            ResponseData responseData = new ResponseData(
                    new BuyerDataResponse(
                            buyer.getUuid(),
                            new TokensDataResponse(
                                    buyer.getTokenCount(TokenType.RUBY),
                                    buyer.getTokenCount(TokenType.EMERALD),
                                    buyer.getTokenCount(TokenType.SAPPHIRE),
                                    buyer.getTokenCount(TokenType.DIAMOND),
                                    buyer.getTokenCount(TokenType.ONYX),
                                    buyer.getTokenCount(TokenType.GOLD_JOKER)),
                            boughtCard.getUuid()));

            ArrayList<User> players = room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                    userMessage.getContextId(),
                    ServerMessageType.BUY_RESERVED_MINE_ANNOUNCEMENT,
                    Result.OK,
                    responseData);

            for (User player : players) {
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);
            }

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    Result.FAILURE,
                    e.getMessage(),
                    ServerMessageType.BUY_RESERVED_MINE_RESPONSE,
                    userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }

    private void validateData(DataDTO dataDTO, Database database) throws UserTurnException, CardDoesntExistException, UserDoesntExistException, UserNotAMemberException, GameNotStartedException, InvalidUUIDException {
        // Check if user's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.userDTO.uuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");

        // Check if card's UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(dataDTO.cardDTO.uuid.toString()))
            throw new InvalidUUIDException("Invalid UUID format.");


        User user = database.getUser(dataDTO.userDTO.uuid);

        // Check if user exists
        if (user == null)
            throw new UserDoesntExistException("Couldn't find a user with given UUID.");

        Room room = database.getRoomWithUser(user.getUuid());
        // Check if room exists
        if (room == null)
            throw new UserNotAMemberException("You are not a member of any room!");

        // Check if game is running
        if (room.getGame() == null)
            throw new GameNotStartedException("Game hasn't started yet.");

        // Check if it is user's turn
        if (room.getCurrentPlayer() != user)
            throw new UserTurnException("It's not your turn.");

        if (user.hasPerformedAction())
            throw new UserTurnException("You have already performed an action.");

        Card card = database.getCard(dataDTO.cardDTO.uuid);
        // Check if card exists
        if (card == null)
            throw new CardDoesntExistException("Couldn't find a card with given UUID.");

        // Check if the card is available
        if (!user.isCardReserved(card))
            throw new CardDoesntExistException("The card is not in the reserved deck.");
    }
}
