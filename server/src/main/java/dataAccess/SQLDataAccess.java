package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class SQLDataAccess implements DataAccess{
    public void iAmBecomeDeath(){

    }

    public void createUser(UserData u) throws DataAccessException{

    }

    public UserData getUser(String u) throws DataAccessException{
        return null;
    }

    public int createGame(String gameName) throws DataAccessException{
        return 0;
    }

    public boolean joinGame(String username, String color, int gameID){
        return true;
    }

    public MemoryDataAccess.GetGameResponse getGame(int gameID) throws DataAccessException{
        return null;
    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException{
        return null;
    }

    //void updateGame() throws DataAccessException;

    public void createAuth(AuthData a) throws DataAccessException{

    }

    public String getAuth(AuthData a) throws DataAccessException{
        return "";
    }

    public boolean deleteAuth(String a) throws DataAccessException{
        return true;
    }
}
