package com.github.splendor_mobile_game.websocket.config;

import java.io.File;

import com.github.splendor_mobile_game.websocket.config.exceptions.EnvFileNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvRequiredValueNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvValueWrongTypeException;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.websocket.config.exceptions.UnsupportedEnvValueTypeException;
import com.github.splendor_mobile_game.websocket.utils.Log;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig implements Config {
    private int port;
    private int connectionLostTimeoutSec;
    private int pingIntervalMs;
    private int connectionCheckIntervalMs;
    private String logsDir;

    public EnvConfig() throws InvalidConfigException {
        this("./env");
    }

    public EnvConfig(String dotEnvPath) throws InvalidConfigException {
        this.loadFile(dotEnvPath);
    }

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
        this.port = (Integer) this.loadValue(dotenv, "PORT", Integer.class, true);
        this.connectionLostTimeoutSec = (Integer) this.loadValue(dotenv, "CONNECTION_LOST_TIMEOUT_SEC", Integer.class,
                true);
        this.pingIntervalMs = (Integer) this.loadValue(dotenv, "PING_INTERVAL_MS", Integer.class,
                true);
        this.connectionCheckIntervalMs = (Integer) this.loadValue(dotenv, "CONNECTION_CHECK_INTERVAL_MS", Integer.class,
                true);

        this.logsDir = (String) this.loadValue(dotenv, "LOGS_DIR", String.class, true);
    }

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

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public int getConnectionLostTimeoutSec() {
        return this.connectionLostTimeoutSec;
    }

    @Override
    public int getPingIntervalMs() {
        return this.pingIntervalMs;
    }

    @Override
    public int getConnectionCheckIntervalMs() {
        return this.connectionCheckIntervalMs;
    }

    @Override
    public String getLogsDir() {
        return this.logsDir;
    }

}
