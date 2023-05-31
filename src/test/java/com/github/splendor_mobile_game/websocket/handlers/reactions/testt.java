package com.github.splendor_mobile_game.websocket.handlers.reactions;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;

public class testt{
    @Test
    void test()
    {
        Log.DEBUG("TEST 1");

        // 1. Setup data for the test
        
        int clientConnectionHashCode = 714239;
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "Janek", clientConnectionHashCode);
        database.addUser(owner);
        Room room = new Room(UUID.randomUUID(), "Pokoj", "123", owner, database);
        database.addRoom(room);

        User player1 = new User(UUID.randomUUID(), "Rodrigo", 1234);
        database.addUser(player1);
        room.joinGame(player1);
        User player2 = new User(UUID.randomUUID(), "player2", 12345);
        database.addUser(player2);
        room.joinGame(player2);
        User player3 = new User(UUID.randomUUID(), "player3", 12347);
        database.addUser(player3);
        room.joinGame(player3);

        room.startGame();
        room.changeTurn();
        room.changeTurn();
        room.leaveGame(player2);
        room.changeTurn();
        //player3
        
        //Game game = room.getGame()
    }
}