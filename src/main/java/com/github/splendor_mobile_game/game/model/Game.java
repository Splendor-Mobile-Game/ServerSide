package com.github.splendor_mobile_game.game.model;

import java.util.ArrayList;
import java.util.Random;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;

public class Game {

    private TokenList emeraldTokens;
    private TokenList sapphireTokens;
    private TokenList rubyTokens;
    private TokenList diamondTokens;
    private TokenList onyxTokens;
    private TokenList goldTokens;

    //There are always 4 cards revealed to players for each Tier
    private ArrayList<Card> revealedCardTier1List = new ArrayList<>(4);
    private ArrayList<Card> revealedCardTier2List = new ArrayList<>(4);
    private ArrayList<Card> revealedCardTier3List = new ArrayList<>(4);

    //Deck meaning list of cards that was not drawn
    private ArrayList<Card> deckTier1 = new ArrayList<>();
    private ArrayList<Card> deckTier2 = new ArrayList<>();
    private ArrayList<Card> deckTier3 = new ArrayList<>();

    private int maxTokenStack = 7; // Default number of each token type

    private Database database;

    public Game(Database database) {
        this.database = database;
    }


    public TokenList getEmeraldTokens() {
        return emeraldTokens;
    }

    public TokenList getSapphireTokens() {
        return sapphireTokens;
    }

    public TokenList getRubyTokens() {
        return rubyTokens;
    }

    public TokenList getDiamondTokens() {
        return diamondTokens;
    }

    public TokenList getOnyxTokens() {
        return onyxTokens;
    }

    public TokenList getGoldTokens() {
        return goldTokens;
    }

    public int getMaxTokenStack() {
        return maxTokenStack;
    }






    public boolean startGame(Room room) {
        if (room.getPlayerCount() < 2) return false; // Minimum number of players to start a game is 2.
        if (room.getPlayerCount() > 4) return false; // Maximum number of players to start a game is 4.

        // Calculate number of tokens of each type
        if (room.getPlayerCount() == 2) this.maxTokenStack = 4;
        if (room.getPlayerCount() == 3) this.maxTokenStack = 5;

        // Assign all tokenLists
        this.emeraldTokens  = createTokenList(TokenType.EMERALD);
        this.sapphireTokens = createTokenList(TokenType.SAPPHIRE);
        this.rubyTokens     = createTokenList(TokenType.RUBY);
        this.diamondTokens  = createTokenList(TokenType.DIAMOND);
        this.onyxTokens     = createTokenList(TokenType.ONYX);
        this.goldTokens     = createTokenList(TokenType.GOLD_JOKER);

        //Get ALL cards from database
        this.deckTier1= database.getSpecifiedCards(CardTier.LEVEL_1);
        this.deckTier2= database.getSpecifiedCards(CardTier.LEVEL_2);
        this.deckTier3= database.getSpecifiedCards(CardTier.LEVEL_3);


        // Choose random cards from deck.
        this.revealedCardTier1List = getRandomCards( this.deckTier1, 4);
        this.revealedCardTier2List = getRandomCards( this.deckTier2, 4);
        this.revealedCardTier3List = getRandomCards( this.deckTier3, 4);

        return true;
    }






    /**
     *
     * @param tokenType -> Color of the token
     * @return TokenList -> Object representing all available tokens
     */
    private TokenList createTokenList(TokenType tokenType) {
        TokenList tokenList = new TokenList(tokenType);

        int numberOfRepeats = maxTokenStack;
        if (tokenType == TokenType.GOLD_JOKER) numberOfRepeats = 5; // There are only 5 golden tokens

        for (int i=0; i < numberOfRepeats; i++) {
            tokenList.addToken(new Token(tokenType));
        }

        return tokenList;
    }


    /**
     *
     * @param deck -> Collection of not revealed cards for a given Tier from we will draw a card
     * @param amount -> Amount of elements we want to draw
     * @return ArrayList<Card> -> Collection of randomly picked cards
     */
    private ArrayList<Card> getRandomCards(ArrayList<Card> deck, int amount) {
        int size = deck.size();
        if (size < amount) return null;

        ArrayList<Card> array = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>(size);
        for(int i = 1; i <= size; i++) {
            list.add(i);
        }


        Random rand = new Random();
        while(list.size() > 0) {
            int index = rand.nextInt(list.size()); // Get random index
            array.add(deck.get(index));
            //If the card was drawn it would not be used at any time by the Game class
            deck.remove(index);
            System.out.println("Selected: " + list.remove(index));
        }

        return array;
    }


}
