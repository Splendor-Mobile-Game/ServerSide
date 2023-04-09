package com.github.splendor_mobile_game.database;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Noble;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;

public interface Database {

    public User getUser(UUID uuid);

    public User getUserByConnectionHashCode(int connectionHashCode);

    public void addUser(User user);

    public Room getRoom(UUID uuid);

    public Room getRoomWithUser(UUID userUuid);

    public Room getRoom(String name);

    public void addRoom(Room room);

    public ArrayList<User> getAllUsers();

    public ArrayList<Room> getAllRooms();

    public void loadNobles();

    public ArrayList<Noble> getAllNobles();

    public void loadCards();

    public ArrayList<Card> getAllCards();

    public ArrayList<Card> getSpecifiedCards(CardTier tier);
}
