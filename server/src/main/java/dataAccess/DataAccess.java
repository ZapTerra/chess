package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public interface DataAccess {
    public void iAmBecomeDeath();

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String u) throws DataAccessException;

    public int createGame(String gameName) throws DataAccessException;

    void getGame() throws DataAccessException;

    HashMap<Integer, GameData> listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void createAuth(AuthData a) throws DataAccessException;

    String getAuth(AuthData a) throws DataAccessException;

    boolean deleteAuth(String a) throws DataAccessException;
}
