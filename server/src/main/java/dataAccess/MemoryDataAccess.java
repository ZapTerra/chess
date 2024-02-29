package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private int gameIDCount = 0;
    final private HashMap<Integer, UserData> users = new HashMap<>();
    final private HashMap<Integer, AuthData> tokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    public record GetGameResponse(boolean found, int mapKey){}
    public void iAmBecomeDeath(){
        users.clear();
        tokens.clear();
        games.clear();
        gameIDCount = 0;
    }

    public void createUser(UserData u) throws DataAccessException {
        try {
            users.put(users.size() + 1, u);
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public UserData getUser(String u) throws DataAccessException {
        try {
            for (Map.Entry<Integer, UserData> entry : users.entrySet()) {
                if (entry.getValue().username().equals(u)){
                    return entry.getValue();
                }
            }
            return null;
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public int createGame(String gameName) throws DataAccessException {
        try {
            games.put(games.size()+1, new GameData(++gameIDCount, null, null, gameName, new ChessGame()));
            return gameIDCount;
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public boolean joinGame(String username, String color, int mapKey) throws DataAccessException {
        try {
            if(color == null){
                return true;
            }
            GameData game = games.get(mapKey);
            if(color.equals("WHITE") && (game.whiteUsername() == null || game.whiteUsername().isEmpty())){
                games.put(mapKey, new GameData(
<<<<<<< HEAD
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
                    )
=======
                                game.gameID(),
                                username,
                                game.blackUsername(),
                                game.gameName(),
                                game.game()
                        )
>>>>>>> 94bab2b0f0dea12f810889d4f14a9a10d336ff56
                );
                return true;
            }
            if(color.equals("BLACK") && (game.blackUsername() == null ||  game.blackUsername().isEmpty())){
                games.put(mapKey, new GameData(
                                game.gameID(),
                                game.whiteUsername(),
                                username,
                                game.gameName(),
                                game.game()
                        )
                );
                return true;
            }
            return false;
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public GetGameResponse getGame(int gameID) throws DataAccessException {
        try {
            for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
                if (gameID == entry.getValue().gameID()) {
                    return new GetGameResponse(true, entry.getKey());
                }
            }
            return new GetGameResponse(false, 0);
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        try {
            return games;
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void createAuth(AuthData a) throws DataAccessException {
        try {
            tokens.put(tokens.size() + 1, a);
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public String getAuth(AuthData a) throws DataAccessException {
        try {
            for (Map.Entry<Integer, AuthData> entry : tokens.entrySet()) {
                if (a.authToken().equals(entry.getValue().authToken())){
                    return entry.getValue().username();
                }
            }
            return "";
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    public boolean deleteAuth(String a) throws DataAccessException {
        try {
            int key = 0;
            AuthData kill = new AuthData("","");
            boolean found = false;
            for (Map.Entry<Integer, AuthData> entry : tokens.entrySet()) {
                if (a.equals(entry.getValue().authToken())){
                    key = entry.getKey();
                    kill = entry.getValue();
                    found = true;
                }
            }
            if(found){
                tokens.remove(key, kill);
                return true;
            }
            return false;
        } catch (Exception ex){
            throw new DataAccessException(ex.getMessage());
        }
    }
}
