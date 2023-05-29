package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;

import static org.junit.jupiter.api.Assertions.*;

public class MakeReservationFromDeckTests {

    private Database database;

    private String newBaseMessage() {
        return """
                {
                    "contextId":"02442d1b-2095-4aaa-9db1-0dae99d88e00",
                    "type": "MAKE_RESERVATION_FROM_DECK",
                    "data": {
                        "userUuid": "$userUuid",
                        "cardTier": "$cardTier"
                    }
                }
                """;
    }

    private String newBaseErrorResponse(){
        return """
        {
            "contextId":"$messageContextId",
            "type":"MAKE_RESERVATION_FROM_DECK_RESPONSE",
            "result":"FAILURE",
            "data":{
                "error":"$error"
            }
        }""";
    }

    @Test
    public void validRequestTest() {
        this.database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardTier", cardTier);

        Messenger messenger = new Messenger();
        UserMessage receivedMessage = new UserMessage(message);
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        Log.DEBUG(message);
        mrfd.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String reply = messenger.getMessages().get(0).getMessage();
        JsonElement actualJson = JsonParser.parseString(reply);

        assertEquals(ServerMessageType.MAKE_RESERVATION_FROM_DECK_ANNOUNCEMENT.toString(), actualJson.getAsJsonObject().get("type").getAsString());
        assertEquals(Result.OK.toString(), actualJson.getAsJsonObject().get("result").getAsString());

        assertTrue(owner.hasPerformedAction());
    }

    @Test
    public void invalidUserUuidTest() {
        String userUuid = "invalid-uuid";
        String cardTier = "1";

        String message = this.newBaseMessage()
                .replace("$userUuid", userUuid)
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        Throwable throwable = assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class));
        assertTrue(throwable.getMessage().contains("Failed parsing"));
    }

    @Test
    public void userNotFoundTest() {
        this.database = new InMemoryDatabase();

        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3455";
        String cardTier = "1";

        String message = this.newBaseMessage()
                .replace("$userUuid", userUuid)
                .replace("$cardTier", cardTier);

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "Couldn't find a user with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userNotARoomMemberTest() {
        this.database = new InMemoryDatabase();

        User player = new User(UUID.randomUUID(), "PLAYER", 100000);
        this.database .addUser(player);

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "You are not a member of any room!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void gameNotStartedTest() {
        this.database = new InMemoryDatabase();

        User player = new User(UUID.randomUUID(), "PLAYER", 100000);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", player, this.database);
        this.database.addUser(player);
        this.database.addRoom(room);

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "The game hasn't started yet!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void otherPlayerTurnTest() {
        this.database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "It is not your turn!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void reservationLimitReachedTest() {
        this.database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardTier", cardTier);

        Messenger messenger = new Messenger();
        for (int i = 0; i < 3; i++) {
            UserMessage receivedMessage = new UserMessage(message);
            MakeReservationFromDeck mrfd = new MakeReservationFromDeck(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
            receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
            mrfd.react();
        }

        messenger = new Messenger();
        UserMessage receivedMessage = new UserMessage(message);
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "You have reached the current reserved cards limit.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void gameReservationLimitReachedTest() {
        this.database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardTier", cardTier);

        Messenger messenger = new Messenger();
        for (int i = 0; i < 3; i++) {
            UserMessage receivedMessage = new UserMessage(message);
            MakeReservationFromDeck mrfd = new MakeReservationFromDeck(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
            receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
            mrfd.react();
        }

        room.changeTurn();

        message = this.newBaseMessage()
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardTier", cardTier);

        for (int i = 0; i < 2; i++) {
            UserMessage receivedMessage = new UserMessage(message);
            MakeReservationFromDeck mrfd = new MakeReservationFromDeck(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
            receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
            mrfd.react();
        }

        messenger = new Messenger();
        UserMessage receivedMessage = new UserMessage(message);
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "You have reached the limit of reserved cards per game.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void maxTokenCountReachedTest() {
        this.database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        Map<TokenType, Integer> tokens = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if(type == TokenType.GOLD_JOKER) continue;
            tokens.put(type, 0);
        }
        tokens.put(TokenType.EMERALD, 10);
        owner.changeTokens(tokens);

        room.startGame();

        String cardTier = "1";
        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        MakeReservationFromDeck mrfd = new MakeReservationFromDeck(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        mrfd.react();

        assertEquals(1,messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorResponse()
                .replace("$messageContextId", "02442d1b-2095-4aaa-9db1-0dae99d88e00")
                .replace("$error", "You have reached the maximum token count on hand.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
}