package dataAccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    public void iAmBecomeDeath();

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String u) throws DataAccessException;

    void createGame() throws DataAccessException;

    void getGame() throws DataAccessException;

    void listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void createAuth(AuthData a) throws DataAccessException;

    void getAuth() throws DataAccessException;

    boolean deleteAuth(String a) throws DataAccessException;
}
