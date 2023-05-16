package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
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

public class BuyReservedMineTests {

    private Database database;

    private String newBaseMessage(String contextUuid) {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "BUY_RESERVED_MINE",
                    "data": {
                        "userDTO":{
                            "uuid": "$userUuid"
                        },
                        "cardDTO":{
                            "uuid": "$cardUuid"
                        }
                    }
                }
                """.replace("$contextUuid", contextUuid);
    }

    private String newBaseErrorMessage(String contextUuid) {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "BUY_RESERVED_MINE_RESPONSE",
                    "result": "FAILURE",
                    "data": {
                        "error": "$error"
                    }
                }
                """.replace("$contextUuid", contextUuid);
    }
    @Test
    public void invalidUserUuidTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "invalid-uuid";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class));
    }

    @Test
    public void invalidCardUuidTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String cardUuid = "invalid-uuid";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class));
    }

    @Test
    public void userNotFoundTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "Couldn't find a user with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userNotInRoomTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User user = new User(UUID.randomUUID(), "PLAYER", 100000);
        this.database.addUser(user);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", user.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(user.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(user.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
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

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        this.database.addUser(owner);
        this.database.addRoom(room);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "Game hasn't started yet.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void invalidTurnTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "It's not your turn.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void cardNotFoundTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "Couldn't find a card with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void cardNotReservedTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        Card card = new Card(CardTier.LEVEL_1, 0, 0,0,0,0,0, TokenType.EMERALD);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);
        this.database.getAllCards().add(card);

        room.startGame();
        room.getGame().getRevealedCards(CardTier.LEVEL_1).remove(card);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "The card is not in the reserved deck.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void validRequestTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        Card card = new Card(CardTier.LEVEL_1, 0, 0,0,0,0,0, TokenType.EMERALD);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);
        this.database.getAllCards().add(card);

        room.startGame();
        owner.reserveCard(card, false);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertFalse(owner.getReservedCards().contains(card));
        assertTrue(owner.getPurchasedCards().contains(card));
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        JsonObject jsonObject = JsonParser.parseString(reply).getAsJsonObject();
        assertEquals(ServerMessageType.BUY_RESERVED_MINE_ANNOUNCEMENT.toString(), jsonObject.get("type").getAsString());
        assertEquals(Result.OK.toString(), jsonObject.get("result").getAsString());
    }

    @Test
    public void userHasPerformedActionTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        Card card = new Card(CardTier.LEVEL_1, 0, 0,0,0,0,0, TokenType.EMERALD);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);
        this.database.getAllCards().add(card);

        room.startGame();
        owner.reserveCard(card, false);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyReservedMine brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertFalse(room.getGame().getRevealedCards(CardTier.LEVEL_1).contains(card));
        assertTrue(owner.hasPerformedAction());

        card = new Card(CardTier.LEVEL_1,0,0,0,0,0,0, TokenType.EMERALD);
        this.database.getAllCards().add(card);
        owner.reserveCard(card, false);

        message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        receivedMessage = new UserMessage(message);
        messenger = new Messenger();
        brm = new BuyReservedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyReservedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "You have already performed an action.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
}