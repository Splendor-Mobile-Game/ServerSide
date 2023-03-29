package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import com.github.splendor_mobile_game.websocket.response.ResponseType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

    public DebugGetRandomCard(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
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

        Gson gson = new Gson();

        JsonObject data = new JsonObject();

        // Add the card to the data response
        // data.add("card", gson.toJson(card));

        JsonObject response = new JsonObject();
        response.addProperty("messageContextId", receivedMessage.getMessageContextId());
        response.addProperty("type", ResponseType.DEBUG_GET_RANDOM_CARD_RESPONSE.toString());
        response.addProperty("result", Result.OK.toString());
        response.add("data", data);

        messenger.addMessageToSend(this.connectionHashCode, gson.toJson(response));


    }

}
