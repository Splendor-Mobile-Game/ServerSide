package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.splendor_mobile_game.database.Database;
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

// TODO: This whole class can be unit tested

/**
 * Reaction sent when a new player joins to already created room.
 * react() function should send information about new player joining, to all other users. Message type should be equivalent to `JOIN_ROOM_RESPONSE`
 *
 * Example user request
  {
       "contextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
       "type": "JOIN_ROOM",
       "data": {
           "roomDTO": {
               "enterCode": "dbjvVn",
               "password": "Tajne6Przez2Poufne.;"
           },
           "userDTO": {
               "uuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3456",
               "name": "Jacuch"
           }
      }
  }
 *
 *
 * If everything is alright, then the server should generate and send to everyone a response containing list of users
 * that are members of the room, and details of the room.
 *
 *
 * Example server response
 {
    "contextId":"80bdc250-5365-4caf-8dd9-a33e709a0116",
    "type":"JOIN_ROOM_RESPONSE",
    "result":"OK",
    "data":{
        "users":[
            {
                "uuid":"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                "name":"James"
            },
            {
                "uuid":"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3456",
                "name":"Jacuch"
            }
        ],
        "room":{
            "uuid":"852999c6-43df-448d-b866-b77a10ad25d2",
            "name":"TajnyPokoj"
        }
    }
 }
 *
 *
 *
 * Otherwise, server should generate an ERROR response sent only to the author of the request.
 * Example response while error occurs:
 {
    "contextId":"80bdc250-5365-4caf-8dd9-a33e709a0116",
    "type":"JOIN_ROOM_RESPONSE",
    "result":"FAILURE",
    "data":{
        "error":"Leave your current room before joining another."
    }
 }

 *
 *
 * Validation:
 * -> regex user uuid
 * -> regex room's enterCode
 * -> check if room exists
 * -> check if user is already in any room
 * -> check if room still has room for new player (player count less than 4)
 * -> validate password correctness
 *
 *
 * Model specification:
 * -> add new user to the database
 * -> add user to the Room.java instance (find room by id specified by user)
 *
 */
@ReactionName("JOIN_ROOM")
public class JoinRoom extends Reaction {

    public JoinRoom(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }


    /**
     * Room information sent by user
     */
    public static class RoomDTO {
        public String enterCode;
        public String password;

        public RoomDTO(String enterCode, String password) {
            this.enterCode = enterCode;
            this.password = password;
        }

    }

    /**
     * Sender information sent by user
     */
    public static class UserDTO {
        public UUID uuid;
        public String name;

        public UserDTO(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

    }


    /**
     * Data sent by the user
     */
    @DataClass
    public static class DataDTO {
        private RoomDTO roomDTO;
        private UserDTO userDTO;

        public DataDTO(RoomDTO roomDTO, UserDTO userDTO) {
            this.roomDTO = roomDTO;
            this.userDTO = userDTO;
        }

    }


    /**
     * Data sent by the server
     */
     public class ResponseData {
        public List<UserDataResponse> users;
        public RoomDataResponse room;

        public ResponseData(List<UserDataResponse> users, RoomDataResponse room) {
            this.users = users;
            this.room = room;
        }
        
    }


    /**
     * User data sent by the server
     */
    public class UserDataResponse {
        public UUID uuid;
        public String name;

        public UserDataResponse(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }
        
    }

    /**
     * Room data sent by the server
     */
    public class RoomDataResponse {
        public UUID uuid;
        public String name;
        
        public RoomDataResponse(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

    }



    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {

            validateData(dataDTO, database);

            Room room = database.getRoom(dataDTO.roomDTO.enterCode);
            User user = new User(dataDTO.userDTO.uuid, dataDTO.userDTO.name, this.connectionHashCode);
            database.addUser(user);
            room.joinGame(user);

            RoomDataResponse roomData = new RoomDataResponse(room.getUuid(), room.getName());
            
            List<UserDataResponse> usersData = new ArrayList<UserDataResponse>();
            usersData.add(new UserDataResponse(room.getOwner().getUuid(), room.getOwner().getName())); // add the owner to the users list
            for (User roomUser : room.getAllUsers()) {
                if (roomUser.equals(room.getOwner())) // skip the owner
                    continue;
                UserDataResponse userDTO = new UserDataResponse(roomUser.getUuid(), roomUser.getName());
                usersData.add(userDTO);
            }
            
            ResponseData responseData = new ResponseData(usersData, roomData);
            ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.JOIN_ROOM_RESPONSE, Result.OK, responseData);
            
            // Send join information to all players
            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
            }

        } catch(Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ServerMessageType.JOIN_ROOM_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);

        }

    }


    /**
     * Validate user data and check if game logic allows joining to the specified room
     *
     * @param dataDTO data provided by user
     * @param database database instance
     * @throws InvalidUUIDException thrown when UUID format is invalid
     * @throws RoomDoesntExistException thrown when specified room doesn't exist
     * @throws UserAlreadyInRoomException thrown if user is already a member of any room
     * @throws RoomFullException thrown when room has already reached a maximum number of members
     * @throws InvalidEnterCodeException thrown when enter code has invalid format
     * @throws InvalidPasswordException thrown when provided password is incorrect
     */
    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, RoomDoesntExistException, UserAlreadyInRoomException, RoomFullException, InvalidEnterCodeException, InvalidPasswordException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Pattern codePattern = Pattern.compile("^([0-9a-zA-Z]+){6}$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern


        // Check if room enterCode matches the pattern
        Matcher codeMatcher = codePattern.matcher(dataDTO.roomDTO.enterCode);
        if (!codeMatcher.find())
            throw new InvalidEnterCodeException("Invalid enter code format.");


        // Check if room exists
        Room room = database.getRoom(dataDTO.roomDTO.enterCode);
        if (room == null)
            throw new RoomDoesntExistException("Could not find a room with specified enterCode.");


        // Check if password is valid
        if (!dataDTO.roomDTO.password.equals(room.getPassword()))
            throw new InvalidPasswordException("Wrong password!");


        // Check players count reached maximum number
        if (room.getPlayerCount() == 4)
            throw new RoomFullException("Room has already reached maximum player count!");


        // Check if user is already a member of any room
        database.isUserInRoom(dataDTO.userDTO.uuid);
    }


}