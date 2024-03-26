package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private int gameIDCount = 0;
    final private HashMap<Integer, UserData> users = new HashMap<>();
    final private HashMap<Integer, AuthData> tokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    public void iAmBecomeDeath(){
        users.clear();
        tokens.clear();
        games.clear();
        gameIDCount = 0;
    }

    public void createUser(UserData u){
        users.put(users.size()+1, u);
    }

    public UserData getUser(String u){
        for (Map.Entry<Integer, UserData> entry : users.entrySet()) {
            if (entry.getValue().username().equals(u)){
                return entry.getValue();
            }
        }
        return null;
    }

    public GameData createGame(String gameName){
        games.put(games.size()+1, new GameData(++gameIDCount, null, null, gameName, new ChessGame()));
        return games.get(games.size());
    }

    public boolean joinGame(String username, String color, int mapKey){
        if(color == null){
            return true;
        }
        color = color.toUpperCase(Locale.ROOT);
        GameData game = games.get(mapKey);
        if(color.equals("WHITE") && (game.whiteUsername() == null || game.whiteUsername().isEmpty())){
            games.put(mapKey, new GameData(
                game.gameID(),
                username,
                game.blackUsername(),
                game.gameName(),
                game.game()
                )
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
    }

    public GetGameResponse getGame(int gameID) {
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            if (gameID == entry.getValue().gameID()) {
                return new GetGameResponse(true, entry.getKey());
            }
        }
        return new GetGameResponse(false, 0);
    }

    public ArrayList<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void createAuth(AuthData a) {
        tokens.put(tokens.size()+1, a);
    }

    public String getAuth(String a) {
        for (Map.Entry<Integer, AuthData> entry : tokens.entrySet()) {
            if (a.equals(entry.getValue().authToken())){
                return entry.getValue().username();
            }
        }
        return "";
    }

    public boolean deleteAuth(String a) {
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
