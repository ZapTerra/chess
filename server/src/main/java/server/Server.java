package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import server.websocket.WebSocketHandler;
import spark.*;
import service.*;
public class Server {
    private final DataAccess dataAccess;
    private final AuthService authService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;
    record RegisterResult(String username, String authToken, String message){}

    public Server(){
        dataAccess = new MemoryDataAccess();
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

        var registerResult = new RegisterResult(name, token, res.body());
        return new Gson().toJson(registerResult);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
