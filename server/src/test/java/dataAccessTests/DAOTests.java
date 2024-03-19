package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.SqlDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestException;
import passoffTests.testClasses.TestModels;
import server.Server;
import service.AuthService;

import java.lang.module.ResolutionException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DAOTests {
    private static final SqlDataAccess dataAccess = new SqlDataAccess();

    @Test
    public void clearServerGood() throws ResponseException {
        dataAccess.createUser(new UserData("Francis", "Password", "rats@yahoo.net"));
        Assertions.assertNotNull(dataAccess.getUser("Francis"));
        dataAccess.iAmBecomeDeath();
        Assertions.assertNull(dataAccess.getUser("Francis"));
    }

    @Test
    public void clearServerThenCheck() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        dataAccess.createUser(new UserData("Francis", "Password", "rats@yahoo.net"));
        Assertions.assertFalse(dataAccess.getUser("Francis").username().isEmpty());
    }

    @Test
    public void createAuth() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
    }

    @Test
    public void createAuthPersistent() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        var auth = dataAccess.getAuth(token);
        Assertions.assertFalse(auth.isEmpty());
    }

    @Test
    public void deleteAuth() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        dataAccess.deleteAuth(token);
    }

    @Test
    public void oldAuthCheck() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        dataAccess.deleteAuth(token);
        var auth = dataAccess.getAuth(token);
        Assertions.assertTrue(auth.isEmpty());
    }

    @Test
    public void usernameTaken() throws ResponseException {
        dataAccess.iAmBecomeDeath();

        dataAccess.createUser(new UserData("Francis", "Password2", "rats@yahoo.net"));

        dataAccess.createUser(new UserData("Francis", "Password", "mice@bing.gov"));

        Assertions.assertEquals(dataAccess.getUser("Francis").password(), "Password2");
    }

    @Test
    public void getUserFromMultiple() throws ResponseException {
        dataAccess.createUser(new UserData("Francis", "Password", "rats@yahoo.net"));
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
    }

    @Test
    public void getUser() throws ResponseException {
        dataAccess.getUser("Jeff");
    }

    @Test
    public void getExistingUser() throws ResponseException {
        dataAccess.createUser(new UserData("Francis", "Password", "rats@yahoo.net"));
        Assertions.assertNotNull(dataAccess.getUser("Francis"));
    }

    @Test
    public void getBadUser() throws ResponseException {
        dataAccess.createUser(new UserData("Francis", "Password", "rats@yahoo.net"));
        Assertions.assertNull(dataAccess.getUser("Rasputin"));
    }

    @Test
    public void logout() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        dataAccess.deleteAuth(token);
        var auth = dataAccess.getAuth(token);
        Assertions.assertTrue(auth.isEmpty());
    }

    @Test
    public void badLogout() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        assert !dataAccess.deleteAuth("yeet");
    }

    @Test
    public void createGame() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");
    }

    @Test
    public void getGame() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        dataAccess.getGame(17);
    }

    @Test
    public void getNonexistentGame() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        int gameID = dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");
        assert !dataAccess.getGame(gameID + 1).found();
    }

    @Test
    public void createGamePersistent() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        int gameID = dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");
        assert dataAccess.getGame(gameID).found();
    }

    @Test
    public void joinGame() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        var auth = dataAccess.getAuth(token);
        Assertions.assertFalse(auth.isEmpty());

        int gameID = dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");

        assert dataAccess.joinGame("Francis", "WHITE", gameID);
    }

    @Test
    public void viewGame() throws ResponseException {
        int gameID = dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");
        assert dataAccess.joinGame("Francis", null, gameID);
    }

    @Test
    public void joinGameBad() throws ResponseException {
        dataAccess.iAmBecomeDeath();
        var token = AuthService.generateNewToken();
        dataAccess.createAuth(new AuthData("Francis", token));
        var auth = dataAccess.getAuth(token);
        Assertions.assertFalse(auth.isEmpty());

        int gameID = dataAccess.createGame("international rat chess 2017 semifinal round, francis v. maurice");
        dataAccess.joinGame("Francis", "WHITE", gameID);
        assert !dataAccess.joinGame("Evil Francis", "WHITE", gameID);
    }
}
