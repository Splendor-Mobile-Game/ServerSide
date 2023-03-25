package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;


public class GetTokens extends Reaction {

    public GetTokens(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
    }

    @Override
    public void react() {
        // TODO Auto-generated method stub
    }
    
}
