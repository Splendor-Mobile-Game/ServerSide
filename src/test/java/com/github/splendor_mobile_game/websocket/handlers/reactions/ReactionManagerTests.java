package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionManager;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // Class 1 Test
    @Test
    public void testClassListWithPrivateClassWithPrivateInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPrivateClassWithPrivateInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 2 Test
    @Test
    public void testClassListWithPrivateClassWithPrivateValidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPrivateClassWithPrivateValidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 3 Test
    @Test
    public void testClassListWithPrivateClassWithPublicInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPrivateClassWithPublicInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 4 Test
    @Test
    public void testClassListWithPrivateClassWithPublicValidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPrivateClassWithPublicValidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 5 Test
    @Test
    public void testClassListWithPublicClassWithPrivateInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPrivateInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 6 Test
    @Test
    public void testClassListWithPublicClassWithPrivateValidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPrivateValidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 7 Test
    @Test
    public void testClassListWithPublicClassWithPublicInvalidConstructor(){
        ReactionManager manager = new ReactionManager();
        List<Class<?>> classList = new ArrayList<>();
        classList.add(TestPublicClassWithPublicInvalidConstructor.class);
        manager.loadReactions(classList);
        assertTrue(manager.reactions.isEmpty());
    }

    // Class 8 Test
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

    // Class 9 Test
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

    private class TestClassWithoutInterface {

    }

    // Class 1
    private class TestPrivateClassWithPrivateInvalidConstructor extends Reaction{
        private TestPrivateClassWithPrivateInvalidConstructor() {
            super(0, null,null, null);
        }

        @Override
        public void react() {

        }
    }

    // Class 2
    private class TestPrivateClassWithPrivateValidConstructor extends Reaction{
        private TestPrivateClassWithPrivateValidConstructor(int chc, UserMessage rm, Messenger m, Database d) {
            super(chc, rm, m, d);
        }

        @Override
        public void react() {

        }
    }

    // Class 3
    private class TestPrivateClassWithPublicInvalidConstructor extends Reaction{
        public TestPrivateClassWithPublicInvalidConstructor() {
            super(0, null,null, null);
        }

        @Override
        public void react() {

        }
    }

    // Class 4
    public class TestPrivateClassWithPublicValidConstructor extends Reaction{
        public TestPrivateClassWithPublicValidConstructor(int chc, UserMessage rm, Messenger m, Database d) {
            super(chc, rm, m, d);
        }

        @Override
        public void react() {

        }
    }

    // Class 5
    public class TestPublicClassWithPrivateInvalidConstructor extends Reaction{
        private TestPublicClassWithPrivateInvalidConstructor() {
            super(0, null,null, null);
        }

        @Override
        public void react() {

        }
    }

    // Class 6
    public class TestPublicClassWithPrivateValidConstructor extends Reaction{
        private TestPublicClassWithPrivateValidConstructor(int chc, UserMessage rm, Messenger m, Database d) {
            super(chc, rm, m, d);
        }

        @Override
        public void react() {

        }
    }

    // Class 7
    public class TestPublicClassWithPublicInvalidConstructor extends Reaction{
        public TestPublicClassWithPublicInvalidConstructor() {
            super(0, null,null, null);
        }

        @Override
        public void react() {

        }
    }

    // Class 8
    public class TestPublicClassWithPublicValidConstructor extends Reaction{
        public TestPublicClassWithPublicValidConstructor(int chc, UserMessage rm, Messenger m, Database d) {
            super(chc, rm, m, d);
        }

        @Override
        public void react() {

        }
    }

    // Class 9
    @ReactionName("test-reaction")
    public class TestValidClassWithAnnotation extends Reaction {
        public TestValidClassWithAnnotation(int chc, UserMessage rm, Messenger m, Database d){
            super(chc, rm, m, d);
        }
        @Override
        public void react() {

        }
    }
}
