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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomPlayerCountException;
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
 *      "contextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME",
 *      "data": {
 *          "userDTO":{
 *              "uuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
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
 *                 "uuid":"5190c790-c9b9-4a72-85ed-8803eea15d1c",
 *                 "prestige":3,
 *                 "rubyMinesRequired":0,
 *                 "emeraldMinesRequired":0,
 *                 "sapphireMinesRequired":3,
 *                 "diamondMinesRequired":3,
 *                 "onyxMinesRequired":3
 *             },
 *             {
 *                 "uuid":"5fd47d77-3e99-4eb3-8436-5e818a17041d",
 *                 "prestige":3,
 *                 "rubyMinesRequired":3,
 *                 "emeraldMinesRequired":0,
 *                 "sapphireMinesRequired":0,
 *                 "diamondMinesRequired":3,
 *                 "onyxMinesRequired":3
 *             },
 *             {
 *                 "uuid":"2552e762-46ea-457d-93e9-f2eebe047f44",
 *                 "prestige":3,
 *                 "rubyMinesRequired":0,
 *                 "emeraldMinesRequired":4,
 *                 "sapphireMinesRequired":4,
 *                 "diamondMinesRequired":0,
 *                 "onyxMinesRequired":0
 *             },
 *             {
 *                 "uuid":"0dae8b0c-afd0-4379-b64b-c169cb7528c6",
 *                 "prestige":3,
 *                 "rubyMinesRequired":0,
 *                 "emeraldMinesRequired":0,
 *                 "sapphireMinesRequired":0,
 *                 "diamondMinesRequired":4,
 *                 "onyxMinesRequired":4
 *             }
 *         ],
 *         "firstLevelMinesCards":[
 *             {
 *                 "uuid":"00838a34-35a5-4057-8e54-d44f8e9e5b6f",
 *                 "prestige":1,
 *                 "color":"BLUE",
 *                 "rubyTokensRequired":4,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"593a7332-77d7-4f30-872d-4d310a16126d",
 *                 "prestige":1,
 *                 "color":"BLACK",
 *                 "rubyTokensRequired":0,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":4,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"3aad3655-2831-4c5f-b6d6-5ba59bb4c28c",
 *                 "prestige":0,
 *                 "color":"BLUE",
 *                 "rubyTokensRequired":2,
 *                 "emeraldTokensRequired":2,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":1,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"8e0f6fb5-004a-4ed8-89b3-49955fdd5ee5",
 *                 "prestige":0,
 *                 "color":"BLACK",
 *                 "rubyTokensRequired":3,
 *                 "emeraldTokensRequired":1,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":1
 *             }
 *         ],
 *         "secondLevelMinesCards":[
 *             {
 *                 "uuid":"822a6dc4-ad8f-4c27-a618-ee769be89b03",
 *                 "prestige":2,
 *                 "color":"GREEN",
 *                 "rubyTokensRequired":0,
 *                 "emeraldTokensRequired":5,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"b57e6ff6-69aa-4504-a9e1-0ca50b9461f6",
 *                 "prestige":2,
 *                 "color":"WHITE",
 *                 "rubyTokensRequired":5,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":3
 *             },
 *             {
 *                 "uuid":"a87fb24c-37c4-4359-a6dc-4505df7e8848",
 *                 "prestige":2,
 *                 "color":"WHITE",
 *                 "rubyTokensRequired":5,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"88b96c47-66d8-45ca-ae47-dd536298ab52",
 *                 "prestige":2,
 *                 "color":"RED",
 *                 "rubyTokensRequired":0,
 *                 "emeraldTokensRequired":2,
 *                 "sapphireTokensRequired":4,
 *                 "diamondTokensRequired":1,
 *                 "onyxTokensRequired":0
 *             }
 *         ],
 *         "thirdLevelMinesCards":[
 *             {
 *                 "uuid":"a4fd23b5-d0d0-4960-96a0-3f5c5a68a479",
 *                 "prestige":4,
 *                 "color":"WHITE",
 *                 "rubyTokensRequired":0,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":7
 *             },
 *             {
 *                 "uuid":"1b262ffc-8ca4-44d1-a6f2-063cf0d7619b",
 *                 "prestige":4,
 *                 "color":"BLACK",
 *                 "rubyTokensRequired":7,
 *                 "emeraldTokensRequired":0,
 *                 "sapphireTokensRequired":0,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"14ffcc43-d178-4476-afc4-9cdd55e0cc38",
 *                 "prestige":3,
 *                 "color":"BLACK",
 *                 "rubyTokensRequired":3,
 *                 "emeraldTokensRequired":5,
 *                 "sapphireTokensRequired":3,
 *                 "diamondTokensRequired":3,
 *                 "onyxTokensRequired":0
 *             },
 *             {
 *                 "uuid":"07b7827f-f31d-491f-95f3-da92f46eeb45",
 *                 "prestige":5,
 *                 "color":"GREEN",
 *                 "rubyTokensRequired":0,
 *                 "emeraldTokensRequired":3,
 *                 "sapphireTokensRequired":7,
 *                 "diamondTokensRequired":0,
 *                 "onyxTokensRequired":0
 *             }
 *         ],
 *         "userToPlay":{
 *             "uuid":"6850e6c1-6f1d-48c6-a412-52b39225ded7"
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

        public UserDTO(UUID uuid) {
            this.uuid = uuid;
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

        public int rubyMinesRequired;
        public int emeraldMinesRequired;
        public int sapphireMinesRequired;
        public int diamondMinesRequired;
        public int onyxMinesRequired;

        public NobleDataResponse(UUID uuid, int prestige, int rubyMinesRequired, int emeraldMinesRequired,
                int sapphireMinesRequired, int diamondMinesRequired, int onyxMinesRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.rubyMinesRequired = rubyMinesRequired;
            this.emeraldMinesRequired = emeraldMinesRequired;
            this.sapphireMinesRequired = sapphireMinesRequired;
            this.diamondMinesRequired = diamondMinesRequired;
            this.onyxMinesRequired = onyxMinesRequired;
        }
    }

    public class MinesCardDataResponse{
        public UUID uuid;
        public int prestige;
        public Color color;

        public int rubyTokensRequired;
        public int emeraldTokensRequired;
        public int sapphireTokensRequired;
        public int diamondTokensRequired;
        public int onyxTokensRequired;

        public MinesCardDataResponse(UUID uuid, int prestige, Color color, int rubyTokensRequired,
                int emeraldTokensRequired, int sapphireTokensRequired, int diamondTokensRequired, int onyxTokensRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.color = color;
            this.rubyTokensRequired = rubyTokensRequired;
            this.emeraldTokensRequired = emeraldTokensRequired;
            this.sapphireTokensRequired = sapphireTokensRequired;
            this.diamondTokensRequired = diamondTokensRequired;
            this.onyxTokensRequired = onyxTokensRequired;
        }
        
    }

    public class UserDataResponse {
        public UUID uuid;

        public UserDataResponse(UUID uuid) {
            this.uuid = uuid;
        }      
    }

    public class ResponseData{
        public TokensDataResponse tokens;
        public ArrayList<NobleDataResponse> nobles;

        public ArrayList<MinesCardDataResponse> firstLevelMinesCards;
        public ArrayList<MinesCardDataResponse> secondLevelMinesCards;
        public ArrayList<MinesCardDataResponse> thirdLevelMinesCards;

        public UserDataResponse userToPlay;

        public ResponseData(TokensDataResponse tokens,
                ArrayList<NobleDataResponse> nobles, ArrayList<MinesCardDataResponse> firstLevelMinesCards,
                ArrayList<MinesCardDataResponse> secondLevelMinesCards,
                ArrayList<MinesCardDataResponse> thirdLevelMinesCards,
                UserDataResponse userToPlay) {
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
            //validateData(dataDTO, database);

            User user = new User(dataDTO.userDTO.uuid, "janek", connectionHashCode);
            Room room = new Room(dataDTO.roomDTO.uuid, "pokoj", "123", user, database);

            //User user = database.getUser(dataDTO.userDTO.uuid);
            //Room room = database.getRoom(dataDTO.roomDTO.uuid);

            room.startGame();
            Game game = room.getGame();

            Log.DEBUG("Game started by "+user.getName()+". Room UUID: "+room.getUuid());

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

            UserDataResponse userToPlayResponse=new UserDataResponse(game.getCurrentPlayer().getUuid());
                  
            ResponseData responseData = new ResponseData(
                tokensDataResponse, nobleDataResponses, 
                firstLevelMinesCardsResponses, secondLevelMinesCardsResponses, 
                thirdLevelMinesCardsResponses,userToPlayResponse
            );


            ArrayList<User> players= room.getAllUsers();
            ServerMessage serverMessage = new ServerMessage(
                    userMessage.getContextId(), 
                    ServerMessageType.START_GAME_RESPONSE,
                    Result.OK, 
                    responseData
            );
            for(User player : players){          
                messenger.addMessageToSend(player.getConnectionHashCode(), serverMessage);
            }
       

        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.START_GAME_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }








    private void validateData(DataDTO dataDTO, Database database) throws  RoomDoesntExistException, UserDoesntExistException, RoomOwnershipException, InvalidUUIDException, RoomInGameException, RoomPlayerCountException{
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

        //Check number of players
        if(!(room.getPlayerCount()>=2 && room.getPlayerCount()<=4)){
            throw new RoomPlayerCountException("Cannot start game due to insufficient or overload number of players");
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
