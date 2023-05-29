package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.plaf.InsetsUIResource;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class GetTokensTest {
    
    @Test
    public void Test1()
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

        User player = new User(UUID.randomUUID(), "Rodrigo", 1234);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();
        
        //Game game = room.getGame();     

        Map<TokenType,Integer> tokensToTake = new HashMap<TokenType,Integer>();
        Map<TokenType,Integer> tokensToReturn = new HashMap<TokenType,Integer>();

        insert(tokensToTake,2,0,0,0,0);
        insert(tokensToReturn,0,0,0,0,0);

        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,0,0,0,0,2);
        sendRequest(player,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,0,2,0,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,0,0,0,2,0);
        sendRequest(player,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,1,1,1,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,0,0,1,1,1);
        sendRequest(player,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,1,1,1,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();
        insert(tokensToTake,0,0,1,1,1);
        sendRequest(player,messenger,database,tokensToTake,tokensToReturn);
        room.changeTurn();

        assertThat(owner.getTokenCount()).isEqualTo(10);
        assertThat(player.getTokenCount()).isEqualTo(10);
    }

    @Test
    public void Test2(){
        Log.DEBUG("TEST 2");

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
        
        //Game game = room.getGame();     

        Map<TokenType,Integer> tokensToTake = new HashMap<TokenType,Integer>();
        Map<TokenType,Integer> tokensToReturn = new HashMap<TokenType,Integer>();

        insert(tokensToReturn,0,0,0,0,0);

        insert(tokensToTake,2,0,0,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        insert(tokensToTake,0,2,0,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        insert(tokensToTake,1,1,1,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
        insert(tokensToTake,1,1,1,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);

        insert(tokensToTake,0,0,0,0,2);
        insert(tokensToReturn,2,0,0,0,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);

    }
    @Test
    public void Test3(){
        Log.DEBUG("TEST 3");

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

        Game game = room.getGame();

        try{
            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            Map<TokenType, Integer> tokens = (HashMap<TokenType,Integer>)privateTokens.get(owner);

            tokens.put(TokenType.RUBY,1);
            tokens.put(TokenType.EMERALD,3);
            tokens.put(TokenType.SAPPHIRE,1);
            tokens.put(TokenType.DIAMOND,2);
            tokens.put(TokenType.ONYX,0);
            tokens.put(TokenType.GOLD_JOKER,3);

            Field privateTokensGame = Game.class.getDeclaredField("tokensOnTable");
            privateTokensGame.setAccessible(true);
            Map<TokenType, Integer> tokensOnTable = (HashMap<TokenType,Integer>)privateTokensGame.get(game);

            tokensOnTable.put(TokenType.RUBY,3);
            tokensOnTable.put(TokenType.EMERALD,1);
            tokensOnTable.put(TokenType.SAPPHIRE,3);
            tokensOnTable.put(TokenType.DIAMOND,2);
            tokensOnTable.put(TokenType.ONYX,4);
            tokensOnTable.put(TokenType.GOLD_JOKER,2);

        }catch (Exception e){
            e.printStackTrace();
        }
        Map<TokenType,Integer> tokensToTake = new HashMap<TokenType,Integer>();
        Map<TokenType,Integer> tokensToReturn = new HashMap<TokenType,Integer>();

        insert(tokensToTake,0,0,0,0,2);
        insert(tokensToReturn,0,0,0,2,0);
        sendRequest(owner,messenger,database,tokensToTake,tokensToReturn);
    }

    private void sendRequest(User user,Messenger messenger,Database database,Map<TokenType,Integer> tokensToTake,Map<TokenType,Integer> tokensToRetrun)
    {
        String contextId = UUID.randomUUID().toString();
        String messageType = "GET_TOKENS";
        String userUuid = user.getUuid().toString();   

        String message = """
            {
                "contextId": "$contextId",
                "type": "$type",
                "data": {
                    "userUuid": "$userId",
                    "tokensTakenDTO": {
                        "ruby": $RUBY_TAKE,
                        "sapphire": $SAPPHIRE_TAKE,
                        "emerald": $EMERALD_TAKE,
                        "diamond": $DIAMOND_TAKE,
                        "onyx": $ONYX_TAKE
                    },
                    "tokensReturnedDTO": {
                        "ruby": $RUBY_RETURN,
                        "sapphire": $SAPPHIRE_RETURN,
                        "emerald": $EMERALD_RETURN,
                        "diamond": $DIAMOND_RETURN,
                        "onyx": $ONYX_RETURN
                    }
                }         
            }
        """.replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid);

        for(TokenType type:EnumSet.allOf(TokenType.class)){
            if(type!=TokenType.GOLD_JOKER){
                message = message.replace("$"+type.toString()+"_TAKE",tokensToTake.get(type).toString());
                message = message.replace("$"+type.toString()+"_RETURN",tokensToRetrun.get(type).toString());
            }
        }

        UserMessage receivedMessage = new UserMessage(message);
        GetTokens getTokens = new GetTokens(user.getConnectionHashCode(), receivedMessage, messenger, database);

        Log.DEBUG(message);

        // Parse the data given in the message
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        getTokens.react();

        user.setPerformedAction(false);
    }
    private void insert(Map<TokenType,Integer> arr,int ruby,int sapphire,int emerald,int diamond,int onyx)
    {
        arr.put(TokenType.RUBY, ruby);
        arr.put(TokenType.SAPPHIRE, sapphire);
        arr.put(TokenType.EMERALD, emerald);
        arr.put(TokenType.DIAMOND, diamond);
        arr.put(TokenType.ONYX, onyx);
    }
}
