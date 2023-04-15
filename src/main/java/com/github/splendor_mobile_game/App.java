package com.github.splendor_mobile_game;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.websocket.communication.ConnectionCheckerWithoutDefaultConstructorException;
import com.github.splendor_mobile_game.websocket.communication.WebSocketSplendorServer;
import com.github.splendor_mobile_game.websocket.config.Config;
import com.github.splendor_mobile_game.websocket.config.EnvConfig;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.websocket.handlers.ReactionManager;
import com.github.splendor_mobile_game.websocket.handlers.connection.SimpleConnectionChecker;
import com.github.splendor_mobile_game.websocket.handlers.reactions.CreateRoom;
import com.github.splendor_mobile_game.websocket.handlers.reactions.DebugGetRandomCard;
import com.github.splendor_mobile_game.websocket.handlers.reactions.GetTokens;
import com.github.splendor_mobile_game.websocket.handlers.reactions.JoinRoom;
import com.github.splendor_mobile_game.websocket.handlers.reactions.LeaveRoom;
import com.github.splendor_mobile_game.websocket.handlers.reactions.StartGame;
import com.github.splendor_mobile_game.websocket.utils.Log;

/** This class represents the main application class for the Splendor game WebSocket server. */
public class App {

	/** A list of classes that contain reactions to messages from clients. */
	private static List<Class<?>> classesWithReactions = new ArrayList<>(Arrays.asList(
		CreateRoom.class, JoinRoom.class, DebugGetRandomCard.class, LeaveRoom.class, GetTokens.class, StartGame.class
	));


	/**
     * The main entry point of the server application.
     *
     * @param args An array of command-line arguments passed to the application.
     * @throws InvalidConfigException                     			Thrown when the configuration file (.env) is invalid (ie. field is missing).
     * @throws ConnectionCheckerWithoutDefaultConstructorException 	Thrown when the specified connection handler class does not have a default constructor.
     */
	public static void main(String[] args) throws InvalidConfigException, ConnectionCheckerWithoutDefaultConstructorException {

		// Read the environment configuration
		Config config = new EnvConfig("./.env");

		// Initialize the logger
		Log.setSavingLogsToFile(config.getLogsDir());

		// Define where are reactions to the messages from client and load these reactions
		ReactionManager reactionManager = new ReactionManager();
		reactionManager.loadReactions(App.classesWithReactions);

		// Setup the server
		int port = config.getPort();
		WebSocketSplendorServer server = new WebSocketSplendorServer(
			new InetSocketAddress(port),
			reactionManager.reactions,
			SimpleConnectionChecker.class,
			config.getPingIntervalMs(),
			config.getConnectionCheckIntervalMs(),
			new InMemoryDatabase()
		);

		server.setConnectionLostTimeout(config.getConnectionLostTimeoutSec());

		// Start the server
		Log.INFO("Starting the server on ws://localhost:" + port);
		server.run();
	}
}
