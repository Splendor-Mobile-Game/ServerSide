package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.splendor_mobile_game.websocket.handlers.reactions.test_classes.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("InnerClassMayBeStatic")
public class ReactionManagerTests {

    @Test
    public void testNullClassList(){
        ReactionManager manager = new ReactionManager();
        manager.loadReactions(null);
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testEmptyClassList(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        manager.loadReactions((classList));
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testClassListWithClassWithoutInterface(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestClassWithoutInterface.class);
        manager.loadReactions((classList));
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testClassListWithPublicClassWithPrivateInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPrivateInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testClassListWithPublicClassWithPrivateValidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPrivateValidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testClassListWithPublicClassWithPublicInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPublicInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    @Test
    public void testClassListWithPublicClassWithPublicValidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPublicValidConstructor.class);
        manager.loadReactions(classList);
        Map<String, Class<? extends Reaction>> reactions = manager.reactions;
        assertFalse(reactions.isEmpty());
        assertEquals(1, reactions.size());
        assertTrue(reactions.containsKey(TestPublicClassWithPublicValidConstructor.class.getName().substring(TestPublicClassWithPublicValidConstructor.class.getName().lastIndexOf(".") + 1)));
    }

    @Test
    public void testClassListWithValidClassWithAnnotation(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestValidClassWithAnnotation.class);
        manager.loadReactions(classList);
        Map<String, Class<? extends Reaction>> reactions = manager.reactions;
        assertFalse(reactions.isEmpty());
        assertEquals(1, reactions.size());
        assertTrue(reactions.containsKey("test-reaction"));
    }
}