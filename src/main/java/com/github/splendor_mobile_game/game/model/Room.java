package com.github.splendor_mobile_game.game.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Room {


    private final String name;
    private final String password;

    private User owner;

    private final UUID uuid;

    private int playerCount;
    private final ArrayList<User> users = new ArrayList<>();




    public Room(UUID uuid, String name, String password, User owner) {
        this.uuid     = uuid;
        this.name     = name;
        this.password = password;
        this.owner    = owner;

        this.users.add(owner);
        playerCount++;
    }


    public int getPlayerCount() {
        return playerCount;
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public boolean userExists(User user) {
        return users.contains(user);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     *
     * Add new player to the game if number of currently awaiting players is smaller than 4.
     *
     * @param user -> user who is trying to join the game
     * @return boolean -> true if user successfully joined the game
     */
    public void joinGame(User user) {
        if (users.contains(user)) return;  // Player is already a part of the game.

        users.add(user);
        playerCount++;
    }




    /**
     *
     * Remove a player from the game if he is awaiting.
     *
     * @param user -> user who is trying to leave the game
     * @return boolean -> true if user successfully left the game
     */
    public void leaveGame(User user) {
        if (!users.contains(user)) return;  // Player is not part of the game.
        users.remove(user);
        playerCount--;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return name.equals(room.name) && password.equals(room.password) && uuid.equals(room.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, uuid);
    }
}
