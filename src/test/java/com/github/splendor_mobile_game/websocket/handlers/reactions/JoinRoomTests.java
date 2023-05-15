package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.InvalidReceivedMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinRoomTests {

    private Database database;
    private final String roomPassword = "PASSWORD";

    private String newBaseMessage(){
        return """
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
    }

    private String newBaseErrorResponse(){
        return """
        {
            "contextId":"$messageContextId",
            "type":"JOIN_ROOM_RESPONSE",
            "result":"FAILURE",
            "data":{
                "error":"$error"
            }
        }""";
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

        String roomName = "ROOM";
        message = message
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$roomName", roomName)
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

    @Test
    public void validRequestTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();
        String roomId = room.get("uuid").getAsString();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3455";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(2,messenger.getMessages().size());
        assertEquals(2,this.database.getAllUsers().size());
        User user = this.database.getAllUsers().get(1);
        assertEquals(userUuid, user.getUuid().toString());
        assertEquals(userName, user.getName());

        String expectedJsonString = """
                {
                   "contextId":"$messageContextId",
                   "type":"JOIN_ROOM_RESPONSE",
                   "result":"OK",
                   "data":{
                      "users":[
                        {
                            "uuid":"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                            "name":"OWNER"
                        },
                        {
                            "uuid":"$userId",
                            "name":"$userName"
                        }
                      ],
                      "room":{
                        "uuid":"$roomId",
                        "name":"ROOM"
                      }
                   }
                }
                """
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$userId",userUuid)
                .replace("$userName",userName)
                .replace("$roomId",roomId);

        String reply = messenger.getMessages().get(1).getMessage();
        JsonElement actualJson = JsonParser.parseString(reply);

        JsonObject jsonUser = actualJson.getAsJsonObject().get("data").getAsJsonObject().get("users").getAsJsonArray().get(1).getAsJsonObject();
        assertEquals(userUuid, jsonUser.get("uuid").getAsString());
        assertEquals(userName, jsonUser.get("name").getAsString());

        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);
    }

    @Test
    public void joinNonexistentRoomTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3455";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", "aaaaaa")
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Could not find a room with specified enterCode.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void JoinFullRoomTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c345";
        String userName = "JOINER";

        Messenger messenger = new Messenger();

        for (int i = 0; i < 3; i++) {
            String realUserUuid = userUuid + i;
            String realUserName = userUuid + i;
            int clientConnectionHashCode = 100001 + i;

            String message = this.newBaseMessage()
                    .replace("$userId", realUserUuid)
                    .replace("$userName", realUserName)
                    .replace("$enterCode", enterCode)
                    .replace("$roomPassword", this.roomPassword);

            UserMessage receivedMessage = new UserMessage(message);
            JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
            receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
            joinRoom.react();
        }

        assertEquals(4, this.database.getAllUsers().size());
        assertEquals(1,this.database.getAllRooms().size());
        Room databaseRoom = this.database.getAllRooms().get(0);
        assertEquals(4, databaseRoom.getAllUsers().size());

        String message = this.newBaseMessage()
                .replace("$userId", userUuid + "3")
                .replace("$userName", userName + "3")
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        UserMessage receivedMessage = new UserMessage(message);
        JoinRoom joinRoom = new JoinRoom(100004, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(4, this.database.getAllUsers().size());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Room has already reached maximum player count!");

        String reply = messenger.getMessages().get(messenger.getMessages().size() - 1).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userAlreadyJoinedRoomTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c345";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        receivedMessage = new UserMessage(message);
        messenger = new Messenger();
        joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Leave your current room before joining another.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void invalidRoomPasswordTest() throws InvalidReceivedMessage {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c345";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", "invalid");

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Wrong password!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void invalidUserUuidTest() {
        this.database = new InMemoryDatabase();
        JsonObject room = this.createRoom();
        String enterCode = room.get("enterCode").getAsString();

        String userUuid = "invalid-uuid";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", enterCode)
                .replace("$roomPassword", this.roomPassword);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(JoinRoom.DataDTO.class));
    }

    @Test
    public void invalidEnterCodeTest() {
        this.database = new InMemoryDatabase();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c345";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", "inv-ali")
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Invalid enter code format.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void emptyEnterCodeTest() {
        this.database = new InMemoryDatabase();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c345";
        String userName = "JOINER";

        String message = this.newBaseMessage()
                .replace("$userId", userUuid)
                .replace("$userName", userName)
                .replace("$enterCode", "")
                .replace("$roomPassword", this.roomPassword);

        int clientConnectionHashCode = 100001;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        JoinRoom joinRoom = new JoinRoom(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(JoinRoom.DataDTO.class);
        joinRoom.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId","80bdc250-5365-4caf-8dd9-a33e709a0117")
                .replace("$error","Invalid enter code format.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
}