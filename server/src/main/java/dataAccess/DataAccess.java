package dataAccess;

import model.UserData;

public interface DataAccess {
    public void iAmBecomeDeath();

    void createUser() throws DataAccessException;

    void insertUser(UserData u) throws DataAccessException;
    void getUser() throws DataAccessException;

    void createGame() throws DataAccessException;

    void getGame() throws DataAccessException;

    void listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void createAuth() throws DataAccessException;

    void getAuth() throws DataAccessException;

    void deleteAuth() throws DataAccessException;
}
