package com.github.splendor_mobile_game.websocket.config;

public interface Config {
    public int getPort();

    public int getConnectionLostTimeoutSec();

    public int getPingIntervalMs();

    public int getConnectionCheckIntervalMs();

    public String getLogsDir();
}
