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

public class BuyMineTest {
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

            tokens.put(TokenType.RUBY,cardToBuy.getCost(TokenType.RUBY));
            tokens.put(TokenType.EMERALD,cardToBuy.getCost(TokenType.EMERALD));
            tokens.put(TokenType.SAPPHIRE,cardToBuy.getCost(TokenType.SAPPHIRE));
            tokens.put(TokenType.DIAMOND,cardToBuy.getCost(TokenType.DIAMOND));
            tokens.put(TokenType.ONYX,cardToBuy.getCost(TokenType.ONYX));

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
        BuyMine buyMine = new BuyMine(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);


        // Parse the data given in the message
        receivedMessage.parseDataToClass(BuyMine.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        buyMine.react();
 
        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);

        // 4. Check that return value and side effects of this call is correct
        // TO DO
    }
}
