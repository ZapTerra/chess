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

    public void createGame() throws DataAccessException {
        System.out.println("RATS");
    }

    public void getGame() throws DataAccessException {

    }

    public void listGames() throws DataAccessException {

    }

    public void updateGame() throws DataAccessException {

    }

    public void createAuth(AuthData a) throws DataAccessException {
        tokens.put(tokens.size()+1, a);
    }

    public void getAuth() throws DataAccessException {

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
