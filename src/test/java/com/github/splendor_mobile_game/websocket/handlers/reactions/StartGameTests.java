package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartGameTests {

    private Database database;

    private String newBaseMessage() {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "START_GAME",
                    "data": {
                        "userDTO":{
                            "uuid": "$userUuid"
                        },
                        "roomDTO":{
                            "uuid": "$roomUuid"
                        }
                    }
                }
                """;
    }

    private String newBaseErrorMessage() {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "START_GAME_RESPONSE",
                    "result": "FAILURE",
                    "data": {
                        "error": "$error"
                    }
                }
                """;
    }

    @Test
    public void invalidUserUuidTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "invalid-uuid";
        String roomUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", userUuid)
                .replace("$roomUuid", roomUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(StartGame.DataDTO.class));
    }

    @Test
    public void invalidRoomUuidTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String roomUuid = "invalid-uuid";

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", userUuid)
                .replace("$roomUuid", roomUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(StartGame.DataDTO.class));
    }

    @Test
    public void userNotFoundTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String roomUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", userUuid)
                .replace("$roomUuid", roomUuid);

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "Couldn't find a user with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void roomNotFoundTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String roomUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User user = new User(UUID.randomUUID(), "USER", 100000);
        this.database.addUser(user);

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", user.getUuid().toString())
                .replace("$roomUuid", roomUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(user.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(user.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "Couldn't find a room with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void playerNotOwnerTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        this.database.addUser(owner);
        this.database.addUser(player);

        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(player);
        this.database.addRoom(room);

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", player.getUuid().toString())
                .replace("$roomUuid", room.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "You are not an owner of the room.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void gameAlreadyStartedTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        this.database.addUser(owner);
        this.database.addUser(player);

        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(player);
        this.database.addRoom(room);

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$roomUuid", room.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String reply = messenger.getMessages().get(0).getMessage();
        JsonObject jsonObject = JsonParser.parseString(reply).getAsJsonObject();
        assertEquals(ServerMessageType.START_GAME_RESPONSE.toString(), jsonObject.get("type").getAsString());
        assertEquals(Result.OK.toString(), jsonObject.get("result").getAsString());

        receivedMessage = new UserMessage(message);
        messenger = new Messenger();
        startGame = new StartGame(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "The game has already started!");

        reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void insufficientPlayerCountTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        this.database.addUser(owner);

        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        this.database.addRoom(room);

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$roomUuid", room.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "Cannot start the game due to insufficient or overload number of players.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void overloadPlayerCountTest() {
        this.database = new InMemoryDatabase();

        String contextUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        this.database.addUser(owner);

        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        for (int i = 0; i < 4; i++) {
            User player = new User(UUID.randomUUID(), "PLAYER" + (i + 1), 100001 + i);
            this.database.addUser(player);
            room.joinGame(player);
        }

        this.database.addRoom(room);

        String message = this.newBaseMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$roomUuid", room.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        StartGame startGame = new StartGame(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(StartGame.DataDTO.class);
        startGame.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$contextUuid", contextUuid)
                .replace("$error", "Cannot start the game due to insufficient or overload number of players.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
}
