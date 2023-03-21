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
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.ResponseType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CreateRoom extends Reaction {

    public CreateRoom(int connectionHashCode) {
        super(connectionHashCode);
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

    /*   ----> EXAMPLE USER REQUEST <----
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "CreateRoom",
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
    public void react(ReceivedMessage parsedMessage, Messenger messenger, Database database) {

        DataDTO receivedMessage = (DataDTO) parsedMessage.getData();

        try {
            validateData(receivedMessage, database);

            User user = new User(receivedMessage.userDTO.uuid, receivedMessage.userDTO.name, this.connectionHashCode);
            Room room = new Room(UUID.randomUUID(), receivedMessage.roomDTO.name, receivedMessage.roomDTO.password, user);

            Log.DEBUG("UUID pokoju: " + room.getUuid());

            database.addUser(user);
            database.addRoom(room);

            JsonObject userJson = new JsonObject();
            userJson.addProperty("uuid", receivedMessage.userDTO.uuid.toString());
            userJson.addProperty("name", receivedMessage.userDTO.name);

            JsonObject roomJson = new JsonObject();
            roomJson.addProperty("name", receivedMessage.roomDTO.name);

            JsonObject data = new JsonObject();
            data.add("user", userJson);
            data.add("room", roomJson);

            JsonObject response = new JsonObject();
            response.addProperty("messageContextId", parsedMessage.getMessageContextId());
            response.addProperty("type", ResponseType.CREATE_ROOM_RESPONSE.toString());
            response.addProperty("result", Result.OK.toString());
            response.add("data", data);

            messenger.addMessageToSend(this.connectionHashCode, (new Gson()).toJson(response));

        } catch (Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE,e.getMessage(),ResponseType.CREATE_ROOM_RESPONSE, parsedMessage.getMessageContextId());
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
