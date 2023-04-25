package com.github.splendor_mobile_game.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.ReservationResult;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.DeckIsEmptyException;
import com.github.splendor_mobile_game.websocket.utils.Log;

public class Game {


    private final Map<TokenType, Integer> tokensOnTable = new HashMap<TokenType, Integer>();

    private int gameReservationCount=0;

    private final ArrayList<User> users = new ArrayList<>();

    private final Map<CardTier,Deck> revealedCards = new HashMap<CardTier,Deck>(); // Cards that were already revealed
    private final Map<CardTier,Deck> decks = new HashMap<CardTier,Deck>(); // Cards of each tier visible on the table

    private ArrayList<Noble> nobles;
    /** Maximum number of non-gold tokens generated for game. Depends on player count */
    private int maxNonGoldTokensOnStart = 7;
    private final Database database;

    public Game(Database database, ArrayList<User> users) {
        this.database = database;
;
        start(users.size());
    }

    public ReservationResult reserveCardFromDeck(CardTier tier,User player) throws DeckIsEmptyException{
        Card card = getRandomCard(tier);
        if(card==null){
            throw new DeckIsEmptyException("Deck "+tier+" is empty");
        }

        boolean goldenToken = removeToken(TokenType.GOLD_JOKER);
        player.reserveCard(card,goldenToken);

        gameReservationCount++;

        return new ReservationResult(card, goldenToken);
    }
    public ReservationResult reserveCardFromTable(Card card,User player) throws DeckIsEmptyException{

        if(card==null){
            throw new DeckIsEmptyException("Deck is empty");
        }
        boolean goldenToken = removeToken(TokenType.GOLD_JOKER);
        player.reserveCard(card,goldenToken);
        Card newcard = takeCardFromRevealed(card);

        gameReservationCount++;

        return new ReservationResult(newcard, goldenToken);
    }

    public void DecreaseGameReservationCount(){
        gameReservationCount--;
    }

    public int getGameReservationCount(){
        return gameReservationCount;
    }


    private boolean removeToken(TokenType type){
        if(tokensOnTable.get(type)==0){
            return false;
        }

        tokensOnTable.put(type, tokensOnTable.get(type)-1);
        return true;
    }
    
    //The return Card is a card that was drawn from deck and put on table
    public Card takeCardFromRevealed(Card card){
        
        removeCardFromRevealed(card);

        Card cardDrawn = getRandomCard(card.getCardTier());
        addCardToRevealed(cardDrawn);

        return cardDrawn;
    }

    private void removeCardFromRevealed(Card card){
        CardTier cardTier = card.getCardTier();
        revealedCards.get(cardTier).remove(card);
    }

    private void addCardToRevealed(Card card){
        revealedCards.get(card.getCardTier()).add(card);
    }

    public boolean isCardRevealed(UUID uuid)
    {
        for (Deck deck : revealedCards.values())
        {
            for(Card card : deck)
            {
                if(card.getUuid() == uuid) return true;
            }
        }
        return false;
    }


    private void start(int playerCount) {
        // Calculate number of tokens of each type
        if (playerCount == 2) this.maxNonGoldTokensOnStart = 4;
        if (playerCount == 3) this.maxNonGoldTokensOnStart = 5;

        // Assign all tokenLists
        tokensOnTable.put(TokenType.EMERALD,    maxNonGoldTokensOnStart);
        tokensOnTable.put(TokenType.SAPPHIRE,   maxNonGoldTokensOnStart);
        tokensOnTable.put(TokenType.RUBY,       maxNonGoldTokensOnStart);
        tokensOnTable.put(TokenType.DIAMOND,    maxNonGoldTokensOnStart);
        tokensOnTable.put(TokenType.ONYX,       maxNonGoldTokensOnStart);
        tokensOnTable.put(TokenType.GOLD_JOKER, 5);

        //Get ALL cards from database
        decks.put(CardTier.LEVEL_1,new Deck(CardTier.LEVEL_1,database.getSpecifiedCards(CardTier.LEVEL_1)));
        decks.put(CardTier.LEVEL_2,new Deck(CardTier.LEVEL_2,database.getSpecifiedCards(CardTier.LEVEL_2)));
        decks.put(CardTier.LEVEL_3,new Deck(CardTier.LEVEL_3,database.getSpecifiedCards(CardTier.LEVEL_3)));
        
        // Choose random cards from deck
        revealedCards.put(CardTier.LEVEL_1,new Deck(CardTier.LEVEL_1,getRandomCards((CardTier.LEVEL_1),4)));
        revealedCards.put(CardTier.LEVEL_2,new Deck(CardTier.LEVEL_2,getRandomCards((CardTier.LEVEL_2),4)));
        revealedCards.put(CardTier.LEVEL_3,new Deck(CardTier.LEVEL_3,getRandomCards((CardTier.LEVEL_3),4)));

        // Choose random noble cards from database
        nobles = getRandomNobles(4);//Always we draw four noblemen

        //Only for testing TO BE DELTED
        //testForDuplicates(CardTier.LEVEL_1);
        //testForDuplicates(CardTier.LEVEL_2);
        //testForDuplicates(CardTier.LEVEL_3);
        //testForDuplicatesNoble();

        // takeNobleTest();
    }


