package com.github.splendor_mobile_game.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Noble;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.utils.Log;


public class InMemoryDatabase implements Database {

    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<Room> allRooms = new ArrayList<>();
    private ArrayList<Card> allCards = new ArrayList<>();
    private ArrayList<Noble> allNobles = new ArrayList<>();

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
    public Room getRoom(String enterCode) {
        for(Room room : allRooms) {
            if (room.getEnterCode().equals(enterCode)) return room;
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

    @Override
    public void loadNobles(){
        String csvFile = "resources/NobleDatabase.csv";
        String line = "";
        String csvSplitBy = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            line = br.readLine();   //skipping first line because there are headlines

            while ((line = br.readLine()) != null) {

                String[] data = line.split(csvSplitBy);

                try {
                    Noble nobleCard = new Noble(
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]),
                        Integer.parseInt(data[0])
                    );
                                    
                    this.allNobles.add(nobleCard);
                } catch (IllegalArgumentException e) {
                    Log.ERROR(e.getMessage());
                }

            }

        } catch (IOException e) {
            Log.ERROR(e.getMessage());
        } catch (Exception e) {
            Log.ERROR(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void loadCards() {
        String csvFile = "resources/CardDatabase.csv";
        String line = "";
        String csvSplitBy = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            line = br.readLine();   //skipping first line because there are headlines

            while ((line = br.readLine()) != null) {

                String[] data = line.split(csvSplitBy);

                try {
                    Card card = new Card(CardTier.valueOf(data[0]),
                                    Integer.parseInt(data[2]),
                                    Integer.parseInt(data[5]),
                                    Integer.parseInt(data[4]),
                                    Integer.parseInt(data[6]),
                                    Integer.parseInt(data[7]),
                                    Integer.parseInt(data[3]),
                                    TokenType.valueOf(data[1]));
                                    
                    this.allCards.add(card);
                } catch (IllegalArgumentException e) {
                    Log.ERROR(e.getMessage());
                }

            }

        } catch (IOException e) {
            Log.ERROR(e.getMessage());
        } catch (Exception e) {
            Log.ERROR(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Noble> getAllNobles(){
        return this.allNobles;
    }

    @Override
    public ArrayList<Card> getAllCards() {
      return this.allCards;
    }

    @Override
    public ArrayList<Card> getSpecifiedCards(CardTier tier) {
        ArrayList<Card> tempCardList = new ArrayList<Card>(allCards);
        tempCardList.removeIf(card -> card.getCardTier() != tier);
        return tempCardList;
    }
}
