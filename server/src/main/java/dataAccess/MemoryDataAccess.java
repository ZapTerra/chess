package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<Integer, UserData> users = new HashMap<>();
    final private HashMap<Integer, AuthData> tokens = new HashMap<>();
    public void iAmBecomeDeath(){
        users.clear();
        tokens.clear();
    }

    public void createUser(UserData u, AuthData a) throws DataAccessException {
        users.put(users.size()+1, u);
        tokens.put(tokens.size()+1, a);
    }

    public UserData getUser(UserData u) throws DataAccessException {
        String target = u.username();
        for (Map.Entry<Integer, UserData> entry : users.entrySet()) {
            if (entry.getValue().username().equals(target)){
                return entry.getValue();
            }
        }
        return null;
    }

    public void createGame() throws DataAccessException {
        System.out.println("RATS");
    }

    public void getGame() throws DataAccessException {

    }

    public void listGames() throws DataAccessException {

    }

    public void updateGame() throws DataAccessException {

    }

    public void createAuth() throws DataAccessException {

    }

    public void getAuth() throws DataAccessException {

    }

    public void deleteAuth() throws DataAccessException {

    }
}
