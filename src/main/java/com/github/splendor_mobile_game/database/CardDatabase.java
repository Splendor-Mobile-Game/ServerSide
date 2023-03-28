package com.github.splendor_mobile_game.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;

public class CardDatabase {

   private static ArrayList<Card> allCards = new ArrayList<Card>();

   public static void loadCards() {
      String csvFile = "CardDatabase.csv";
      String line = "";
      String csvSplitBy = ";";

      try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

          line = br.readLine();   //skipping first line because there are headlines

          while ((line = br.readLine()) != null) {

              String[] data = line.split(csvSplitBy);

              Card card = new Card(CardTier.valueOf(data[0]), Integer.parseInt(data[2]), Integer.parseInt(data[5]), Integer.parseInt(data[4]), Integer.parseInt(data[6]), Integer.parseInt(data[7]), Integer.parseInt(data[3]), TokenType.valueOf(data[1]));

              CardDatabase.allCards.add(card);

          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public static ArrayList<Card> getCards() {
      return allCards;
  }

  public static ArrayList<Card> getSpecifiedCards(CardTier tier) {
      ArrayList<Card> tempCardList = new ArrayList<Card>(allCards);
      tempCardList.removeIf(card -> card.getCardTier() != tier);
      return tempCardList;
  }
}
