package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private int gameIdCount = 0;
    final private HashMap<Integer, UserData> users = new HashMap<>();
    final private HashMap<Integer, AuthData> tokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    public void iAmBecomeDeath(){
        users.clear();
        tokens.clear();
    }

    public void createUser(UserData u) throws DataAccessException {
        users.put(users.size()+1, u);
    }

    public UserData getUser(String u) throws DataAccessException {
        for (Map.Entry<Integer, UserData> entry : users.entrySet()) {
            if (entry.getValue().username().equals(u)){
                return entry.getValue();
            }
        }
        return null;
    }

    public int createGame(String gameName) throws DataAccessException {
        games.put(games.size()+1, new GameData(gameIdCount++, "", "", new ChessGame()));
        return gameIdCount;
    }

    public void getGame() throws DataAccessException {

    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        return games;
    }

    public void updateGame() throws DataAccessException {

    }

    public void createAuth(AuthData a) throws DataAccessException {
        tokens.put(tokens.size()+1, a);
    }

    public String getAuth(AuthData a) throws DataAccessException {
        for (Map.Entry<Integer, AuthData> entry : tokens.entrySet()) {
            if (a.authToken().equals(entry.getValue().authToken())){
                return entry.getValue().username();
            }
        }
        return "";
    }

    public boolean deleteAuth(String a) throws DataAccessException {
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
    }
}
