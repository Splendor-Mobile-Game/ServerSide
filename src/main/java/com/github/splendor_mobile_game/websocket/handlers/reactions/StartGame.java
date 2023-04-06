package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.slf4j.helpers.NOPLoggerFactory;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.Color;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Deck;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Noble;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUsernameException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomInGameException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomOwnershipException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserDoesntExistException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

/**
 * Reaction class for handling the "START_GAME" message sent by the host player to start the game.
 * Sends a "GAME_STARTED_ANNOUNCEMENT" message to all players with the initial state of the game.
 * Also sends a "NEW_TURN_ANNOUNCEMENT" message with the player whose turn is first.
 *
 * Example of user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME",
 *      "data": {
 *          "userDTO":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *              "name":"James"
 *          }
 *          "roomDTO":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *          }
 *      }
 * }
 *
 * Example of server announcement in successful case:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GAME_STARTED_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "user":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *              "name":"James"
 *          }
 *          "room":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *          }
 *          "tokens": {
 *              "red": 7,
 *              "green": 7,
 *              "blue": 7,
 *              "white": 7,
 *              "black": 7,
 *              "gold": 5,
 *          },
 *          "nobles": [
 *              {
 *                   "uuid": "81b7249e-d1f0-4030-a59d-0217ee3ac161",
 *                   "prestige": 3,
 *                   "redMinesRequired": 4,
 *                   "blueMinesRequired": 4
 *               },
 *               {
 *                   "uuid": "8bceab0a-d67f-44b2-ad4f-cda592cb4b13",
 *                   "prestige": 3,
 *                   "greenMinesRequired": 2,
 *                   "whiteMinesRequired": 3,
 *                   "blackMinesRequired": 3
 *               },
 *               ...
 *               ...
 *               ...
 *          ],
 *          "firstLevelMinesCards": [
 *              {
 *                  "prestige": 2,
 *                  "bonusColor": "green",
 *                  "greenTokensRequired": 2,
 *                  "whiteTokensRequired": 3
 *              },
 *              {
 *                  "prestige": 0,
 *                  "bonusColor": "white",
 *                  "blackTokensRequired": 3
 *              }
 *              ...
 *              ...
 *              ...
 *          ],
 *          "secondLevelMinesCards": [], // The same as above
 *          "thirdLevelMinesCards": [], // The same as above
 *          "playersOrder": [
 *              {
 *                  "uuid": 81b7249e-d1f0-4030-a59d-0217ee3ac161
 *              }
 *              {
 *                  ...
 *              }
 *              ...
 *          ]
 *      }
 * }
 *
 * Example of implementation details:
 * - Check if the user is the host and is in a game.
 * - If yes, get the initial state of the game from the database.
 * - Send a "GAME_STARTED_ANNOUNCEMENT" message to all players with the initial state of the game.
 * - Send a "NEW_TURN_ANNOUNCEMENT" message with the player whose turn is first.
 *
 * Description of bad requests:
 * - If the user is not the host or is not in any game, send a "START_GAME_RESPONSE" message with a failure result and an error message.
 *
 * Example of response to the bad request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot start the game if you are not the host or if you are not in any game!"
 *      }
 * }
 */
@ReactionName("START_GAME")
public class StartGame extends Reaction {

