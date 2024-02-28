package service;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class GameService {
    private final DataAccess dataAccess;
    record CreateGameRequest(String gameName){}
    record GameListResponse(HashMap<Integer, GameData> games){}
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void listGames(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        var username = dataAccess.getAuth(new AuthData("", auth));
        if(username.isEmpty()){
            res.status(401);
            res.body("Error: unauthorized");
            return;
        }
        res.body(new Gson().toJson(new GameListResponse(dataAccess.listGames())));
        res.status(200);
    }
    public int createGame(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        var username = dataAccess.getAuth(new AuthData("", auth));
        if(username.isEmpty()){
            res.status(401);
            res.body("Error: unauthorized");
            return 0;
        }
        var requestData = new Gson().fromJson(req.body(), CreateGameRequest.class);
        int id = dataAccess.createGame(requestData.gameName());
        res.status(200);
        return id;
    }
}
