package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.github.splendor_mobile_game.websocket.utils.reflection.Reflection;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReactionManagerTest {

    private ReactionManager reactionManager;

    @Before
    public void setUp() {
        reactionManager = new ReactionManager();
    }

    @Test
    public void testLoadReactions_ValidClasses_ReactionsLoaded() {
        // Given
        List<Class<?>> classesToSearchIn = new ArrayList<>();
        classesToSearchIn.add(TestReaction.class);

        // When
        reactionManager.loadReactions(classesToSearchIn);
        Map<String, Class<? extends Reaction>> reactions = reactionManager.reactions;

        // Then
        assertFalse("Reactions map should not be empty", reactions.isEmpty());
        assertTrue("Reactions map should contain key 'testReaction'", reactions.containsKey("testReaction"));
        assertEquals("TestReaction", reactions.get("testReaction").getSimpleName());
    }

    @Test
    public void testLoadReactions_NullClassesList_ReactionsNotLoaded() {
        // When
        reactionManager.loadReactions(null);
        Map<String, Class<? extends Reaction>> reactions = reactionManager.reactions;

        // Then
        assertTrue("Reactions map should be empty", reactions.isEmpty());
    }

    @Test
    public void testLoadReactions_InvalidClasses_ReactionsNotLoaded() {
        // Given
        List<Class<?>> classesToSearchIn = new ArrayList<>();
        classesToSearchIn.add(InvalidReaction.class);

        // When
        reactionManager.loadReactions(classesToSearchIn);
        Map<String, Class<? extends Reaction>> reactions = reactionManager.reactions;

        // Then
        assertTrue("Reactions map should be empty", reactions.isEmpty());
        // Assert that the error is logged, you can use your own logging mechanism here
        // For simplicity, using a boolean flag to check if error log is called
        assertTrue("Error log should be called", TestLog.errorLogCalled);
    }

    // Test classes used for testing

    static class TestReaction implements Reaction {
        @Override
        public void react(int playerId, UserMessage message, Messenger messenger, Database database) {
            // Do nothing
        }
    }

    static class InvalidReaction {
        // Invalid class, doesn't implement Reaction interface
    }

    static class TestLog {
        static boolean errorLogCalled = false;

        public static void ERROR(String message) {
            errorLogCalled = true;
        }
    }
}
