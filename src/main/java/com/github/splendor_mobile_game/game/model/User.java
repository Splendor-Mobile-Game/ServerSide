package com.github.splendor_mobile_game.game.model;

import java.util.UUID;

public class User {

    private final String name;

    private UUID uuid;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
