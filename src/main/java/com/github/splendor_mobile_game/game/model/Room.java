package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.utils.Log;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Room {

    private boolean lastTurn;
    private short movesPlayed;
    private User currentOrder;
    private final UUID uuid;
    private final String name;
    private final String enterCode;
    private final String password;
    private int playerCount;
    private final ArrayList<User> users = new ArrayList<>();
    private User owner;
    private Game game=null;
    private final Database database;


    public Room(UUID uuid, String name, String password, User owner, Database database) {
        this.uuid     = uuid;
        this.name     = name;
        this.password = password;
        this.database = database;
        this.owner    = owner;

        this.enterCode = generateRoomCode(database);
        Log.DEBUG(this.enterCode);

        playerCount++;
        this.users.add(owner);

        this.movesPlayed = 0;
        this.currentOrder = users.get(0);
        this.lastTurn = false;
    }

    public void startGame() {
        this.game = new Game(database, users);
    }

    public void displayScoreboard() {
        // TODO
        // endGame
    }

    public void endGame() {
        this.game = null;
    }

    public User getCurrentPlayer() {
        return currentOrder;
    }

    public User changeTurn(){
        int index = users.indexOf(currentOrder);
        
        this.movesPlayed++;
        
        if(index == users.size()-1){
            currentOrder = users.get(0);
            return currentOrder;
        } 
        else{
            currentOrder = users.get(index+1);
            return currentOrder;
        }
    }

    public boolean getLastTurn() {
        return this.lastTurn;
    }

    public boolean isPlayersMovesEqual() {
        return this.movesPlayed % this.playerCount == 0;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public User getUserByUuid(UUID uuid) {
        for(User u : users) {
            if(u.getUuid() == uuid) return u;
        }
        return null;
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public boolean userExists(User user) {
        return users.contains(user);
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

    public String getEnterCode() {
        return enterCode;
    }

    public User getOwner() {
        return owner;
    }
    
    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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


    /**
     * Function generating unique room code.
     *
     * @param database -> game database storing all rooms. Needed to compare enterCodes
     * @return enterCode -> code needed to enter someone else's room
     */
    private String generateRoomCode(Database database) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        // Create Stream of ints limited by "targetStringLength" length and filter to contain only alphanumeric characters
        String generatedCode = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        if (!isCodeAvailable(database, generatedCode)) return generateRoomCode(database);

        return generatedCode;
    }


    /**
     * Check if enterCode already exists in other room object
     *
     * @param database -> game database storing all rooms. Needed to compare enterCodes
     * @param enterCode -> code which we want to find in already existing rooms
     * @return boolean -> true if code doesn't exist yet
     */
    private boolean isCodeAvailable(Database database, String enterCode) {
        for (Room room : database.getAllRooms()) {
            if (room.getEnterCode().equals(enterCode)) return false;
        }
        return true;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return uuid.equals(room.uuid) && name.equals(room.name) && enterCode.equals(room.enterCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, enterCode);
    }
}
