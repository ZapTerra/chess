package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.*;
import spark.Request;
import spark.Response;


public class GameService {
    private final DataAccess dataAccess;
    record GameListResponse<C>(C games){}
    record MessageResponse(String message){}
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void listGames(Request req, Response res) throws DataAccessException, ResponseException {
        String auth = req.headers("Authorization");
        var username = dataAccess.getAuth(auth);
        if(username.isEmpty()){
            res.status(401);
            res.body(new Gson().toJson(new MessageResponse("Error: unauthorized")));
            return;
        }

        res.body(new Gson().toJson(dataAccess.listGames()));
        res.status(200);
    }

    public GameData createGame(Request req, Response res) throws DataAccessException, ResponseException {
        String auth = req.headers("Authorization");
        var username = dataAccess.getAuth(auth);
        if(username.isEmpty()){
            res.status(401);
            res.body("Error: unauthorized");
            return null;
        }
        var requestData = new Gson().fromJson(req.body(), CreateGameRequest.class);
        var gameData = dataAccess.createGame(requestData.gameName());
        res.status(200);
        return gameData;
    }

    public void joinGame(Request req, Response res) throws DataAccessException, ResponseException {
        String auth = req.headers("Authorization");
        var requestData = new Gson().fromJson(req.body(), JoinGameRequest.class);
        var username = dataAccess.getAuth(auth);
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
        MemoryDataAccess.GetGameResponse searchResult = dataAccess.getGame(requestData.gameID());
        if(!searchResult.found()){
            res.status(400);
            res.body("Error: bad game ID");
            return;
        }
        if(!dataAccess.joinGame(username, requestData.playerColor(), searchResult.mapKey())){
            res.status(403);
            System.out.println(username);
            res.body("Error: already taken");
            return;
        }
        res.status(200);
    }
}
