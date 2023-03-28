package com.github.splendor_mobile_game.websocket.config;

import java.io.File;

import com.github.splendor_mobile_game.websocket.config.exceptions.EnvFileNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvRequiredValueNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvValueWrongTypeException;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.websocket.config.exceptions.UnsupportedEnvValueTypeException;
import com.github.splendor_mobile_game.websocket.utils.Log;

import io.github.cdimascio.dotenv.Dotenv;

/** An implementation of the Config interface that reads configuration values from environment variables. */
public class EnvConfig implements Config {
    private int port;
    private int connectionLostTimeoutSec;
    private int pingIntervalMs;
    private int connectionCheckIntervalMs;
    private String logsDir;

    /**
     * Creates a new EnvConfig instance with the default path for the environment file.
     * @throws InvalidConfigException if there is an error loading the environment file or parsing its contents.
     */
    public EnvConfig() throws InvalidConfigException {
        this("./env");
    }

    /**
     * Creates a new EnvConfig instance that loads environment variables from the specified file path.
     * @param dotEnvPath the file path to load environment variables from.
     * @throws InvalidConfigException if there is an error loading the environment file or parsing its contents.
     */
    public EnvConfig(String dotEnvPath) throws InvalidConfigException {
        this.loadFile(dotEnvPath);
    }

    /**
     * Loads environment variables from the specified file path.
     * @param path the file path to load environment variables from.
     * @throws InvalidConfigException if there is an error loading the environment file or parsing its contents.
     */
    private void loadFile(String path) throws InvalidConfigException {

        // Check if file exists
        File envFile = new File(path);
        if (!envFile.exists()) {
            String message = "Env file does not exist: " + path;
            Log.ERROR(message);
            throw new EnvFileNotFoundException(message);
        }

        // Load the file
        Dotenv dotenv = Dotenv.configure().filename(path).load();

        // Load all the values, one by one
        // TODO: Check, if value is not required, then it's null. Then casting result in error?
        this.port = (Integer) this.loadValue(dotenv, "PORT", Integer.class, true);
        this.connectionLostTimeoutSec = (Integer) this.loadValue(dotenv, "CONNECTION_LOST_TIMEOUT_SEC", Integer.class, true);
        this.pingIntervalMs = (Integer) this.loadValue(dotenv, "PING_INTERVAL_MS", Integer.class, true);
        this.connectionCheckIntervalMs = (Integer) this.loadValue(dotenv, "CONNECTION_CHECK_INTERVAL_MS", Integer.class, true);
        this.logsDir = (String) this.loadValue(dotenv, "LOGS_DIR", String.class, true);
    }

    /**
     * Loads a single environment variable from the specified Dotenv instance.
     * @param dotenv the Dotenv instance to load the environment variable from.
     * @param key the key of the environment variable to load.
     * @param valueType the expected type of the environment variable.
     * @param required whether the environment variable is required to be present.
     * @return the parsed value of the environment variable, or null if it was not required and not found.
     * @throws InvalidConfigException if there is an error loading or parsing the environment variable.
     */
    private Object loadValue(Dotenv dotenv, String key, Class<?> valueType, boolean required)
            throws InvalidConfigException {

        String value = dotenv.get(key);

        // If required and not found, throw an exception
        if (required == true && value == null) {
            String message = "Required value not found in env file: " + key;
            Log.ERROR(message);
            throw new EnvRequiredValueNotFoundException(message);
        }

        if (required == false && value == null) {
            return null;
        }

        // Parse to specified type
        Object parsedValue = null;
        try {
            if (valueType == String.class) {
                parsedValue = value.toString();
            } else if (valueType == Integer.class) {
                parsedValue = Integer.parseInt(value);
            } else if (valueType == Boolean.class) {
                parsedValue = Boolean.parseBoolean(value);
            } else if (valueType == Float.class) {
                parsedValue = Float.parseFloat(value);
            } else if (valueType == Double.class) {
                parsedValue = Double.parseDouble(value);
            } else {
                String message = "Env file doesn't support type you provided in code: " + valueType.getName();
                Log.ERROR(message);
                throw new UnsupportedEnvValueTypeException(message);
            }
        } catch (NumberFormatException e) {
            String message = "Value for port should be the type of " + valueType.getName();
            Log.ERROR(message);
            throw new EnvValueWrongTypeException(message, e);
        }

        return parsedValue;
    }

    /**
     * Returns the port number to use for the network connection.
     * @return The port number.
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * Returns the maximum number of seconds to wait for a response from the server
     * before assuming the connection has been lost.
     * @return The maximum number of seconds.
     */
    @Override
    public int getConnectionLostTimeoutSec() {
        return this.connectionLostTimeoutSec;
    }

    /**
     * Returns the interval in milliseconds at which to send ping messages
     * to the client to check the status of the connection.
     * @return The ping interval in milliseconds.
     */
    @Override
    public int getPingIntervalMs() {
        return this.pingIntervalMs;
    }

    /**
     * Returns the interval in milliseconds at which to check the status of the connection
     * to the client and trigger any necessary actions if the connection has been lost.
     * @return The connection check interval in milliseconds.
     */
    @Override
    public int getConnectionCheckIntervalMs() {
        return this.connectionCheckIntervalMs;
    }

    /**
     * Returns the directory where log files should be stored.
     * @return The log directory as a String.
     */
    @Override
    public String getLogsDir() {
        return this.logsDir;
    }

}
