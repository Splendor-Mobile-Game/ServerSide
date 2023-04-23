package com.github.splendor_mobile_game.websocket.handlers.reactions;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Deck;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BuyRevealedMineTest {
    @Test
    public void test1(){
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
        
        Game game = room.getGame();
        Card cardToBuy=null;
        try{
            Field privateField = Game.class.getDeclaredField("revealedCards");
            privateField.setAccessible(true);
            Map<CardTier,Deck> revealedDecks = (HashMap<CardTier,Deck>)privateField.get(game);
            cardToBuy = revealedDecks.get(CardTier.LEVEL_1).get(0);

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            Map<TokenType, Integer> tokens = (HashMap<TokenType,Integer>)privateTokens.get(owner);

            tokens.put(TokenType.RUBY,cardToBuy.getCost(TokenType.RUBY)+2);
            tokens.put(TokenType.EMERALD,cardToBuy.getCost(TokenType.EMERALD)+1);
            tokens.put(TokenType.SAPPHIRE,cardToBuy.getCost(TokenType.SAPPHIRE));
            tokens.put(TokenType.DIAMOND,cardToBuy.getCost(TokenType.DIAMOND));
            tokens.put(TokenType.ONYX,cardToBuy.getCost(TokenType.ONYX)+1);

        }catch (Exception e){
            e.printStackTrace();
        }
        

        String contextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "BUY_MINE";
        String userUuid = owner.getUuid().toString();
        String cardUuid = cardToBuy.getUuid().toString();

        String message = """
        {
            "contextId": "$contextId",
            "type": "$type",
            "data": {
                "userDTO": {
                    "uuid": "$userId"
                },
                "cardDTO": {
                    "uuid": "$cardId"
                }
            }
        }""".replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
            .replace("$cardId", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        BuyRevealedMine buyRevealedMine = new BuyRevealedMine(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);


        // Parse the data given in the message
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        buyRevealedMine.react();
 
        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);

        // 4. Check that return value and side effects of this call is correct
        // TO DO
    }

    @Test
    public void test2(){
        // 1. Setup data for the test
        Log.DEBUG("TEST 2");
        
        int clientConnectionHashCode = 7164239;
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "Janeek", clientConnectionHashCode);
        database.addUser(owner);
        Room room = new Room(UUID.randomUUID(), "Pokojj", "1423", owner, database);
        database.addRoom(room);

        User player = new User(UUID.randomUUID(), "Rodrrigo", 12634);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();
        
        Game game = room.getGame();
        Card cardToBuy=null;
        try{
            Field privateFieldDecks = Game.class.getDeclaredField("decks");
            privateFieldDecks.setAccessible(true);
            Map<CardTier,Deck> decks = (HashMap<CardTier,Deck>)privateFieldDecks.get(game);
          
            decks.get(CardTier.LEVEL_1).removeAll(decks.get(CardTier.LEVEL_1));
            
            Field privateFieldRevealedCards = Game.class.getDeclaredField("revealedCards");
            privateFieldRevealedCards.setAccessible(true);
            Map<CardTier,Deck> revealedCards = (HashMap<CardTier,Deck>)privateFieldRevealedCards.get(game);

            for(int i=0; i<3;i++){
                revealedCards.get(CardTier.LEVEL_1).remove(0);
            }

            cardToBuy = revealedCards.get(CardTier.LEVEL_1).get(0);

            Field privateFieldTokens = User.class.getDeclaredField("tokens");
            privateFieldTokens.setAccessible(true);
            Map<TokenType, Integer> tokens = (HashMap<TokenType,Integer>)privateFieldTokens.get(owner);

            tokens.put(TokenType.RUBY,cardToBuy.getCost(TokenType.RUBY)+2);
            tokens.put(TokenType.EMERALD,cardToBuy.getCost(TokenType.EMERALD)+1);
            tokens.put(TokenType.SAPPHIRE,cardToBuy.getCost(TokenType.SAPPHIRE));
            tokens.put(TokenType.DIAMOND,cardToBuy.getCost(TokenType.DIAMOND));
            tokens.put(TokenType.ONYX,cardToBuy.getCost(TokenType.ONYX)+1);

        }catch (Exception e){
            e.printStackTrace();
        }

        String contextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "BUY_MINE";
        String userUuid = owner.getUuid().toString();
        String cardUuid = cardToBuy.getUuid().toString();

        String message = """
        {
            "contextId": "$contextId",
            "type": "$type",
            "data": {
                "userDTO": {
                    "uuid": "$userId"
                },
                "cardDTO": {
                    "uuid": "$cardId"
                }
            }
        }""".replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
            .replace("$cardId", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        BuyRevealedMine buyRevealedMine = new BuyRevealedMine(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);

        // Parse the data given in the message
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        buyRevealedMine.react();
 
        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);
    }
}