    public StartGame(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class UserDTO {
        public UUID uuid;
        public String name;

        public UserDTO(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

    }

    public static class RoomDTO{
        public UUID uuid;

        public RoomDTO(UUID uuid) {
            this.uuid = uuid;
        }
    }


    @DataClass
    public static class DataDTO{
        public UserDTO userDTO;
        public RoomDTO roomDTO;

        public DataDTO(UserDTO userDTO,RoomDTO roomDTO) {
            this.userDTO = userDTO;
            this.roomDTO=roomDTO;
        }   
    }
    public class UserDataResponse {
        public UUID uuid;
        public String name;

        public UserDataResponse(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }      
    }

    public class RoomDataResponse{
        public UUID uuid;

        public RoomDataResponse(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public class TokensDataResponse{
        public int red;
        public int green;
        public int blue;
        public int white;
        public int black;
        public int gold;

        public TokensDataResponse(int red, int green, int blue, int white, int black, int gold) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.white = white;
            this.black = black;
            this.gold = gold;
        }
    }

    public class NobleDataResponse{
        public UUID uuid;
        public int prestige;

        public int redMinesRequired;
        public int greenMinesRequired;
        public int blueMinesRequired;
        public int whiteMinesRequired;
        public int blackMinesRequired;

        public NobleDataResponse(UUID uuid, int prestige, int redMinesRequired, int greenMinesRequired,
                int blueMinesRequired, int whiteMinesRequired, int blackMinesRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.redMinesRequired = redMinesRequired;
            this.greenMinesRequired = greenMinesRequired;
            this.blueMinesRequired = blueMinesRequired;
            this.whiteMinesRequired = whiteMinesRequired;
            this.blackMinesRequired = blackMinesRequired;
        }
    }

    public class MinesCardDataResponse{
        public UUID uuid;
        public int prestige;
        public Color color;

        public int redTokensRequired;
        public int greenTokensRequired;
        public int blueTokensRequired;
        public int whiteTokensRequired;
        public int blackTokensRequired;

        public MinesCardDataResponse(UUID uuid, int prestige, Color color, int redTokensRequired,
                int greenTokensRequired, int blueTokensRequired, int whiteTokensRequired, int blackTokensRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.color = color;
            this.redTokensRequired = redTokensRequired;
            this.greenTokensRequired = greenTokensRequired;
            this.blueTokensRequired = blueTokensRequired;
            this.whiteTokensRequired = whiteTokensRequired;
            this.blackTokensRequired = blackTokensRequired;
        }
        
    }

    public class PlayerOrderDataResponse{
        UUID uuid;

        public PlayerOrderDataResponse(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public class ResponseData{
        public UserDataResponse user;
        public RoomDataResponse room;
        public TokensDataResponse tokens;
        public ArrayList<NobleDataResponse> nobles;

        public ArrayList<MinesCardDataResponse> firstLevelMinesCards;
        public ArrayList<MinesCardDataResponse> secondLevelMinesCards;
        public ArrayList<MinesCardDataResponse> thirdLevelMinesCards;

        public ArrayList<PlayerOrderDataResponse> playerOrder;

        public ResponseData(UserDataResponse user, RoomDataResponse room, TokensDataResponse tokens,
                ArrayList<NobleDataResponse> nobles, ArrayList<MinesCardDataResponse> firstLevelMinesCards,
                ArrayList<MinesCardDataResponse> secondLevelMinesCards,
                ArrayList<MinesCardDataResponse> thirdLevelMinesCards,
                ArrayList<PlayerOrderDataResponse> playerOrder) {
            this.user = user;
            this.room = room;
            this.tokens = tokens;
            this.nobles = nobles;
            this.firstLevelMinesCards = firstLevelMinesCards;
            this.secondLevelMinesCards = secondLevelMinesCards;
            this.thirdLevelMinesCards = thirdLevelMinesCards;
            this.playerOrder=playerOrder;
        }
        
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO)userMessage.getData();

        try{
            validateData(dataDTO, database);

            User user = database.getUser(dataDTO.userDTO.uuid);
            Room room = database.getRoom(dataDTO.roomDTO.uuid);
            Game game = room.getGame();

            Log.DEBUG("Game started by "+user.getName()+". Room UUID: "+room.getUuid());
            game.startGame(room);

            

            UserDataResponse userDataResponse = new UserDataResponse(user.getUuid(), user.getName());
            RoomDataResponse roomDataResponse=new RoomDataResponse(room.getUuid());

            TokensDataResponse tokensDataResponse=
                new TokensDataResponse(
                    game.getRubyTokens().getAvailableTokensCount(),
                    game.getEmeraldTokens().getAvailableTokensCount(),
                    game.getSapphireTokens().getAvailableTokensCount(), 
                    game.getDiamondTokens().getAvailableTokensCount(), 
                    game.getOnyxTokens().getAvailableTokensCount(), 
                    game.getGoldTokens().getAvailableTokensCount()
                );
            
            ArrayList<Noble> nobles = game.getNobles();
            ArrayList<NobleDataResponse> nobleDataResponses = new ArrayList<>();
            for(Noble noble : nobles){
                nobleDataResponses.add(new NobleDataResponse(
                    noble.getUuid(), noble.getPoints(), 
                    noble.getRubyCost(), noble.getEmeraldCost(), 
                    noble.getSapphireCost(), noble.getDiamondCost(), 
                    noble.getOnyxCost())
                );
            }


            
            Deck deck = game.getRevealedCards(CardTier.LEVEL_1);
            ArrayList<MinesCardDataResponse> firstLevelMinesCardsResponses=new ArrayList<>();
            for(Card card : deck){
                firstLevelMinesCardsResponses.add(new MinesCardDataResponse(
                    card.getUuid(), card.getPoints(), 
                    card.getAdditionalToken().color, card.getRubyCost(), 
                    card.getEmeraldCost(), card.getSapphireCost(), 
                    card.getDiamondCost(), card.getOnyxCost())
                );
            }

            deck = game.getRevealedCards(CardTier.LEVEL_2);
            ArrayList<MinesCardDataResponse> secondLevelMinesCardsResponses=new ArrayList<>();
            for(Card card : deck){
                secondLevelMinesCardsResponses.add(new MinesCardDataResponse(
                    card.getUuid(), card.getPoints(), 
                    card.getAdditionalToken().color, card.getRubyCost(), 
                    card.getEmeraldCost(), card.getSapphireCost(), 
                    card.getDiamondCost(), card.getOnyxCost())
                );
            }

            deck = game.getRevealedCards(CardTier.LEVEL_3);
            ArrayList<MinesCardDataResponse> thirdLevelMinesCardsResponses=new ArrayList<>();
            for(Card card : deck){
                thirdLevelMinesCardsResponses.add(new MinesCardDataResponse(
                    card.getUuid(), card.getPoints(), 
                    card.getAdditionalToken().color, card.getRubyCost(), 
                    card.getEmeraldCost(), card.getSapphireCost(), 
                    card.getDiamondCost(), card.getOnyxCost())
                );
            }

            ArrayList<User> users= room.getAllUsers();
            ArrayList<PlayerOrderDataResponse> playerOrderDataResponses=new ArrayList<>();
            for(User _user : users){
                playerOrderDataResponses.add(new PlayerOrderDataResponse(_user.getUuid()));
            }

            ResponseData responseData = new ResponseData(
                userDataResponse, roomDataResponse, 
                tokensDataResponse, nobleDataResponses, 
                firstLevelMinesCardsResponses, secondLevelMinesCardsResponses, 
                thirdLevelMinesCardsResponses,playerOrderDataResponses);
            ServerMessage serverMessage = new ServerMessage(
                userMessage.getMessageContextId(), ServerMessageType.START_GAME_RESPONSE,
                 Result.OK, responseData);
            messenger.addMessageToSend(this.connectionHashCode, serverMessage);


        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.START_GAME_RESPONSE, userMessage.getMessageContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }

   
    }

    private void validateData(DataDTO dataDTO, Database database) throws  RoomDoesntExistException, UserDoesntExistException, RoomOwnershipException, InvalidUUIDException, RoomInGameException{
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"); 

         // Check if user UUID matches the pattern
         Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
         if (!uuidMatcher.find())
             throw new InvalidUUIDException("Invalid UUID format."); 
 
         // Check if room UUID matches the pattern
         uuidMatcher = uuidPattern.matcher(dataDTO.roomDTO.uuid.toString());
         if (!uuidMatcher.find())
             throw new InvalidUUIDException("Invalid UUID format."); 

        User user = database.getUser(dataDTO.userDTO.uuid);
        Room room =database.getRoom(dataDTO.roomDTO.uuid);

        //Check if user exits
        if (user==null){
            throw new UserDoesntExistException("No such user in the database");
        }

        //Check if room exits
        if(room==null){
            throw new RoomDoesntExistException("No such room in the database");
        }      

        //Check if he owns only one room
        int ownerships =0;
        for(Room _room : database.getAllRooms()){
            if(_room.getOwner()==user){
                ownerships++;
            }
        }
        if(ownerships!=1){
            throw new RoomOwnershipException("User owns more than one or zero rooms");
        }

        //Check if user is the owner of the room
        if(!room.getOwner().equals(user)){
            throw new RoomOwnershipException("User is not an owner of a given room");
        }

        //Check if room is not in game
        if(room.getGame()!=null){
            throw new RoomInGameException("Room is during the match");
        }

    }
    
}
