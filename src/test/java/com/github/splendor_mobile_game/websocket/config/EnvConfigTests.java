package com.github.splendor_mobile_game.websocket.config;

import com.github.splendor_mobile_game.websocket.config.exceptions.EnvFileNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvRequiredValueNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvValueWrongTypeException;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import com.github.splendor_mobile_game.websocket.utils.LogLevel;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class EnvConfigTests {
    private final String testEnvConfigsDirectoryPath = "./testEnvConfigs/";

    @Test
    public void envFileNotFoundTest(){
        String filepath = this.testEnvConfigsDirectoryPath + "nonexistent.env";
        assertThrows(EnvFileNotFoundException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void createCustomValidEnvConfigTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "first.env";
        int port = 6789;
        int connectionLostTimeoutSec = 180;
        int pingIntervalMs = 500;
        int connectionCheckIntervalMs = 3000;
        EnumSet<LogLevel> consoleLogLevels = EnumSet.allOf(LogLevel.class);
        EnumSet<LogLevel> fileLogLevels = EnumSet.allOf(LogLevel.class);
        String logsDir = "./logs/";

        // 2. Call the function you are testing.
        try{
            EnvConfig config = new EnvConfig(filepath);

            // 3. Check if returned values are equal to expected values.
            assertEquals(port, config.getPort());
            assertEquals(connectionLostTimeoutSec, config.getConnectionLostTimeoutSec());
            assertEquals(pingIntervalMs, config.getPingIntervalMs());
            assertEquals(connectionCheckIntervalMs, config.getConnectionCheckIntervalMs());
            assertEquals(logsDir, config.getLogsDir());
            for (LogLevel ll : consoleLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}
            for (LogLevel ll : fileLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void envConfigRequiredValueIsNullTest(){
        String filepath = this.testEnvConfigsDirectoryPath + "second.env";
        assertThrows(EnvRequiredValueNotFoundException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void invalidEnvValueTypeTest(){
        String filepath = this.testEnvConfigsDirectoryPath + "third.env";
        assertThrows(EnvValueWrongTypeException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void missingEnvValueTest(){
        String filepath = this.testEnvConfigsDirectoryPath + "fourth.env";
        assertThrows(EnvValueWrongTypeException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void additionalValuesInEnvConfigFileTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "fifth.env";
        int port = 8887;
        int connectionLostTimeoutSec = 100;
        int pingIntervalMs = 1000;
        int connectionCheckIntervalMs = 1000;
        EnumSet<LogLevel> consoleLogLevels = EnumSet.allOf(LogLevel.class);
        EnumSet<LogLevel> fileLogLevels = EnumSet.allOf(LogLevel.class);
        String logsDir = "./logs/";

        // 2. Call the function you are testing.
        try{
            EnvConfig config = new EnvConfig(filepath);

            // 3. Check if returned values are equal to expected values.
            assertEquals(port, config.getPort());
            assertEquals(connectionLostTimeoutSec, config.getConnectionLostTimeoutSec());
            assertEquals(pingIntervalMs, config.getPingIntervalMs());
            assertEquals(connectionCheckIntervalMs, config.getConnectionCheckIntervalMs());
            assertEquals(logsDir, config.getLogsDir());
            for (LogLevel ll : consoleLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}
            for (LogLevel ll : fileLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

    // TODO: DuplicateKeyException should be implemented.
    @Test
    public void duplicateKeyInEnvConfigFileTest(){
        String filepath = this.testEnvConfigsDirectoryPath + "sixth.env";
        assertThrows(IllegalStateException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void invalidLogLevelInConfigFileTest() {
        String filepath = this.testEnvConfigsDirectoryPath + "seventh.env";
        assertThrows(IllegalArgumentException.class, () -> new EnvConfig(filepath));
    }

    @Test
    public void createCustomEnvConfigWithoutAllLogLevelsTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "eighth.env";
        int port = 6789;
        int connectionLostTimeoutSec = 180;
        int pingIntervalMs = 500;
        int connectionCheckIntervalMs = 3000;
        EnumSet<LogLevel> consoleLogLevels = EnumSet.of(LogLevel.TRACE, LogLevel.INFO, LogLevel.DEBUG);
        EnumSet<LogLevel> fileLogLevels = EnumSet.of(LogLevel.TRACE, LogLevel.INFO, LogLevel.DEBUG);
        String logsDir = "./logs/";

        // 2. Call the function you are testing.
        try{
            EnvConfig config = new EnvConfig(filepath);

            // 3. Check if returned values are equal to expected values.
            assertEquals(port, config.getPort());
            assertEquals(connectionLostTimeoutSec, config.getConnectionLostTimeoutSec());
            assertEquals(pingIntervalMs, config.getPingIntervalMs());
            assertEquals(connectionCheckIntervalMs, config.getConnectionCheckIntervalMs());
            assertEquals(logsDir, config.getLogsDir());
            for (LogLevel ll : consoleLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}
            for (LogLevel ll : fileLogLevels) { assertTrue(config.getConsoleLogLevels().contains(ll));}

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }
}