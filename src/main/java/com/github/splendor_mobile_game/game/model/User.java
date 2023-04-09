package com.github.splendor_mobile_game.game.model;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final String name;

    private UUID uuid;

    private int connectionHasCode;

    private boolean hasPerformedAction;

    public User(UUID uuid, String name, int connectionHasCode) {
        this.uuid = uuid;
        this.name = name;
        this.connectionHasCode = connectionHasCode;
        this.hasPerformedAction = false;
    }


    public int getConnectionHashCode() {
        return connectionHasCode;
    }

    public void setConnectionHasCode(int connectionHasCode) {
        this.connectionHasCode = connectionHasCode;
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

    public boolean hasPerformedAction() {
        return hasPerformedAction;
    }

    public void setPerformedAction(boolean hasPerformedAction) {
        this.hasPerformedAction = hasPerformedAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && uuid.equals(user.uuid) && connectionHasCode == user.getConnectionHashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid, connectionHasCode);
    }
}
