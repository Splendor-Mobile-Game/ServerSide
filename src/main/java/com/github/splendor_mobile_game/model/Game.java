package com.github.splendor_mobile_game.model;

import com.github.splendor_mobile_game.enums.TokenType;

import java.util.ArrayList;

public class Game {

    private TokenList emeraldTokens;
    private TokenList sapphireTokens;
    private TokenList rubyTokens;
    private TokenList diamondTokens;
    private TokenList onyxTokens;
    private TokenList goldTokens;

    private int maxTokenStack = 7; // Default number of each token type

    private int playerCount;
    private final ArrayList<User> players = new ArrayList<>();

    public Game() {

    }


    public ArrayList<User> getAllPlayers() {
        return players;
    }


    public boolean playerExists(User user) {
        return players.contains(user);
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

    public int getPlayerCount() {
        return playerCount;
    }



    /**
     *
     * Add new player to the game if number of currently awaiting players is smaller than 4.
     *
     * @param user -> user who is trying to join the game
     * @return boolean -> true if user successfully joined the game
     */
    public boolean joinGame(User user) {
        if (playerCount >= 4) return false; // Maximum number of players reached

        players.add(user);
        playerCount++;
        return true;
    }




    /**
     *
     * Remove a player from the game if he is awaiting.
     *
     * @param user -> user who is trying to leave the game
     * @return boolean -> true if user successfully left the game
     */
    public boolean leaveGame(User user) {
        if (playerCount < 0) return false;  // There is no one awaiting
        if (!players.contains(user)) return false;  // Player is not part of the game.

        players.remove(user);
        playerCount--;
        return true;
    }




    public boolean startGame() {
        if (playerCount < 2) return false; // Minimum number of players to start a game is 2.
        if (playerCount > 4) return false; // Maximum number of players to start a game is 4.

        // Calculate number of tokens of each type
        if (playerCount == 2) this.maxTokenStack = 4;
        if (playerCount == 3) this.maxTokenStack = 5;

        // Assign all tokenLists
        this.emeraldTokens  = createTokenList(TokenType.EMERALD);
        this.sapphireTokens = createTokenList(TokenType.SAPPHIRE);
        this.rubyTokens     = createTokenList(TokenType.RUBY);
        this.diamondTokens  = createTokenList(TokenType.DIAMOND);
        this.onyxTokens     = createTokenList(TokenType.ONYX);
        this.goldTokens     = createTokenList(TokenType.GOLD_JOKER);



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





}
