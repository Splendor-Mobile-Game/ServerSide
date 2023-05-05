package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.InvalidReceivedMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LeaveRoomTests {

    private Database database;

    private final String roomName = "ROOM";
    private final String roomPassword = "PASSWORD";

    private String newBaseMessage(){
        return """
            {
                "contextId": "$messageContextId",
                "type": "LEAVE-ROOM",
                "data": {
                    "userDTO": {
                        "uuid": "$userId"
                    },
                    "roomDTO": {
                        "uuid": "$roomId"
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
        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
        String userName = "OWNER";

        String message = """
            {
                "contextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
                "type": "CREATE_ROOM",
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

        assertEquals(2, messenger.getMessages().size());
        assertEquals(2, this.database.getAllUsers().size());
        User user = this.database.getAllUsers().get(1);
        assertEquals(userId, user.getUuid().toString());
        assertEquals(userName, user.getName());
    }

    @Test
    public void validRequestTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();
        String roomId = room.get("uuid").getAsString();

        String messageContextId = "80bdc250-5365-4caf-8dd9-a33e709a0118";
        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3455";
        String userName = "JOINER";
        this.joinRoom(userUuid, userName, enterCode);

        String message = this.newBaseMessage()
                .replace("$messageContextId", messageContextId)
                .replace("$userId", userUuid)
                .replace("$roomId", roomId);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        LeaveRoom leaveRoom = new LeaveRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(LeaveRoom.DataDTO.class);
        leaveRoom.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(1, this.database.getAllUsers().size());

        String expectedJsonString = """
                {
                   "contextId":"$messageContextId",
                   "type":"LEAVE_ROOM_RESPONSE",
                   "result":"OK",
                   "data":{
                      "user":{
                         "id":"$userId",
                         "name":"$userName"
                      }
                   }
                }
                """
                .replace("$messageContextId", "80bdc250-5365-4caf-8dd9-a33e709a0118")
                .replace("$userId", userUuid)
                .replace("$userName", userName);

        String reply = messenger.getMessages().get(0).getMessage();
        JsonElement actualJson = JsonParser.parseString(reply);

        JsonObject user = actualJson.getAsJsonObject().get("data").getAsJsonObject().get("user").getAsJsonObject();
        assertEquals(userUuid, user.get("id").getAsString());
        assertEquals(userName, user.get("name").getAsString());

        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);
    }
}
