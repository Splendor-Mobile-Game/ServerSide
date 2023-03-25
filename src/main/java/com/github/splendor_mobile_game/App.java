package com.github.splendor_mobile_game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.splendor_mobile_game.websocket.config.Config;
import com.github.splendor_mobile_game.websocket.config.EnvConfig;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.websocket.handlers.ReactionManager;
import com.github.splendor_mobile_game.websocket.handlers.connection.ConnectionHandlerImpl;
import com.github.splendor_mobile_game.websocket.handlers.reactions.CreateRoom;
import com.github.splendor_mobile_game.websocket.handlers.reactions.CreateServer;
import com.github.splendor_mobile_game.websocket.handlers.reactions.JoinRoom;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.websocket.communication.ConnectionHandlerWithoutDefaultConstructorException;
import com.github.splendor_mobile_game.websocket.communication.WebSocketSplendorServer;

public class App {

        private static List<Class<?>> classesWithReactions = new ArrayList<>(Arrays.asList(
                        CreateServer.class, CreateRoom.class, JoinRoom.class));

        // Komenatrz 2
        public static void main(String[] args)
                        throws InvalidConfigException, IOException,
                        ConnectionHandlerWithoutDefaultConstructorException {

                // Read the env config
                Config config = new EnvConfig("./.env");

                // Initialize logger
                Log.setSavingLogsToFile(config.getLogsDir());

                // Define where are reactions to the messages from client and load these reactions
                ReactionManager reactionManager = new ReactionManager();
                reactionManager.loadReactions(App.classesWithReactions);

                // Setup the server
                int port = config.getPort();
                WebSocketSplendorServer server = new WebSocketSplendorServer(
                                new InetSocketAddress(port),
                                reactionManager.reactions,
                                ConnectionHandlerImpl.class,
                                config.getPingIntervalMs(),
                                config.getConnectionCheckIntervalMs(),
                                new InMemoryDatabase());

                server.setConnectionLostTimeout(-1);
                // server.setConnectionLostTimeout(config.getConnectionLostTimeoutSec());

                // Start the server
                Log.INFO("Starting the server on ws://localhost:" + port);
                server.run();
        }
}
