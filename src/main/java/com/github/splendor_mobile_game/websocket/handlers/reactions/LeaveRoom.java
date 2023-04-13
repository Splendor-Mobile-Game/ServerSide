package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;

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
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserNotAMemberException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ReactionName("LEAVE_ROOM")
public class LeaveRoom extends Reaction{

    public LeaveRoom(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);

    }
    public static class UserDTO{
        public UUID uuid;
        public UserDTO(UUID uuid){
            this.uuid=uuid;
        }
    }
    public static class RoomDTO {
        public UUID uuid;
        public RoomDTO(UUID uuid){
            this.uuid=uuid;
        }
    }
    @DataClass
    public static class DataDTO{
        private UserDTO userDTO;
        private RoomDTO roomDTO;
        public DataDTO(RoomDTO roomDTO, UserDTO userDTO) {
            this.roomDTO = roomDTO;
            this.userDTO = userDTO;
        }
    }
    
    public static class ResponseData {
        public UserDataResponse user;

        public ResponseData(UserDataResponse user) {
            this.user = user;
        }
        
    }
    
    public static class UserDataResponse {
        public UUID id;
        public String name;
        public UserDataResponse(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
        
    }

    /* ----> EXAMPLE USER REQUEST <----
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "LEAVE_ROOM",
         "data": {
             "roomDTO": {
                "uuid": "a88f224f-f656-4925-9341-dda4b9099e90"
             },
             "userDTO": {
                 "uuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3456"
             }
         }
     }
     */

    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) userMessage.getData();

        try {

            validateData(dataDTO, database);
            User user = database.getUser(dataDTO.userDTO.uuid);
            Room room = database.getRoom(dataDTO.roomDTO.uuid);
            ArrayList<User> usersTmp = room.getAllUsers();
            room.leaveGame(user);
            database.getAllUsers().remove(user);

            if(room.getPlayerCount()>0)
                //checking if user who wants to leave room isn't owner, if that's true, setting new owner as another user from list of users
                if(room.getOwner().equals(user)){
                    room.setOwner(room.getAllUsers().get(0));
                    
                    UserDataResponse userDataResponse = new UserDataResponse(room.getOwner().getUuid(), room.getOwner().getName());
                    ResponseData responseData = new ResponseData(userDataResponse);
                    ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.NEW_ROOM_OWNER, Result.OK, responseData);

                    messenger.addMessageToSend(this.connectionHashCode, serverMessage);

                    //Send information about new room owner to other players
                    for (User u : usersTmp) {
                        messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
                    }
                    
                }

            //If last user wants to leave room, then remove empty room
            if(room.getPlayerCount()==0)
                database.getAllRooms().remove(room);

            UserDataResponse userDataResponse = new UserDataResponse(dataDTO.userDTO.uuid, user.getName());
            ResponseData responseData = new ResponseData(userDataResponse);
            ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.LEAVE_ROOM_RESPONSE, Result.OK, responseData);

            messenger.addMessageToSend(this.connectionHashCode, serverMessage);
            

            //Send leave information to other players
            for (User u : usersTmp) {
                messenger.addMessageToSend(u.getConnectionHashCode(), serverMessage);
            }


        } catch(Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE,e.getMessage(), ServerMessageType.LEAVE_ROOM_RESPONSE, userMessage.getContextId().toString());
            messenger.addMessageToSend(connectionHashCode, errorResponse);

        }
        
    }
    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, UserNotAMemberException, RoomDoesntExistException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern


        // Check if room UUID matches the pattern
        uuidMatcher = uuidPattern.matcher(dataDTO.roomDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if room UUID matches the pattern


        // Check if room exists
        Room room = database.getRoom(dataDTO.roomDTO.uuid);
        if (room == null)
            throw new RoomDoesntExistException("Could not find a room with specified UUID.");


        // Check if user is a member of the room
        User user = database.getUser(dataDTO.userDTO.uuid);
        if (user != null) {
            if(!room.userExists(user)){
                throw new UserNotAMemberException("User is not a member of this room");
            }
        }

    }

}

