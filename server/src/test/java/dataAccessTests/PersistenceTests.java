package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.SqlDataAccess;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestException;
import passoffTests.testClasses.TestModels;
import server.Server;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PersistenceTests {

    private static TestServerFacade serverFacade;
    private static Server server;


    @BeforeAll
    public static void init() {
        startServer();
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    public static void startServer(){
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @Test
    @DisplayName("Persistence Test")
    public void persistenceTest() throws TestException {
        var initialRowCount = getDatabaseRows();

        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;

        //create a game
        TestModels.TestCreateRequest createRequest = new TestModels.TestCreateRequest();
        createRequest.gameName = "Test Game";
        TestModels.TestCreateResult createResult = serverFacade.createGame(createRequest, auth);

        //join the game
        TestModels.TestJoinRequest joinRequest = new TestModels.TestJoinRequest();
        joinRequest.gameID = createResult.gameID;
        joinRequest.playerColor = ChessGame.TeamColor.WHITE;
        serverFacade.verifyJoinPlayer(joinRequest, auth);

        Assertions.assertTrue(initialRowCount < getDatabaseRows(), "No new data added to database");

        // Test that we can read the data after a restart
        stopServer();
        startServer();

        //list games using the auth
        TestModels.TestListResult listResult = serverFacade.listGames(auth);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Server response code was not 200 OK");
        Assertions.assertEquals(1, listResult.games.length, "Missing game(s) in database after restart");

        TestModels.TestListResult.TestListEntry game1 = listResult.games[0];
        Assertions.assertEquals(game1.gameID, createResult.gameID);
        Assertions.assertEquals(createRequest.gameName, game1.gameName, "Game name changed after restart");
        Assertions.assertEquals(registerRequest.username, game1.whiteUsername,
                "White player username changed after restart");

        //test that we can still log in
        TestModels.TestLoginRequest loginRequest = new TestModels.TestLoginRequest();
        loginRequest.username = registerRequest.username;
        loginRequest.password = registerRequest.password;
        serverFacade.login(loginRequest);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Unable to login");
    }

    @Test
    public void clearServerGood() throws TestException {
        serverFacade.clear();
        Assertions.assertEquals(0, getDatabaseRows());
    }

    @Test
    public void clearServerNotBad() throws TestException {
        serverFacade.clear();
        Assertions.assertFalse(getDatabaseRows() > 0);
    }

    @Test
    public void createUser() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;
        System.out.println(auth);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void usernameTaken() throws TestException {
        serverFacade.clear();

        TestModels.TestRegisterRequest registerRequest1 = new TestModels.TestRegisterRequest();
        registerRequest1.username = "ExistingUser";
        registerRequest1.password = "existingUserPassword";
        registerRequest1.email = "eu@mail.com";

        serverFacade.register(registerRequest1);

        TestModels.TestRegisterRequest registerRequest2 = new TestModels.TestRegisterRequest();
        registerRequest2.username = "ExistingUser";
        registerRequest2.password = "existingUserPassword";
        registerRequest2.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest2);
        var auth = regResult.authToken;
        System.out.println(auth);
        Assertions.assertNull(auth);
    }

    @Test
    public void login() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;
        TestModels.TestLoginRequest loginRequest = new TestModels.TestLoginRequest();
        loginRequest.username = "ExistingUser";
        loginRequest.password = "existingUserPassword";
        Assertions.assertNotNull(serverFacade.login(loginRequest).authToken);
    }

    @Test
    public void badLogin() throws TestException {
        TestModels.TestLoginRequest loginRequest = new TestModels.TestLoginRequest();
        loginRequest.username = "FakeUser";
        loginRequest.password = "notPassword";
        Assertions.assertNull(serverFacade.login(loginRequest).authToken);
    }

    @Test
    public void getBadUser() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;
        System.out.println(auth);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void logout() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";

        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;
        Assertions.assertNull(serverFacade.logout(auth).message);
    }

    @Test
    public void badLogout() throws TestException {
        serverFacade.clear();
        assert !serverFacade.logout("bad").message.isEmpty();
    }

    @Test
    public void createGame() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";
        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;

        TestModels.TestCreateRequest createGameRequest = new TestModels.TestCreateRequest();
        createGameRequest.gameName = "game";
        serverFacade.createGame(createGameRequest, auth);
        assert Objects.equals(serverFacade.listGames(auth).games[0].gameName, "game");
    }

    @Test
    public void createGameBadAuth() throws TestException {
        serverFacade.clear();
        TestModels.TestCreateRequest createGameRequest = new TestModels.TestCreateRequest();
        String auth = "bad";
        createGameRequest.gameName = "game";
        serverFacade.createGame(createGameRequest, auth);
        Assertions.assertNull(serverFacade.listGames(auth).games);
    }

    @Test
    public void joinGame() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";
        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;

        TestModels.TestCreateRequest createGameRequest = new TestModels.TestCreateRequest();
        createGameRequest.gameName = "game";
        serverFacade.createGame(createGameRequest, auth);

        TestModels.TestJoinRequest joinGameRequest = new TestModels.TestJoinRequest();
        joinGameRequest.gameID = serverFacade.listGames(auth).games[0].gameID;
        joinGameRequest.playerColor = ChessGame.TeamColor.WHITE;
        Assertions.assertNull(serverFacade.verifyJoinPlayer(joinGameRequest, auth).message);
    }

    @Test
    public void viewGame() throws TestException {
        serverFacade.clear();
        TestModels.TestRegisterRequest registerRequest = new TestModels.TestRegisterRequest();
        registerRequest.username = "ExistingUser";
        registerRequest.password = "existingUserPassword";
        registerRequest.email = "eu@mail.com";
        TestModels.TestLoginRegisterResult regResult = serverFacade.register(registerRequest);
        var auth = regResult.authToken;

        TestModels.TestCreateRequest createGameRequest = new TestModels.TestCreateRequest();
        createGameRequest.gameName = "game";
        serverFacade.createGame(createGameRequest, auth);

        TestModels.TestJoinRequest joinGameRequest = new TestModels.TestJoinRequest();
        joinGameRequest.gameID = serverFacade.listGames(auth).games[0].gameID;
        joinGameRequest.playerColor = null;
        Assertions.assertNull(serverFacade.verifyJoinPlayer(joinGameRequest, auth).message);
    }

    @Test
    public void joinGameBad() throws TestException {
        String auth = "bad";
        TestModels.TestJoinRequest joinGameRequest = new TestModels.TestJoinRequest();
        joinGameRequest.gameID = 0;
        joinGameRequest.playerColor = ChessGame.TeamColor.WHITE;
        Assertions.assertNotNull(serverFacade.verifyJoinPlayer(joinGameRequest, auth).message);
    }

    private int getDatabaseRows() {
        int rows = 0;
        try {
            Class<?> clazz = Class.forName("dataAccess.DatabaseManager");
            Method getConnectionMethod = clazz.getDeclaredMethod("getConnection");
            getConnectionMethod.setAccessible(true);

            Object obj = clazz.getDeclaredConstructor().newInstance();
            try (Connection conn = (Connection) getConnectionMethod.invoke(obj);) {
                try (var statement = conn.createStatement()) {
                    for (String table : getTables(conn)) {
                        var sql = "SELECT count(*) FROM " + table;
                        try (var resultSet = statement.executeQuery(sql)) {
                            if (resultSet.next()) {
                                rows += resultSet.getInt(1);
                            }
                        }
                    }
                }

            }
        } catch (Exception ex) {
            Assertions.fail("Unable to load database in order to verify persistence. Are you using dataAccess.DatabaseManager to set your credentials?");
        }

        return rows;
    }

    private List<String> getTables(Connection conn) throws SQLException {
        String sql = """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = DATABASE();
                """;

        List<String> tableNames = new ArrayList<>();
        try (var preparedStatement = conn.prepareStatement(sql)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    tableNames.add(resultSet.getString(1));
                }
            }
        }

        return tableNames;
    }

}
