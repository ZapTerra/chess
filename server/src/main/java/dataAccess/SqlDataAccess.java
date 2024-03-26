package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess{
    private int gameCount = 0;
    public SqlDataAccess() {
        try {
            configureDatabase();
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void iAmBecomeDeath() throws ResponseException {
        executeUpdate("TRUNCATE TABLE users;");
        executeUpdate("TRUNCATE TABLE tokens;");
        executeUpdate("TRUNCATE TABLE games;");
    }

    public void createUser(UserData u) throws ResponseException {
        if(getUser(u.username()) == null){
            var statement = "INSERT INTO users (username, passHash, email, json) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(u);
            executeUpdate(statement, u.username(), u.password(), u.email(), json);
        }
    }

    public UserData getUser(String u) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, u);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("json"), UserData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameData createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (gameName, json) VALUES (?, ?)";
        var game = new ChessGame();
        var json = new Gson().toJson(game);
        executeUpdate(statement, gameName, json);
        return new GameData(++gameCount, null, null, gameName, game);
    }

    public boolean joinGame(String username, String color, int gameID) throws ResponseException {
        if(color == null){
            return true;
        }
        var joinTeam = color.equals("WHITE") ? "whiteUsername" : (color.equals("BLACK") ? "blackUsername" : "unknownTeam");
        String currentTeamPlayer = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername FROM games WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentTeamPlayer = rs.getString(joinTeam);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        if(currentTeamPlayer == null || currentTeamPlayer.isEmpty()){
            var statement = """
                UPDATE games
                SET %s = ?
                WHERE gameID = ?;
            """.formatted(joinTeam);
            executeUpdate(statement, username, gameID);
            return true;
        }
        return false;
    }

    public GetGameResponse getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new GetGameResponse(true, gameID);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return new GetGameResponse(false, gameID);
    }

    public ArrayList<GameData> listGames() throws ResponseException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var game = readGame(rs);
                        result.add(game);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    //void updateGame() throws DataAccessException;

    public void createAuth(AuthData a) throws ResponseException {
        var statement = "INSERT INTO tokens (username, authToken, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(a);
        executeUpdate(statement, a.username(), a.authToken(), json);
    }

    public String getAuth(String a) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM tokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, a);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return "";
    }

    public boolean deleteAuth(String a) throws ResponseException {
        if(!getAuth(a).isEmpty()){
            var statement = "DELETE FROM tokens WHERE authToken = ?";
            executeUpdate(statement, a);
            return true;
        }
        return false;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS users (
          `username` varchar(256) NOT NULL,
          `passHash` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          INDEX(username)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """,
            """
        CREATE TABLE IF NOT EXISTS tokens (
          `username` varchar(256) NOT NULL,
          `authToken` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          INDEX(username)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """,
            """
        CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256) DEFAULT NULL,
          `blackUsername` varchar(256) DEFAULT NULL,
          `gameName` varchar(256),
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`gameID`),
          INDEX(gameName)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """
    };

    private void configureDatabase() throws DataAccessException, ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
