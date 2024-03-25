import chess.ChessGame;
import chess.ChessPiece;
import dataAccess.SqlDataAccess;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.io.IOException;


public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearServer() throws ResponseException{
        facade.clear();
    }

    @Test
    void clearTest() throws Exception{
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.clear();
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void tryJoinClearedGame() throws Exception{
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        facade.clear();
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(1, "WHITE", authData.authToken()));
    }

    @Test
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerBad() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    void logoutOnce() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue(facade.logout(authData.authToken()));
    }

    @Test
    void logoutTwice() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void login() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.login("player1", "password"));
    }

    @Test
    void badLogin() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.login("charles", "secure"));
    }

    @Test
    void listGames() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).length == 0;
    }

    @Test
    void listGamesUnauthorized() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("badpassword"));
    }

    @Test
    void createGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).length == 0;
        facade.createGame("RATS", authData.authToken());
        assert facade.listGames(authData.authToken()).length == 1;
    }

    @Test
    void createGames() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).length == 0;
        facade.createGame("RATS", authData.authToken());
        facade.createGame("RATS", authData.authToken());
        assert facade.listGames(authData.authToken()).length == 2;
    }

    @Test
    void joinGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        assert facade.joinGame(1, "WHITE", authData.authToken()).equals("{}");
    }

    @Test
    void joinGameBadTeam() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(1, "REPUBLICAN", authData.authToken()));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
