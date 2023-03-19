package com.github.splendor_mobile_game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.splendor_mobile_game.config.Config;
import com.github.splendor_mobile_game.config.EnvConfig;
import com.github.splendor_mobile_game.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.handlers.ReactionManager;
import com.github.splendor_mobile_game.handlers.connection.ConnectionHandlerImpl;
import com.github.splendor_mobile_game.handlers.reactions.CreateServer;
import com.github.splendor_mobile_game.utils.Log;
import com.github.splendor_mobile_game.websocket.ConnectionHandlerWithoutDefaultConstructorException;
import com.github.splendor_mobile_game.websocket.WebSocketSplendorServer;

public class App {

    private static List<Class<?>> classesWithReactions = new ArrayList<>(Arrays.asList(
            CreateServer.class));

    public static void main(String[] args)
            throws InvalidConfigException, IOException, ConnectionHandlerWithoutDefaultConstructorException {

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
                config.getConnectionCheckIntervalMs());

        server.setConnectionLostTimeout(-1);
        // server.setConnectionLostTimeout(config.getConnectionLostTimeoutSec());

        // Start the server
        Log.INFO("Starting the server on ws://localhost:" + port);
        server.run();
    }
}
