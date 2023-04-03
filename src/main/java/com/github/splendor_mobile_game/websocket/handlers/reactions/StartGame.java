package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.ArrayList;
import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.enums.Color;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

/**
 * Reaction class for handling the "START_GAME" message sent by the host player to start the game.
 * Sends a "GAME_STARTED_ANNOUNCEMENT" message to all players with the initial state of the game.
 * Also sends a "NEW_TURN_ANNOUNCEMENT" message with the player whose turn is first.
 *
 * Example of user request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME",
 *      "data": {
 *          "userUuid": "6850e6c1-6f1d-48c6-a412-52b39225ded7"
 *      }
 * }
 *
 * Example of server announcement in successful case:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "GAME_STARTED_ANNOUNCEMENT",
 *      "result": "OK",
 *      "data": {
 *          "tokens": {
 *              "red": 7,
 *              "green": 7,
 *              "blue": 7,
 *              "white": 7,
 *              "black": 7,
 *              "gold": 5,
 *          },
 *          "aristocrats": [
 *              {
 *                   "uuid": "81b7249e-d1f0-4030-a59d-0217ee3ac161",
 *                   "prestige": 3,
 *                   "redMinesRequired": 4,
 *                   "blueMinesRequired": 4
 *               },
 *               {
 *                   "uuid": "8bceab0a-d67f-44b2-ad4f-cda592cb4b13",
 *                   "prestige": 3,
 *                   "greenMinesRequired": 2,
 *                   "whiteMinesRequired": 3,
 *                   "blackMinesRequired": 3
 *               },
 *               ...
 *               ...
 *               ...
 *          ],
 *          "firstLevelMinesCards": [
 *              {
 *                  "uuid": "0ba9cba8-3bc0-42fe-b24f-25d7b52fcd2c",
 *                  "prestige": 2,
 *                  "bonusColor": "green",
 *                  "greenTokensRequired": 2,
 *                  "whiteTokensRequired": 3
 *              },
 *              {
 *                  "uuid": "e1df722d-a64c-424c-856e-431748bb358f",
 *                  "prestige": 0,
 *                  "bonusColor": "white",
 *                  "blackTokensRequired": 3
 *              }
 *              ...
 *              ...
 *              ...
 *          ],
 *          "secondLevelMinesCards": [], // The same as above
 *          "thirdLevelMinesCards": [], // The same as above
 *      }
 * }
 *
 * Example of implementation details:
 * - Check if the user is the host and is in a game.
 * - If yes, get the initial state of the game from the database.
 * - Send a "GAME_STARTED_ANNOUNCEMENT" message to all players with the initial state of the game.
 * - Send a "NEW_TURN_ANNOUNCEMENT" message with the player whose turn is first.
 *
 * Description of bad requests:
 * - If the user is not the host or is not in any game, send a "START_GAME_RESPONSE" message with a failure result and an error message.
 *
 * Example of response to the bad request:
 * {
 *      "messageContextId": "02442d1b-2095-4aaa-9db1-0dae99d88e03",
 *      "type": "START_GAME_RESPONSE",
 *      "result": "FAILURE",
 *      "data": {
 *          "error": "You cannot start the game if you are not the host or if you are not in any game!"
 *      }
 * }
 */
@ReactionName("START_GAME")
public class StartGame extends Reaction {

    public StartGame(int connectionHashCode, UserMessage userMessage, Messenger messenger, Database database) {
        super(connectionHashCode, userMessage, messenger, database);
    }

    public static class UserDTO {
        public UUID uuid;
        public String name;

        public UserDTO(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

    }


    @DataClass
    public static class DataDTO{
        public UserDTO userDTO;

        public DataDTO(UserDTO userDTO) {
            this.userDTO = userDTO;
        }   
    }
    public class UserDataResponse {
        public UUID userUuid;
        public String name;

        public UserDataResponse(UUID userUuid, String name) {
            this.userUuid = userUuid;
            this.name = name;
        }      
    }

    public class TokensDataResponse{
        public int red;
        public int green;
        public int blue;
        public int white;
        public int black;
        public int gold;

        public TokensDataResponse(int red, int green, int blue, int white, int black, int gold) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.white = white;
            this.black = black;
            this.gold = gold;
        }
    }

    public class AristocratDataResponse{
        public UUID uuid;
        public int prestige;

        public int redMinesRequired;
        public int greenMinesRequired;
        public int blueMinesRequired;
        public int whiteMinesRequired;
        public int blackMinesRequired;

        public AristocratDataResponse(UUID uuid, int prestige, int redMinesRequired, int greenMinesRequired,
                int blueMinesRequired, int whiteMinesRequired, int blackMinesRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.redMinesRequired = redMinesRequired;
            this.greenMinesRequired = greenMinesRequired;
            this.blueMinesRequired = blueMinesRequired;
            this.whiteMinesRequired = whiteMinesRequired;
            this.blackMinesRequired = blackMinesRequired;
        }
    }

    public class MinesCardDataResponse{
        public UUID uuid;
        public int prestige;
        public Color color;

        public int redTokensRequired;
        public int greenTokensRequired;
        public int blueTokensRequired;
        public int whiteTokensRequired;
        public int blackTokensRequired;

        public MinesCardDataResponse(UUID uuid, int prestige, Color color, int redTokensRequired,
                int greenTokensRequired, int blueTokensRequired, int whiteTokensRequired, int blackTokensRequired) {
            this.uuid = uuid;
            this.prestige = prestige;
            this.color = color;
            this.redTokensRequired = redTokensRequired;
            this.greenTokensRequired = greenTokensRequired;
            this.blueTokensRequired = blueTokensRequired;
            this.whiteTokensRequired = whiteTokensRequired;
            this.blackTokensRequired = blackTokensRequired;
        }
        
    }

    public class ResponseData{
        public TokensDataResponse tokens;
        public ArrayList<AristocratDataResponse> aristocrats;

        public ArrayList<MinesCardDataResponse> firstLevelMinesCards;
        public ArrayList<MinesCardDataResponse> secondLevelMinesCards;
        public ArrayList<MinesCardDataResponse> thirdLevelMinesCards;

        public ResponseData(TokensDataResponse tokens, ArrayList<AristocratDataResponse> aristocrats,
                ArrayList<MinesCardDataResponse> firstLevelMinesCards,
                ArrayList<MinesCardDataResponse> secondLevelMinesCards,
                ArrayList<MinesCardDataResponse> thirdLevelMinesCards) {
            this.tokens = tokens;
            this.aristocrats = aristocrats;
            this.firstLevelMinesCards = firstLevelMinesCards;
            this.secondLevelMinesCards = secondLevelMinesCards;
            this.thirdLevelMinesCards = thirdLevelMinesCards;
        }
        
    }

    @Override
    public void react() {
        DataDTO dataDTO = (DataDTO)userMessage.getData();
        
    }
    
}
