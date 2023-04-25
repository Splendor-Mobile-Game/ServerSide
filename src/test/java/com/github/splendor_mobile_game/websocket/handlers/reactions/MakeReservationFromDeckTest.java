package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;

public class MakeReservationFromDeckTest {
    
    @Test
    public void test(){
        // 1. Setup data for the test

        int clientConnectionHashCode = 714239;
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "Janek", clientConnectionHashCode);
        database.addUser(owner);
        Room room = new Room(UUID.randomUUID(), "Pokoj", "123", owner, database);
        database.addRoom(room);

        User player = new User(UUID.randomUUID(), "Rodrigo", 1234);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();

        String contextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "MAKE_RESERVATION_FROM_DECK";
        String userUuid = owner.getUuid().toString();
        String cardTier = "LEVEL_1";

        String message = """
        {
            "contextId": "$contextId",
            "type": "$type",
            "data": {
                "userUuid": "$userId",
                "cardTier": "$cardTier"
            }
        }""".replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
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
