package com.github.splendor_mobile_game.game.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.websocket.utils.Log;

public class Game {

    private TokenList emeraldTokens;
    private TokenList sapphireTokens;
    private TokenList rubyTokens;
    private TokenList diamondTokens;
    private TokenList onyxTokens;
    private TokenList goldTokens;

    private Map<CardTier,Deck> revealedDecks = new HashMap<CardTier,Deck>();
    private Map<CardTier,Deck> decks = new HashMap<CardTier,Deck>();

    private int maxTokenStack = 7; // Default number of each token type

    private Database database;

    public Game(Database database) {
        this.database = database;
        database.loadCards();

        decks.put(CardTier.LEVEL_1,new Deck(CardTier.LEVEL_1));
        decks.put(CardTier.LEVEL_2,new Deck(CardTier.LEVEL_2));
        decks.put(CardTier.LEVEL_3,new Deck(CardTier.LEVEL_3));

        revealedDecks.put(CardTier.LEVEL_1,new Deck(CardTier.LEVEL_1));
        revealedDecks.put(CardTier.LEVEL_2,new Deck(CardTier.LEVEL_2));
        revealedDecks.put(CardTier.LEVEL_3,new Deck(CardTier.LEVEL_3));
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

        //If startGame will be called second time, we need to clear decks
        //Maybe will be good idea to make startGame private and call it in constructior?
        //because why we would call startGame more than once?
        decks.get(CardTier.LEVEL_1).clear();
        decks.get(CardTier.LEVEL_2).clear();
        decks.get(CardTier.LEVEL_3).clear();
        revealedDecks.get(CardTier.LEVEL_1).clear();
        revealedDecks.get(CardTier.LEVEL_2).clear();
        revealedDecks.get(CardTier.LEVEL_3).clear();


        //Get ALL cards from database
        decks.get(CardTier.LEVEL_1).addAll(database.getSpecifiedCards(CardTier.LEVEL_1)); 
        decks.get(CardTier.LEVEL_2).addAll(database.getSpecifiedCards(CardTier.LEVEL_2));
        decks.get(CardTier.LEVEL_3).addAll(database.getSpecifiedCards(CardTier.LEVEL_3));


        // Choose random cards from deck.
        //For testing maxium cards that can be drawn TO BE DELETED
        revealedDecks.get(CardTier.LEVEL_1).addAll(getRandomCards((CardTier.LEVEL_1),Deck.whatSize(CardTier.LEVEL_1)));
        revealedDecks.get(CardTier.LEVEL_2).addAll(getRandomCards((CardTier.LEVEL_2),Deck.whatSize(CardTier.LEVEL_2)));
        revealedDecks.get(CardTier.LEVEL_3).addAll(getRandomCards((CardTier.LEVEL_3),Deck.whatSize(CardTier.LEVEL_3)));

        //Only for testing TO BE DELTED
        testForDuplicates(CardTier.LEVEL_1);
        testForDuplicates(CardTier.LEVEL_2);
        testForDuplicates(CardTier.LEVEL_3);

        return true;
    }

    //Only for testing private function TO BE DELETED
    private void testForDuplicates(CardTier tier){
        Deck deck1 = revealedDecks.get(CardTier.LEVEL_1);

        for(int i=0;i<deck1.size();++i){
            for(int j=0;j<deck1.size();++j){
                if(i!=j){
                    if(deck1.get(i)==deck1.get(j))
                        Log.ERROR("Found duplicate at i="+i+" and j="+j);
                }
            }
        }
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

    private Card getRandomCard(CardTier tier){
        return getRandomCards(tier,1).get(0);
    }


    /**
     *
     * @param tier -> Tier of deck from which we draw cards
     * @param amount -> Amount of elements we want to draw
     * @return Deck -> Collection of randomly picked cards
     */
    public Deck getRandomCards(CardTier tier, int amount) {
        Deck deck = decks.get(tier);

        // We draw cards until deck will be empty
        if ( deck.size() < amount) amount=deck.size();

        Deck array = new Deck(tier);

        Random rand = new Random();
        for(;amount > 0;amount--) {
            int index = rand.nextInt(deck.size()); // Get random index
            Card drawnCard =deck.remove(index);
            array.add(drawnCard);
            
            //globalIndex in that context is an index of InMemoryDatabase.allCards
            int globalIndex=database.getAllCards().indexOf(drawnCard);          
            Log.DEBUG("Card has been drawn of tier "+tier.toString()+" and index "+globalIndex);
        }

        return array;
    }

}
