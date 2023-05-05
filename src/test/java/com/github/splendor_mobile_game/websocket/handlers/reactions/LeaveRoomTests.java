package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.InvalidReceivedMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeaveRoomTests {

    private Database database;

    private final String roomName = "ROOM-NAME";
    private final String roomPassword = "ROOM-PASSWORD";

    private String newBaseMessage(){
        return """
            {
                "contextId": "$messageContextId",
                "type": "$type",
                "data": {
                    "userDTO": {
                        "uuid": "$userId",
                        "name": "$userName"
                    },
                    "roomDTO": {
                        "name": "$roomName",
                        "password": "$roomPassword"
                    }
                }
            }
            """;
    }

    private String newBaseErrorResponse(){
        return """
        {
            "contextId":"$messageContextId",
            "type":"$responseType",
            "result":"$result",
            "data":{
                "error":"$error"
            }
        }"""
                .replace("$responseType", ServerMessageType.LEAVE_ROOM_RESPONSE.toString())
                .replace("$result", Result.FAILURE.toString());
    }

    private JsonObject createRoom()
    {
        String messageContextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "CREATE_ROOM";
        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
        String userName = "ROOM-OWNER";

        String message = """
            {
                "contextId": "$messageContextId",
                "type": "$type",
                "data": {
                    "userDTO": {
                        "uuid": "$userId",
                        "name": "$userName"
                    },
                    "roomDTO": {
                        "name": "$roomName",
                        "password": "$roomPassword"
                    }
                }
            }
            """;

        message = message
                .replace("$messageContextId", messageContextId)
                .replace("$type", messageType)
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$roomName", this.roomName)
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        CreateRoom createRoom = new CreateRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(CreateRoom.DataDTO.class);
        createRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(1, this.database.getAllUsers().size());
        User user = this.database.getAllUsers().get(0);
        assertEquals(userUuid, user.getUuid().toString());
        assertEquals(userName, user.getName());

        JsonElement response = JsonParser.parseString(messenger.getMessages().get(0).getMessage());
        return response.getAsJsonObject().get("data").getAsJsonObject().get("room").getAsJsonObject();
    }

    private void joinRoom(String userId, String userName, String enterCode) {
        String message = """
            {
                "contextId": "80bdc250-5365-4caf-8dd9-a33e709a0117",
                "type": "JOIN_ROOM",
                "data": {
                    "userDTO": {
                        "uuid": "$userId",
                        "name": "$userName"
                    },
                    "roomDTO": {
                        "enterCode": "$enterCode",
                        "password": "$roomPassword"
                    }
                }
            }
            """;

        message = message
                .replace("$userId", userId)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(2, this.database.getAllUsers().size());
        User user = this.database.getAllUsers().get(1);
        assertEquals(userId, user.getUuid().toString());
        assertEquals(userName, user.getName());
    }

    @Test
    public void validRequestTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        this.createRoom();

        String messageContextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "LEAVE_ROOM";
        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
        String userName = "James";
        String roomName = "TajnyPokoj";
        String roomPassword = "kjashjkasd";
    }
}
