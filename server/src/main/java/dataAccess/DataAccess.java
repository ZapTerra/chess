package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public interface DataAccess {
    void iAmBecomeDeath();

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String u) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    boolean joinGame(String username, String color, int gameID) throws DataAccessException;

    MemoryDataAccess.GetGameResponse getGame(int gameID) throws DataAccessException;

    HashMap<Integer, GameData> listGames() throws DataAccessException;

    void createAuth(AuthData a) throws DataAccessException;

    String getAuth(AuthData a) throws DataAccessException;

    boolean deleteAuth(String a) throws DataAccessException;
}
