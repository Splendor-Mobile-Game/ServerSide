package com.github.splendor_mobile_game.game.model;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.CardTier;

public class GameTest {

    //Very bad test NOT TO COPY
    @Test
    public void Test(){

        Database database = new InMemoryDatabase();
        User owner =new User(UUID.randomUUID(), "name", 0);
        Room room = new Room(UUID.randomUUID(), "name", "password",owner,database);
        room.joinGame(new User(UUID.randomUUID(), "asdf", 0));
        room.joinGame(new User(UUID.randomUUID(), "aaasdf", 0));

        Game game=new Game(database);
        game.startGame(room);
        
    }

    public int whatSize(CardTier tier){
        switch(tier){
            case LEVEL_1:
                return 40;
            case LEVEL_2:
                return 30;
            case LEVEL_3:
                return 20;
            default:
                return 0;
        }
    }
}
