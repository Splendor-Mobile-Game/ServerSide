package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.exception.InvalidUUIDException;
import com.github.splendor_mobile_game.exception.InvalidUsernameException;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CreateRoom extends Reaction {

    public CreateRoom(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
    }

    private class RoomDTO {

        public String name;
        public String password;
    }

    private class UserDTO {
        private UUID id;
        public String name;
    }

    @DataClass
    private class DataDTO {
        public UserDTO userDTO;
        public RoomDTO roomDTO;
    }

    /*
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "CreateRoom",
         "data": {
             "userDTO": {
                 "id": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
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
            validateData(dataDTO);

            User user = new User(dataDTO.userDTO.id, dataDTO.userDTO.name, this.connectionHashCode);
            Room room = new Room(UUID.randomUUID(), dataDTO.roomDTO.name, dataDTO.roomDTO.password, user);

            database.addUser(user);
            database.addRoom(room);

            JsonObject userJson = new JsonObject();
            userJson.addProperty("id", dataDTO.userDTO.id.toString());
            userJson.addProperty("name", dataDTO.userDTO.name);

            JsonObject roomJson = new JsonObject();
            roomJson.addProperty("name", dataDTO.roomDTO.name);

            JsonObject data = new JsonObject();
            data.add("user", userJson);
            data.add("room", roomJson);

            JsonObject response = new JsonObject();
            response.addProperty("messageContextId", receivedMessage.getMessageContextId());
            response.addProperty("type", "CreateRoomResponse");
            response.addProperty("result", "OK");
            response.add("data", data);

            messenger.addMessageToSend(this.connectionHashCode, (new Gson()).toJson(response));

        } catch (Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(
                Result.FAILURE, 
                e.getMessage(), 
                "CreateRoomResponse",
                receivedMessage.getMessageContextId()
            );

            messenger.addMessageToSend(connectionHashCode, errorResponse.ToJson());
        }
    }

    private void validateData(DataDTO dataDTO) throws InvalidUUIDException, InvalidUsernameException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.id.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern

        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher usernameMatcher = usernamePattern.matcher(dataDTO.userDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid username credentials."); // Check if user UUID matches the pattern

        usernameMatcher = usernamePattern.matcher(dataDTO.roomDTO.name);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid room name format."); // Check if user UUID matches the pattern

        usernameMatcher = usernamePattern.matcher(dataDTO.roomDTO.password);
        if (!usernameMatcher.find())
            throw new InvalidUsernameException("Invalid room password format."); // Check if user UUID matches the pattern
    }
}
