package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;

import java.util.ArrayList;
import java.util.UUID;

public class InMemoryDatabase implements Database {

    public static ArrayList<User> allUsers = new ArrayList<>();
    public static ArrayList<Room> allRooms = new ArrayList<>();


    public InMemoryDatabase() {

    }




    public User getUser(UUID uuid) {
        for(User u : allUsers) {
            if (u.getUuid().equals(uuid)) return u;
        }
        return null;
    }


    public Room getRoom(UUID uuid) {
        for(Room r : allRooms) {
            if (r.getUuid().equals(uuid)) return r;
        }
        return null;
    }



    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public ArrayList<Room> getAllRooms() {
        return allRooms;
    }
}
