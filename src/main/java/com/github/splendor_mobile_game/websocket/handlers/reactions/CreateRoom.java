package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.AlreadyAnOwnerException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUsernameException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomAlreadyExistsException;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.ResponseType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ReactionName("CREATE_ROOM")
public class CreateRoom extends Reaction {

    public CreateRoom(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
    }

    private class RoomDTO {

        public String name;
        public String password;
    }

    private class UserDTO {
        public UUID uuid;
        public String name;
    }

    @DataClass
    private class DataDTO {
        public UserDTO userDTO;
        public RoomDTO roomDTO;
    }

    @ResponseClass
    private class Response {
        UUID messageContextId;
        String type;
        Result result;
        ResponseData data;

        public Response(UUID messageContextId, String type, Result result, ResponseData data) {
            this.messageContextId = messageContextId;
            this.type = type;
            this.result = result;
            this.data = data;
        }

    }

    private class ResponseData {
        UserDataResponse user;
        RoomDataResponse room;

        public ResponseData(UserDataResponse user, RoomDataResponse room) {
            this.user = user;
            this.room = room;
        }
        
    }

    private class UserDataResponse {
        public UUID id;
        public String name;

        public UserDataResponse(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
        
    }

    private class RoomDataResponse {
        String name;

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
             "userDTO": {
                 "uuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                 "name": "James"
             },
             "roomDTO": {
                 "name": "TajnyPokoj",
                 "password": "kjashjkasd"
             }
         }
     }
     */

    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) receivedMessage.getData();

        try {
            validateData(dataDTO, this.database);

            User user = new User(dataDTO.userDTO.uuid, dataDTO.userDTO.name, this.connectionHashCode);
            Room room = new Room(UUID.randomUUID(), dataDTO.roomDTO.name, dataDTO.roomDTO.password, user);

            Log.DEBUG("UUID pokoju: " + room.getUuid());

            database.addUser(user);
            database.addRoom(room);

            UserDataResponse userDataResponse = new UserDataResponse(dataDTO.userDTO.uuid, dataDTO.userDTO.name);
            RoomDataResponse roomDataResponse = new RoomDataResponse(dataDTO.roomDTO.name);
            ResponseData responseData = new ResponseData(userDataResponse, roomDataResponse);
            Response response = new Response(UUID.fromString(receivedMessage.getMessageContextId()), ResponseType.CREATE_ROOM_RESPONSE.toString(), Result.OK, responseData);

            messenger.addMessageToSend(this.connectionHashCode, response);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE, e.getMessage(), ResponseType.CREATE_ROOM_RESPONSE, receivedMessage.getMessageContextId());
            messenger.addMessageToSend(connectionHashCode, errorResponse.ToJson());
        }
    }


    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, InvalidUsernameException, RoomAlreadyExistsException, AlreadyAnOwnerException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern


        // Check if user UUID matches the pattern
        Pattern usernamePattern = Pattern.compile("^(?=.*\\p{L})[\\p{L}\\p{N}\\s]+$");
        Matcher usernameMatcher = usernamePattern.matcher(dataDTO.userDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid username credentials.");


        // Check if user UUID matches the pattern
        usernameMatcher = usernamePattern.matcher(dataDTO.roomDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid room name format.");


        // Check if user UUID matches the pattern
        usernameMatcher = usernamePattern.matcher(dataDTO.roomDTO.password);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid room password format.");


        // Check if room with specified name already exists NAME OF THE ROOM MUST BE UNIQUEd
        if (database.getRoom(dataDTO.roomDTO.name) != null)
            throw new RoomAlreadyExistsException("Room with specified name already exists!");


        // Check if user is already an owner of some other room
        for(Room room : database.getAllRooms()) {
            if (room.getOwner().getUuid().equals(dataDTO.userDTO.uuid))
                throw new AlreadyAnOwnerException("You are already an owner of " + room.getName() + " room.");
        }

    }
}
