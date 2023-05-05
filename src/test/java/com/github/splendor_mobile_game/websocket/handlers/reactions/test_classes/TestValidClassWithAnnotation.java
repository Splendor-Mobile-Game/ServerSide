package com.github.splendor_mobile_game.websocket.handlers.reactions.test_classes;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

@ReactionName("test-reaction")
public class TestValidClassWithAnnotation extends Reaction {
    public TestValidClassWithAnnotation(int chc, UserMessage rm, Messenger m, Database d){
        super(chc, rm, m, d);
    }
    @Override
    public void react() {

    }
}
