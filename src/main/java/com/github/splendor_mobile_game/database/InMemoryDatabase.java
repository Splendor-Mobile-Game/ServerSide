package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;

import java.util.ArrayList;
import java.util.UUID;

public class InMemoryDatabase implements Database {

    public ArrayList<User> allUsers = new ArrayList<>();
    public ArrayList<Room> allRooms = new ArrayList<>();


    public InMemoryDatabase() {

    }



    @Override
    public User getUser(UUID uuid) {
        for(User u : allUsers) {
            if (u.getUuid().equals(uuid)) return u;
        }
        return null;
    }

    @Override
    public void addUser(User user) {
        this.allUsers.add(user);
    }

    @Override
    public Room getRoom(UUID uuid) {
        for(Room r : allRooms) {
            if (r.getUuid().equals(uuid)) return r;
        }
        return null;
    }

    @Override
    public void addRoom(Room room) {
        this.allRooms.add(room);
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    @Override
    public ArrayList<Room> getAllRooms() {
        return allRooms;
    }
}
