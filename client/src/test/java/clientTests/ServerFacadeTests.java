package clientTests;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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
    void createGameJoinAndLogoutThenRegisterNewAndJoin() throws Exception {
        var authToken = facade.register("player1", "password", "p1@email.com").authToken();
        facade.createGame("CHAMPIONSHIP", authToken);
        facade.joinGame(1, "WHITE", authToken);
        facade.logout(authToken);
        var newAuthToken = facade.register("player2", "StrongerPassword", "p1@email.com").authToken();
        Assertions.assertDoesNotThrow(() -> facade.joinGame(1, "BLACK", newAuthToken));
    }

    @Test
    void twoAuthTokens() throws Exception {
        var authToken = facade.register("player1", "password", "p1@email.com").authToken();
        var secondAuthToken = facade.login("player1", "password").authToken();
        facade.createGame("CHAMPIONSHIP", authToken);
        facade.joinGame(1, "BLACK", authToken);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(1, "WHITE", secondAuthToken));
    }

    @Test
    void logoutUnauthorized(){
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("RATS"));
    }

    @Test
    void login() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.login("player1", "password"));
    }

    @Test
    void badLogin(){
        Assertions.assertThrows(ResponseException.class, () -> facade.login("charles", "secure"));
    }

    @Test
    void listGames() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).isEmpty();
    }

    @Test
    void listGamesUnauthorized(){
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("BadPassword"));
    }

    @Test
    void createGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).isEmpty();
        facade.createGame("RATS", authData.authToken());
        assert facade.listGames(authData.authToken()).size() == 1;
    }

    @Test
    void createGames() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assert facade.listGames(authData.authToken()).isEmpty();
        facade.createGame("RATS", authData.authToken());
        facade.createGame("RATS", authData.authToken());
        assert facade.listGames(authData.authToken()).size() == 2;
    }

    @Test
    void createGameUnauthorized(){
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame("RATS", "RATS"));
    }

    @Test
    void joinGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        assert facade.joinGame(1, "WHITE", authData.authToken()).equals("{}");
    }

    @Test
    void joinGameThenLogout() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        facade.joinGame(1, "WHITE", authData.authToken());
        Assertions.assertTrue(facade.logout(authData.authToken()));
    }

    @Test
    void viewGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(1, null, authData.authToken()));
    }

    @Test
    void joinGameBadTeam() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(1, "REPUBLICAN", authData.authToken()));
    }

    @Test
    void joinGameUnauthorized() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("RATS", authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(1, "WHITE", "RATS"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
