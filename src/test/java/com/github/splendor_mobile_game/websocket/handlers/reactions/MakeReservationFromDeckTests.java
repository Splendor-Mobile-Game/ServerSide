package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MakeReservationFromDeckTests {

    private Database database;
    private final String roomPassword = "PASSWORD";

    private String newBaseMessage() {
        return """
                {
                    "contextId":"02442d1b-2095-4aaa-9db1-0dae99d88e03",
                    "type": "MAKE_RESERVATION_FROM_DECK",
                    "data": {
                        "userUuid": "$userUuid",
                        "cardTier": "$cardTier"
                    }
                }
                """;
    }

    private JsonObject createRoom() {
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
    public void validRequestTest() {
        // 1. Setup data for the test
        this.database = new InMemoryDatabase();
        JsonObject jsonRoom = this.createRoom();
        String roomUuid = jsonRoom.get("uuid").getAsString();

        int clientConnectionHashCode = 714239;
        Messenger messenger = new Messenger();

        Room room = this.database.getRoom(UUID.fromString(roomUuid));

        User player = new User(UUID.randomUUID(), "PLAYER", 1234);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();

        String userUuid = player.getUuid().toString();
        String cardTier = "LEVEL_1";

        String message = this.newBaseMessage()
                .replace("$userUuid", userUuid)
                .replace("$cardTier", cardTier);

        UserMessage receivedMessage = new UserMessage(message);
        MakeReservationFromDeck makeReservationFromDeck = new MakeReservationFromDeck(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);

        // Parse the data given in the message
        receivedMessage.parseDataToClass(MakeReservationFromDeck.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        makeReservationFromDeck.react();

        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);

        // 4. Check that return value and side effects of this call is correct
        // TO DO
    }
}