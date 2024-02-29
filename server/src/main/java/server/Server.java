package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import spark.*;
import service.*;
public class Server {
    private final DataAccess dataAccess;
    private final AuthService authService;
    private final GameService gameService;
    record AuthLoginResult(String username, String authToken, String message){}
    record MessageResult(String message){}
    record CreateGameGood(int gameID){}

    public Server(){
        dataAccess = new MemoryDataAccess();
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
    }

    public Server(DataAccess dataAccess){
        this.dataAccess = dataAccess;
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
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

    private Object clear(Request req, Response res) {
        dataAccess.iAmBecomeDeath();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException {
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

    private Object login(Request req, Response res) throws DataAccessException {
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

    private Object logout(Request req, Response res) throws DataAccessException {
        authService.logout(req, res);
        return new Gson().toJson(new MessageResult(res.body()));
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        gameService.listGames(req, res);
        return res.body();
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        int id = gameService.createGame(req, res);
        if(res.status() == 200){
            return new Gson().toJson(new CreateGameGood(id));
        }
        return new Gson().toJson(new MessageResult(res.body()));
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        gameService.joinGame(req, res);
        return new Gson().toJson(new MessageResult(res.body()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
