package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.*;
import com.github.splendor_mobile_game.game.Exceptions.NotEnoughTokensException;
import com.github.splendor_mobile_game.game.Exceptions.SameTokenTypesException;
import com.github.splendor_mobile_game.game.enums.CardTier;
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
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;

@ReactionName("CREATE_ROOM")
public class CreateRoom extends Reaction {

    public CreateRoom(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class RoomDTO {
        public String name;
        public String password;

        public RoomDTO(String name, String password) {
            this.name = name;
            this.password = password;
        }

    }

    public static class UserDTO {
        public UUID uuid;
        public String name;

        public UserDTO(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

    }

    @DataClass
    public static class DataDTO {
        public RoomDTO roomDTO;
        public UserDTO userDTO;

        public DataDTO(RoomDTO roomDTO, UserDTO userDTO) {
            this.roomDTO = roomDTO;
            this.userDTO = userDTO;
        }

    }

    public class ResponseData {
        public UserDataResponse user;
        public RoomDataResponse room;

        public ResponseData(UserDataResponse user, RoomDataResponse room) {
            this.user = user;
            this.room = room;
        }
        
    }

    public class UserDataResponse {
        public UUID id;
        public String name;

        public UserDataResponse(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
        
    }

    public class RoomDataResponse {
        public String name;

        public RoomDataResponse(String name) {
            this.name = name;
        }

    }

    // {
    //     "messageContextId":"80bdc250-5365-4caf-8dd9-a33e709a0116",
    //     "type":"CREATE_ROOM_RESPONSE",
    //     "result":"OK",
    //     "data":{
    //         "user":{
    //             "id":"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
    //             "name":"James"
    //         },
    //         "room":{
    //             "name":"TajnyPokoj"
    //         }
    //     }
    // }

    /*   ----> EXAMPLE USER REQUEST <----
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "CREATE_ROOM",
         "data": {
             "roomDTO": {
                 "name": "TajnyPokoj",
                 "password": "Tajne6Przez2Poufne.;"
             },
             "userDTO": {
                 "uuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                 "name": "James"
             }
         }
     }
     */

    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {
            validateData(dataDTO, this.database);

            User user = new User(dataDTO.userDTO.uuid, dataDTO.userDTO.name, this.connectionHashCode);
            Room room = new Room(UUID.randomUUID(), dataDTO.roomDTO.name, dataDTO.roomDTO.password, user, database);

            Log.DEBUG("Kod pokoju: " + room.getEnterCode());
            
            Log.DEBUG("Uuid pokoju: " + room.getUuid());

            database.addUser(user);
            database.addRoom(room);

            //room.startGame();

            //User class testing code

            try {
                user.takeThreeTokens(TokenType.DIAMOND, TokenType.EMERALD, TokenType.RUBY);
                user.takeThreeTokens(TokenType.DIAMOND, TokenType.EMERALD, TokenType.RUBY);
                user.takeThreeTokens(TokenType.DIAMOND, TokenType.EMERALD, TokenType.RUBY);
            } catch (SameTokenTypesException e) {
                System.out.println(e.getMessage());
            }

            Card testCard = new Card(CardTier.LEVEL_2, 3, 3,0,1,3,0, TokenType.EMERALD);

            System.out.println(user.getTokenCount());
            System.out.println("Diamond");
            System.out.println(user.getTokenCount(TokenType.DIAMOND));
            System.out.println("Emerald");
            System.out.println(user.getTokenCount(TokenType.EMERALD));
            System.out.println("Ruby");
            System.out.println(user.getTokenCount(TokenType.RUBY));

            System.out.println();
            System.out.println();

            try {
                user.buyCard(testCard);
            } catch (NotEnoughTokensException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(user.getTokenCount());
            System.out.println("Diamond");
            System.out.println(user.getTokenCount(TokenType.DIAMOND));
            System.out.println("Emerald");
            System.out.println(user.getTokenCount(TokenType.EMERALD));
            System.out.println("Ruby");
            System.out.println(user.getTokenCount(TokenType.RUBY));

            user.takeTwoTokens(TokenType.GOLD_JOKER);

            System.out.println();
            System.out.println();

            Card testCard2 = new Card(CardTier.LEVEL_2, 3, 2,0,0,1,0, TokenType.EMERALD);

            try {
                user.buyCard(testCard2);
            } catch (NotEnoughTokensException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(user.getTokenCount());
            System.out.println("Diamond");
            System.out.println(user.getTokenCount(TokenType.DIAMOND));
            System.out.println("Emerald");
            System.out.println(user.getTokenCount(TokenType.EMERALD));
            System.out.println("Ruby");
            System.out.println(user.getTokenCount(TokenType.RUBY));

            //end of User class testing code

            UserDataResponse userDataResponse = new UserDataResponse(dataDTO.userDTO.uuid, dataDTO.userDTO.name);
            RoomDataResponse roomDataResponse = new RoomDataResponse(dataDTO.roomDTO.name);
            ResponseData responseData = new ResponseData(userDataResponse, roomDataResponse);
            ServerMessage serverMessage = new ServerMessage(userMessage.getMessageContextId(), ServerMessageType.CREATE_ROOM_RESPONSE, Result.OK, responseData);

            messenger.addMessageToSend(this.connectionHashCode, serverMessage);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.CREATE_ROOM_RESPONSE, userMessage.getMessageContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);
        }
    }


    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, InvalidUsernameException, RoomAlreadyExistsException, InvalidPasswordException, UserAlreadyInRoomException {
        Pattern uuidPattern     = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Pattern usernamePattern = Pattern.compile("^(?=.*\\p{L})[\\p{L}\\p{N}\\s]+$");
        Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\p{Punct}]+$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format.");


        // Check if user UUID matches the pattern
        Matcher usernameMatcher = usernamePattern.matcher(dataDTO.userDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid username credentials.");


        // Check if user UUID matches the pattern
        usernameMatcher = usernamePattern.matcher(dataDTO.roomDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid room name format.");


        // Check if user UUID matches the pattern
        Matcher passwordMatcher = passwordPattern.matcher(dataDTO.roomDTO.password);
        if (!passwordMatcher.find())
            throw new InvalidPasswordException("Invalid room password format.");


        // Check if room with specified name already exists NAME OF THE ROOM MUST BE UNIQUE
        if (database.getRoom(dataDTO.roomDTO.name) != null)
            throw new RoomAlreadyExistsException("Room with specified name already exists!");


        // Check if user is already a member of any room
        database.isUserInRoom(dataDTO.userDTO.uuid);

    }
}
