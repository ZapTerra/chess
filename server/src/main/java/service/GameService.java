package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.Request;
import spark.Response;


public class GameService {
    private final DataAccess dataAccess;
    record CreateGameRequest(String gameName){}
    record GameListResponse<C>(C games){}
    record MessageResponse(String message){}
    record JoinGameRequest(String playerColor, int gameID){}
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void listGames(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        var username = dataAccess.getAuth(new AuthData("", auth));
        if(username.isEmpty()){
            res.status(401);
            res.body(new Gson().toJson(new MessageResponse("Error: unauthorized")));
            return;
        }

        res.body(new Gson().toJson(new GameListResponse<>(dataAccess.listGames().values())));
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

    public void joinGame(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        var requestData = new Gson().fromJson(req.body(), JoinGameRequest.class);
        var username = dataAccess.getAuth(new AuthData("", auth));
        if(requestData == null){
            res.status(400);
            res.body("Error: bad request");
            return;
        }
        if(username.isEmpty()){
            res.status(401);
            res.body("Error: unauthorized");
            return;
        }
        MemoryDataAccess.GetGameResponse searchResult = dataAccess.getGame(requestData.gameID);
        if(!searchResult.found()){
            res.status(400);
            res.body("Error: bad game ID");
            return;
        }
        if(!dataAccess.joinGame(username, requestData.playerColor, searchResult.mapKey())){
            res.status(403);
            res.body("Error: already taken");
            return;
        }
        res.status(200);
    }
}
