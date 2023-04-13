package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.Color;
import com.github.splendor_mobile_game.game.enums.TokenType;
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
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *              "name":"James"
 *          },
 *          "roomDTO":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *          }
 *      }
 * }
 *
 * Example of server announcement in successful case:
 * {
 *     "contextId":"02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *     "type":"START_GAME_RESPONSE",
 *     "result":"OK",
 *     "data":{
 *         "user":{
 *             "uuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *             "name":"James"
 *         },
 *         "room":{
 *             "uuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *         },
 *         "tokens":{
 *             "ruby":7,
 *             "emerald":7,
 *             "sapphire":7,
 *             "diamond":7,
 *             "onyx":7,
 *             "gold":5
 *         },
 *         "nobles":[
 *             {
 *                 "uuid":"030866e8-cef4-4fa2-aca7-7d95f631a7c9",
 *                 "prestige":3,
 *                 "redMinesRequired":0,
 *                 "greenMinesRequired":0,
 *                 "blueMinesRequired":0,
 *                 "whiteMinesRequired":4,
 *                 "blackMinesRequired":4
 *             },
 *             {
 *                 "uuid":"4e7fdd16-061d-4208-9202-6ada74b4e782",
 *                 "prestige":3,
 *                 "redMinesRequired":4,
 *                 "greenMinesRequired":0,
 *                 "blueMinesRequired":0,
 *                 "whiteMinesRequired":0,
 *                 "blackMinesRequired":4
 *             },
 *             {
 *                 "uuid":"03fbb775-1e17-4d6d-a658-66b18ef9e081",
 *                 "prestige":3,
 *                 "redMinesRequired":4,
 *                 "greenMinesRequired":4,
 *                 "blueMinesRequired":0,
 *                 "whiteMinesRequired":0,
 *                 "blackMinesRequired":0
 *             },
 *             {
 *                 "uuid":"caacfa2c-bbc3-4cb9-bab4-1d2cf6b7d55f",
 *                 "prestige":3,
 *                 "redMinesRequired":0,
 *                 "greenMinesRequired":3,
 *                 "blueMinesRequired":3,
 *                 "whiteMinesRequired":3,
 *                 "blackMinesRequired":0
 *             }
 *         ],
 *         "firstLevelMinesCards":[
 *             {
 *                 "uuid":"68d6a978-4715-4afe-85ab-a9a16c0a1c40",
 *                 "prestige":0,
 *                 "color":"BLUE",
 *                 "redTokensRequired":1,
 *                 "greenTokensRequired":1,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":1,
 *                 "blackTokensRequired":1
 *             },
 *             {
 *                 "uuid":"6dcfa310-93eb-402d-8098-0ea1650f6f70",
 *                 "prestige":0,
 *                 "color":"RED",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":1,
 *                 "blueTokensRequired":1,
 *                 "whiteTokensRequired":2,
 *                 "blackTokensRequired":1
 *             },
 *             {
 *                 "uuid":"baedee13-1057-4514-8d5a-ffdfe620ecc1",
 *                 "prestige":0,
 *                 "color":"GREEN",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":0,
 *                 "blueTokensRequired":1,
 *                 "whiteTokensRequired":2,
 *                 "blackTokensRequired":0
 *             },
 *             {
 *                 "uuid":"22bc7e1a-eb05-4f71-a368-2ba857060538",
 *                 "prestige":0,
 *                 "color":"GREEN",
 *                 "redTokensRequired":2,
 *                 "greenTokensRequired":0,
 *                 "blueTokensRequired":2,
 *                 "whiteTokensRequired":0,
 *                 "blackTokensRequired":0
 *             }
 *         ],
 *         "secondLevelMinesCards":[
 *             {
 *                 "uuid":"acdfd086-a9cf-499e-95ed-ce563fc40c76",
 *                 "prestige":1,
 *                 "color":"BLUE",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":3,
 *                 "blueTokensRequired":2,
 *                 "whiteTokensRequired":0,
 *                 "blackTokensRequired":3
 *             },
 *             {
 *                 "uuid":"154f2f30-64b6-483b-ac2f-68b323c08868",
 *                 "prestige":2,
 *                 "color":"GREEN",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":5,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":0,
 *                 "blackTokensRequired":0
 *             },
 *             {
 *                 "uuid":"96890791-2bdf-4194-9d60-35b1da949619",
 *                 "prestige":3,
 *                 "color":"BLACK",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":0,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":0,
 *                 "blackTokensRequired":6
 *             },
 *             {
 *                 "uuid":"7508853a-0d03-42b7-99d8-c574c2bcdadd",
 *                 "prestige":2,
 *                 "color":"WHITE",
 *                 "redTokensRequired":4,
 *                 "greenTokensRequired":1,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":0,
 *                 "blackTokensRequired":2
 *             }
 *         ],
 *         "thirdLevelMinesCards":[
 *             {
 *                 "uuid":"1c0772e5-3dc6-4da3-b83a-37d03f024c15",
 *                 "prestige":4,
 *                 "color":"BLUE",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":0,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":7,
 *                 "blackTokensRequired":0
 *             },
 *             {
 *                 "uuid":"c1fe9182-9e3d-45a3-bb40-1cd415075bba",
 *                 "prestige":4,
 *                 "color":"GREEN",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":3,
 *                 "blueTokensRequired":6,
 *                 "whiteTokensRequired":3,
 *                 "blackTokensRequired":0
 *             },
 *             {
 *                 "uuid":"6c392cc8-a581-4ac2-8e61-042d6c863a35",
 *                 "prestige":4,
 *                 "color":"BLUE",
 *                 "redTokensRequired":0,
 *                 "greenTokensRequired":0,
 *                 "blueTokensRequired":3,
 *                 "whiteTokensRequired":6,
 *                 "blackTokensRequired":3
 *             },
 *             {
 *                 "uuid":"671e4f8a-3ea4-450a-bdbf-c49bdf41f264",
 *                 "prestige":3,
 *                 "color":"BLUE",
 *                 "redTokensRequired":3,
 *                 "greenTokensRequired":3,
 *                 "blueTokensRequired":0,
 *                 "whiteTokensRequired":3,
 *                 "blackTokensRequired":5
 *             }
 *         ],
 *         "userToPlay":{
 *             "uuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7",
 *             "name":"James"
 *         }
 *     }
 * }
 *
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

    public class ResponseData{
        public UserDataResponse user;
        public RoomDataResponse room;
        public TokensDataResponse tokens;
        public ArrayList<NobleDataResponse> nobles;

        public ArrayList<MinesCardDataResponse> firstLevelMinesCards;
        public ArrayList<MinesCardDataResponse> secondLevelMinesCards;
        public ArrayList<MinesCardDataResponse> thirdLevelMinesCards;

        public UserDataResponse userToPlay;

        public ResponseData(UserDataResponse user, RoomDataResponse room, TokensDataResponse tokens,
                ArrayList<NobleDataResponse> nobles, ArrayList<MinesCardDataResponse> firstLevelMinesCards,
                ArrayList<MinesCardDataResponse> secondLevelMinesCards,
                ArrayList<MinesCardDataResponse> thirdLevelMinesCards,
                UserDataResponse userToPlay) {
            this.user = user;
            this.room = room;
            this.tokens = tokens;
            this.nobles = nobles;
            this.firstLevelMinesCards = firstLevelMinesCards;
            this.secondLevelMinesCards = secondLevelMinesCards;
            this.thirdLevelMinesCards = thirdLevelMinesCards;
            this.userToPlay=userToPlay;
        }
        
    }





    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO)userMessage.getData();

        try{
            validateData(dataDTO, database);

            User user = database.getUser(dataDTO.userDTO.uuid);
            Room room = database.getRoom(dataDTO.roomDTO.uuid);
            
            room.startGame();
            Game game = room.getGame();

            Log.DEBUG("Game started by "+user.getName()+". Room UUID: "+room.getUuid());         

            UserDataResponse userDataResponse = new UserDataResponse(user.getUuid(), user.getName());
            RoomDataResponse roomDataResponse=new RoomDataResponse(room.getUuid());

            TokensDataResponse tokensDataResponse=
                new TokensDataResponse(
                    game.getTokens(TokenType.RUBY),
                    game.getTokens(TokenType.EMERALD),
                    game.getTokens(TokenType.SAPPHIRE),
                    game.getTokens(TokenType.DIAMOND),
                    game.getTokens(TokenType.ONYX),
                    game.getTokens(TokenType.GOLD_JOKER)
                );
            
            ArrayList<Noble> nobles = game.getNobles();
            ArrayList<NobleDataResponse> nobleDataResponses = new ArrayList<>();
            for(Noble noble : nobles){
                nobleDataResponses.add(new NobleDataResponse(
                    noble.getUuid(), 
                    noble.getPoints(), 
                    noble.getCost(TokenType.RUBY),
                    noble.getCost(TokenType.EMERALD),
                    noble.getCost(TokenType.SAPPHIRE), 
                    noble.getCost(TokenType.DIAMOND), 
                    noble.getCost(TokenType.ONYX)
                ));
            }
    
            ArrayList<MinesCardDataResponse> firstLevelMinesCardsResponses=createMinesCardDataResponses(game.getRevealedCards(CardTier.LEVEL_1));
            ArrayList<MinesCardDataResponse> secondLevelMinesCardsResponses=createMinesCardDataResponses(game.getRevealedCards(CardTier.LEVEL_2));
            ArrayList<MinesCardDataResponse> thirdLevelMinesCardsResponses=createMinesCardDataResponses(game.getRevealedCards(CardTier.LEVEL_3));

            UserDataResponse userToPlayResponse=new UserDataResponse(game.getCurrentPlayer().getUuid(), game.getCurrentPlayer().getName());
                  
            ResponseData responseData = new ResponseData(
                userDataResponse, roomDataResponse, 
                tokensDataResponse, nobleDataResponses, 
                firstLevelMinesCardsResponses, secondLevelMinesCardsResponses, 
                thirdLevelMinesCardsResponses,userToPlayResponse);
            ServerMessage serverMessage = new ServerMessage(
                userMessage.getContextId(), ServerMessageType.START_GAME_RESPONSE,
                 Result.OK, responseData);
            messenger.addMessageToSend(this.connectionHashCode, serverMessage);


        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.START_GAME_RESPONSE, userMessage.getContextId().toString());
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

    private ArrayList<MinesCardDataResponse> createMinesCardDataResponses(Deck deck){
        ArrayList<MinesCardDataResponse> minesCardsResponses=new ArrayList<>();
        for(Card card : deck){
            minesCardsResponses.add(new MinesCardDataResponse(
                card.getUuid(), 
                card.getPoints(), 
                card.getAdditionalToken().color, 
                card.getCost(TokenType.RUBY),
                card.getCost(TokenType.EMERALD),
                card.getCost(TokenType.SAPPHIRE),
                card.getCost(TokenType.DIAMOND),
                card.getCost(TokenType.ONYX)       
            ));
        }

        return minesCardsResponses;
    }
    
}
