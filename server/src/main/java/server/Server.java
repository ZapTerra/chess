package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import model.UserData;
import server.websocket.WebSocketHandler;
import spark.*;
import service.*;
public class Server {
    private final DataAccess dataAccess;
    private final AuthService authService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;

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
        return res.status();
    }

    private Object register(Request req, Response res) {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        res.status(200);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