    public int getTokenCount(TokenType type) {
        return tokensOnTable.get(type);
    }

    /** 
     * function which updates token amount on the table by adding or subtracting their current amount by numbers listed in tokensChange map 
     * It is used in GetTokens reaction so it skips Gold token type because users can't take gold tokens by themselves
    */
    public void changeTokens(Map<TokenType, Integer> tokenMap) {
        for(Map.Entry<TokenType, Integer> set : this.tokensOnTable.entrySet()) {
            if(set.getKey() == TokenType.GOLD_JOKER) continue;
            this.tokensOnTable.put(set.getKey(), set.getValue() - tokenMap.get(set.getKey()));
        }
    }


    //Only for testing private function TO BE DELETED
    private void testForDuplicatesNoble(){
        ArrayList<Noble> array = nobles;

        for(int i=0;i<array.size();++i){
            for(int j=0;j<array.size();++j){
                if(i!=j){
                    if(array.get(i)==array.get(j))
                        Log.ERROR("Found duplicate at i="+i+" and j="+j);
                }
            }
        }
    }

    //Only for testing private function TO BE DELETED
    private void testForDuplicates(CardTier tier){
        Deck deck1 = revealedCards.get(tier);

        for(int i=0;i<deck1.size();++i){
            for(int j=0;j<deck1.size();++j){
                if(i!=j){
                    if(deck1.get(i)==deck1.get(j))
                        Log.ERROR("Found duplicate at i="+i+" and j="+j);
                }
            }
        }
    }

    public Deck getRevealedCards(CardTier tier){
        Deck deck = new Deck(tier, revealedCards.get(tier));
        return deck;
    }

    public ArrayList<Noble> getNobles(){
        ArrayList<Noble> nobles=new ArrayList<>(this.nobles);
        return nobles;
    }

    public int getTokens(TokenType type){
        return this.tokensOnTable.get(type);
    }


//    public void takeNobleTest() //will be deleted (only test function)
//    {
//         User u = database.getAllUsers().get(0);

//         Log.DEBUG("FAJNE DZIALA0");

//         u.tokens.put(TokenType.EMERALD,100);
//         u.tokens.put(TokenType.ONYX,100);
//         u.tokens.put(TokenType.DIAMOND,100);
//         u.tokens.put(TokenType.SAPPHIRE,100);
//         u.tokens.put(TokenType.RUBY,100);


//         try {
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.SAPPHIRE));

//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.EMERALD));

//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.RUBY));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.RUBY));
//             u.buyCard(new Card(CardTier.LEVEL_1, 1, 2, 2, 2, 2, 2, TokenType.RUBY));
//         } catch (Exception e) {
//             System.out.println(e.getMessage());
//         }

//            ArrayList<Noble> list = new ArrayList<>();
//            list.add(database.getAllNobles().get(1));
//            list.add(database.getAllNobles().get(4));
//            list.add(database.getAllNobles().get(5));
//            list.add(database.getAllNobles().get(6));
//            this.nobles = list;

//            Log.DEBUG("FAJNE DZIALA1");

//            for (Noble noble : nobles) {
//                try {
//                     u.takeNoble(noble);
//                } catch (Exception e) {
//                     Log.ERROR(e.getMessage());
//                }
//             }

//            Log.DEBUG("FAJNE DZIALA2");
//        }
       


    private Card getRandomCard(CardTier tier){
        Deck deck = getRandomCards(tier,1);
        if(deck == null){
            return null;
        }
        return deck.get(0);
    }


    /**
     *
     * @param tier -> Tier of deck from which we draw cards
     * @param amount -> Amount of elements we want to draw
     * @return Deck -> Collection of randomly picked cards
     */
    private Deck getRandomCards(CardTier tier, int amount) {
        Deck deck = decks.get(tier);

        // We draw cards until deck will be empty
        if ( deck.size() < amount) amount=deck.size();

        Deck array = new Deck(tier);

        Random rand = new Random();
        for(;amount > 0;amount--) {
            if(deck.size()==0){
                return null;
            }
            int index = rand.nextInt(deck.size()); // Get random index
            Card drawnCard =deck.remove(index);
            array.add(drawnCard);
            
            //globalIndex in that context is an index of InMemoryDatabase.allCards
            int globalIndex=database.getAllCards().indexOf(drawnCard);          
            Log.DEBUG("Card has been drawn of tier "+tier.toString()+" and index "+globalIndex);
        }

        return array;
    }

    private ArrayList<Noble> getRandomNobles(int amount){
        ArrayList<Noble> nobles = this.database.getAllNobles();
        ArrayList<Noble> array = new ArrayList<>();

        // We draw cards until deck will be empty
        if ( nobles.size() < amount) amount=nobles.size();

        Random rand = new Random();
        while(amount > 0) {
            if(nobles.size()==0){
                return null;
            }
            int index = rand.nextInt(nobles.size()); // Get random index       
            array.add(nobles.remove(index));
            
            Log.DEBUG("Noble tile has been drawned");
            amount--;
        }

        return array;
    }


}
