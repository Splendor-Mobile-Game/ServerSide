package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
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

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ReactionName("LEAVE_ROOM")
public class LeaveRoom extends Reaction{

    public LeaveRoom(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);

    }
    private class UserDTO{
        public UUID uuid;
        public String name;
    }
    private class RoomDTO {
        public String enterCode;
        public String password;
    }
    @DataClass
    private class DataDTO{
        private UserDTO userDTO;
        private RoomDTO roomDTO;
    }
    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) receivedMessage.getData();

        try {

            //validateData(dataDTO, database);
            User user = database.getUser(dataDTO.userDTO.uuid);
            Room room = database.getRoom(dataDTO.roomDTO.enterCode);
            room.leaveGame(user);

            JsonObject userJson = new JsonObject();
            userJson.addProperty("uuid", dataDTO.userDTO.uuid.toString());
            userJson.addProperty("name", user.getName());

            JsonObject data = new JsonObject();
            data.add("user", userJson);

            JsonObject response = new JsonObject();
            response.addProperty("messageContextId", receivedMessage.getMessageContextId());
            response.addProperty("type", ResponseType.LEAVE_ROOM_RESPONSE.toString());
            response.addProperty("result", Result.OK.toString());
            response.add("data", data);

            // Send join information to other players
            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHasCode(), (new Gson()).toJson(response));
            }

        } catch(Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE,e.getMessage(), ResponseType.LEAVE_ROOM_RESPONSE, receivedMessage.getMessageContextId());
            messenger.addMessageToSend(connectionHashCode, errorResponse.ToJson());

        }
        
    }
    /*private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, RoomDoesntExistException, UserAlreadyInRoomException, RoomFullException, InvalidEnterCodeException, InvalidPasswordException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Pattern codePattern = Pattern.compile("^([0-9a-zA-Z]+){6}$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern


        // Check if room UUID matches the pattern
        Matcher codeMatcher = codePattern.matcher(dataDTO.roomDTO.enterCode);
        if (!codeMatcher.find())
            throw new InvalidEnterCodeException("Invalid enter code format.");


        // Check if room exists
        Room room = database.getRoom(dataDTO.roomDTO.enterCode);
        if (room == null)
            throw new RoomDoesntExistException("Could not find a room with specified UUID.");


        // Check if password is valid
        if (!dataDTO.roomDTO.password.equals(room.getPassword()))
            throw new InvalidPasswordException("Wrong password!");


        // Check players count reached maximum number
        if (room.getPlayerCount() == 4)
            throw new RoomFullException("Room has already reached maximum player count!");


        // Check if user is already a member of any room
        User user = database.getUser(dataDTO.userDTO.uuid);
        if (user != null) {
            for (Room r : database.getAllRooms())
                if (r.getAllUsers().contains(user))
                    throw new UserAlreadyInRoomException("Leave your current room before joining another.");
        }
    }*/

}