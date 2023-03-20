package com.github.splendor_mobile_game.database;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;


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
    public User getUser(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

}
