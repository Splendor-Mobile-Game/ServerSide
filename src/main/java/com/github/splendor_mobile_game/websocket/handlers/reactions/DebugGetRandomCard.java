package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.websocket.communication.ServerMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;

/* ----> EXAMPLE USER REQUEST <----
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "DEBUG_GET_RANDOM_CARD",
         "data": {
             
         }
     }
     */

@ReactionName("DEBUG_GET_RANDOM_CARD")
public class DebugGetRandomCard extends Reaction {

    public DebugGetRandomCard(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    @Override
    public void react() {
        // Get some card from the database
        // Card card = database.getRandomCard();

        database.loadCards();
        database.getSpecifiedCards(CardTier.LEVEL_1);
        database.getSpecifiedCards(CardTier.LEVEL_2);
        ArrayList<Card> ar1 = database.getSpecifiedCards(CardTier.LEVEL_3);

        for(Card c : ar1) {
            System.out.println(c);
        }

        // Add the card to the data response
        // data.add("card", gson.toJson(card));

        ServerMessage serverMessage = new ServerMessage(userMessage.getContextId(), ServerMessageType.DEBUG_GET_RANDOM_CARD_RESPONSE, Result.OK, null);
        messenger.addMessageToSend(this.connectionHashCode, serverMessage);
    }

}
