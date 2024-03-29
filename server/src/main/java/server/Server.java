package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.SqlDataAccess;
import exception.ResponseException;
import model.GameData;
import server.websocket.WebSocketHandler;
import spark.*;
import service.*;
public class Server {
    private final DataAccess dataAccess;
    private final AuthService authService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;
    record AuthLoginResult(String username, String authToken, String message){}
    record MessageResult(String message){}
    record CreateGameGood(int gameID){}

    public Server(){
        dataAccess = new SqlDataAccess();
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
        webSocketHandler = new WebSocketHandler();
    }

    public Server(DataAccess dataAccess){
        this.dataAccess = dataAccess;
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request req, Response res) throws ResponseException {
        dataAccess.iAmBecomeDeath();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException, ResponseException {
        String name = null;
        String token = null;
        var serviceResponse = authService.createUser(req, res);
        if(serviceResponse != null){
            name = serviceResponse.username();
            token = serviceResponse.authToken();
        }

        var registerResult = new AuthLoginResult(name, token, res.body());
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws DataAccessException, ResponseException {
        String name = null;
        String token = null;
        var serviceResponse = authService.login(req, res);
        if(serviceResponse != null){
            name = serviceResponse.username();
            token = serviceResponse.authToken();
        }

        var registerResult = new AuthLoginResult(name, token, res.body());
        return new Gson().toJson(registerResult);
    }

    private Object logout(Request req, Response res) throws DataAccessException, ResponseException {
        return authService.logout(req, res);
    }

    private Object listGames(Request req, Response res) throws DataAccessException, ResponseException {
        gameService.listGames(req, res);
        System.out.println(res.body());
        return res.body();
    }

    private Object createGame(Request req, Response res) throws DataAccessException, ResponseException {
        GameData gameData = gameService.createGame(req, res);
        if(res.status() == 200){
            return new Gson().toJson(gameData);
        }
        return new Gson().toJson(new MessageResult(res.body()));
    }

    private Object joinGame(Request req, Response res) throws DataAccessException, ResponseException {
        gameService.joinGame(req, res);
        return new Gson().toJson(new MessageResult(res.body()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
