package dataAccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public interface DataAccess {
    public record GetGameResponse(boolean found, int mapKey){}
    public void iAmBecomeDeath() throws ResponseException;

    void createUser(UserData u) throws DataAccessException, ResponseException;

    UserData getUser(String u) throws DataAccessException, ResponseException;

    public int createGame(String gameName) throws DataAccessException, ResponseException;

    public boolean joinGame(String username, String color, int gameID) throws ResponseException;

    GetGameResponse getGame(int gameID) throws DataAccessException, ResponseException;

    HashMap<Integer, GameData> listGames() throws DataAccessException, ResponseException;

    //void updateGame() throws DataAccessException;

    void createAuth(AuthData a) throws DataAccessException, ResponseException;

    String getAuth(String a) throws DataAccessException, ResponseException;

    boolean deleteAuth(String a) throws DataAccessException, ResponseException;
}
