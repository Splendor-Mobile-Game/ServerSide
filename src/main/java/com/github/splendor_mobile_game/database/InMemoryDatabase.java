package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;

import java.util.ArrayList;
import java.util.UUID;

public class InMemoryDatabase implements Database {

    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<Room> allRooms = new ArrayList<>();


    public InMemoryDatabase() {

    }



    @Override
    public User getUser(UUID uuid) {
        for(User user : allUsers) {
            if (user.getUuid().equals(uuid)) return user;
        }
        return null;
    }

    @Override
    public void addUser(User user) {
        this.allUsers.add(user);
    }

    @Override
    public Room getRoom(UUID uuid) {
        for(Room room : allRooms) {
            if (room.getUuid().equals(uuid)) return room;
        }
        return null;
    }

    @Override
    public Room getRoom(String name) {
        for(Room room : allRooms) {
            if (room.getName().equals(name)) return room;
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
