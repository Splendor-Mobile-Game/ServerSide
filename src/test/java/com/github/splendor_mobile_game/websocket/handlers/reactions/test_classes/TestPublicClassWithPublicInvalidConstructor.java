package com.github.splendor_mobile_game.websocket.handlers.reactions.test_classes;

import com.github.splendor_mobile_game.websocket.handlers.Reaction;

public class TestPublicClassWithPublicInvalidConstructor extends Reaction {
    public TestPublicClassWithPublicInvalidConstructor() {
        super(0, null,null, null);
    }

    @Override
    public void react() {

    }
}