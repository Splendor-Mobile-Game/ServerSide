package com.github.splendor_mobile_game.database;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Noble;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserAlreadyInRoomException;


// Probably we won't implement any of real database any time soon
// Candidate to delete for now
public class SqliteDatabase implements Database {

    @Override
    public void addRoom(Room room) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addUser(User user) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ArrayList<Room> getAllRooms() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Room getRoom(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Room getRoom(String name) {
        return null;
    }

    @Override
    public User getUser(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Card getCard(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void loadNobles() {
        // TODO Auto-generated method stub
    }

    @Override
    public void loadCards() {
        // TODO Auto-generated method stub
    }

    @Override
    public ArrayList<Noble> getAllNobles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<Card> getAllCards() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<Card> getSpecifiedCards(CardTier tier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUserByConnectionHashCode(int connectionHashCode) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByConnectionHashCode'");
    }

    @Override
    public Room getRoomWithUser(UUID userUuid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRoomWithUser'");
    }

    public void isUserInRoom(UUID uuid) throws UserAlreadyInRoomException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRoomWithUser'");
    }


}
