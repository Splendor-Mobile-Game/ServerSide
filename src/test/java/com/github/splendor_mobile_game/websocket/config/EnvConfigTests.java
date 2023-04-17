package com.github.splendor_mobile_game.websocket.config;

import com.github.splendor_mobile_game.websocket.config.exceptions.EnvFileNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvRequiredValueNotFoundException;
import com.github.splendor_mobile_game.websocket.config.exceptions.EnvValueWrongTypeException;
import com.github.splendor_mobile_game.websocket.config.exceptions.InvalidConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EnvConfigTests {
    private final String testEnvConfigsDirectoryPath = "./testEnvConfigs/";

    @Test
    public void createDefaultEnvConfigTest(){

        // 1. Setup data for the test
        int port = 8887;
        int connectionLostTimeoutSec = 120;
        int pingIntervalMs = 1000;
        int connectionCheckIntervalMs = 2000;
        String logsDir = "./logs/";

        // 2. Call the function you are testing.
        try {
            EnvConfig config = new EnvConfig();

            // 3. Check if returned values are equal to expected values.
            assertEquals(port, config.getPort());
            assertEquals(connectionLostTimeoutSec, config.getConnectionLostTimeoutSec());
            assertEquals(pingIntervalMs, config.getPingIntervalMs());
            assertEquals(connectionCheckIntervalMs, config.getConnectionCheckIntervalMs());
            assertEquals(logsDir, config.getLogsDir());

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void envFileNotFoundTest(){

        // 1. Setup data for the test
        String envFilePath = "";

        // 2. Call the function you are testing.
        try{
            new EnvConfig((envFilePath));
            fail("creating new EnvConfig with empty path should not be possible");
        } catch (EnvFileNotFoundException ignored) {

        } catch (InvalidConfigException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void createCustomValidEnvConfigTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "first.env";
        int port = 6789;
        int connectionLostTimeoutSec = 180;
        int pingIntervalMs = 500;
        int connectionCheckIntervalMs = 3000;
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

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void envConfigRequiredValueIsNullTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "second.env";

        // 2. Call the function you are testing.
        try{
            new EnvConfig(filepath);
            fail("null on required value should not be possible");
        } catch (EnvRequiredValueNotFoundException ignored){

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void invalidEnvValueTypeTest(){

        // 1. Setup data for the test
        String filepath = this.testEnvConfigsDirectoryPath + "third.env";

        // 2. Call the function you are testing.
        try{
            new EnvConfig(filepath);
            fail("string PORT value should not be possible");
        } catch (EnvValueWrongTypeException ignored){

        } catch (InvalidConfigException ex){
            fail(ex.getMessage());
        }
    }

}
