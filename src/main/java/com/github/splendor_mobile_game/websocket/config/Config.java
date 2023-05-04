package com.github.splendor_mobile_game.websocket.config;

import java.util.EnumSet;

import com.github.splendor_mobile_game.websocket.utils.LogLevel;

/** The Config interface provides methods to retrieve various configuration parameters. */
public interface Config {
    
    /**
     * Returns the port number to use for the network connection.
     * @return The port number.
     */
    public int getPort();

    /**
     * Returns the maximum number of seconds to wait for a response from the server
     * before assuming the connection has been lost.
     * @return The maximum number of seconds.
     */
    public int getConnectionLostTimeoutSec();

    /**
     * Returns the interval in milliseconds at which to send ping messages
     * to the client to check the status of the connection.
     * @return The ping interval in milliseconds.
     */
    public int getPingIntervalMs();

    /**
     * Returns the interval in milliseconds at which to check the status of the connection
     * to the client and trigger any necessary actions if the connection has been lost.
     * @return The connection check interval in milliseconds.
     */
    public int getConnectionCheckIntervalMs();

    /**
     * Returns the directory where log files should be stored.
     * @return The log directory as a String.
     */
    public String getLogsDir();
    
    /**
     * Returns the log levels from console.
     * @return The log levels as a EnumSet.
     */
    public EnumSet<LogLevel> getConsoleLogLevels();

    /**
     * Returns the log levels from file.
     * @return The log levels as a EnumSet.
     */
    public EnumSet<LogLevel> getFileLogLevels();
}
