package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.database.InMemoryDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class Room {

    // <- >


    private final String name;
    private final String password;

    private User owner;

    private final UUID uuid;

    private int playerCount;
    private final ArrayList<User> players = new ArrayList<>();




    public Room(UUID uuid, String name, String password, User owner) {
        this.uuid     = uuid;
        this.name     = name;
        this.password = password;
        this.owner    = owner;

        this.players.add(owner);
        playerCount++;

        InMemoryDatabase.allRooms.add(this);
    }


    public int getPlayerCount() {
        return playerCount;
    }

    public ArrayList<User> getAllPlayers() {
        return players;
    }

    public boolean playerExists(User user) {
        return players.contains(user);
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
    public boolean joinGame(User user) {
        if (playerCount >= 4) return false; // Maximum number of players reached

        players.add(user);
        playerCount++;

        // allPlayers.SendAcitvity(AcitvityType.NewPlayerJoinedInfo)

        return true;
    }




    /**
     *
     * Remove a player from the game if he is awaiting.
     *
     * @param user -> user who is trying to leave the game
     * @return boolean -> true if user successfully left the game
     */
    public boolean leaveGame(User user) {
        if (playerCount < 0) return false;  // There is no one awaiting
        if (!players.contains(user)) return false;  // Player is not part of the game.

        players.remove(user);
        playerCount--;
        return true;
    }

}
