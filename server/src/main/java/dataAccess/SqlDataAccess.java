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
    public SqlDataAccess() {
        try {
            configureDatabase();
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void iAmBecomeDeath() throws ResponseException {
        var statement = """
            TRUNCATE users
            TRUNCATE tokens
            TRUNCATE games
        """;
        executeUpdate(statement);
    }

    public void createUser(UserData u) throws ResponseException {
        var statement = "INSERT INTO users (username, passHash, email, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(u);
        executeUpdate(statement, u.username(), u.password(), u.email(), json);
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

    public int createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, json) VALUES (null, null, ?, ?)";
        var json = new Gson().toJson(new ChessGame());
        return executeUpdate(statement, gameName, json);
    }

    public boolean joinGame(String username, String color, int gameID) throws ResponseException {
        if(color == null){
            return true;
        }
        var joinTeam = (Objects.equals(color, "WHITE") ? "whiteUsername" : (Objects.equals(color, "BLACK") ? "blackUsername" : "unknownTeam"));
        String currentTeamPlayer = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT ? FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, joinTeam);
                ps.setInt(2, gameID);
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
                SET
                    ? = '?'
                WHERE
                    gameID = ?;
            """;
            executeUpdate(statement, joinTeam, username, gameID);
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
        return null;
    }

    public HashMap<Integer, GameData> listGames() throws ResponseException {
        var result = new HashMap<Integer, GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var game = readGame(rs);
                        result.put(rs.getInt("gameID"), game);
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

    public String getAuth(AuthData a) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM tokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, a.authToken());
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM tokens WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, a);
                try (var rs = ps.executeQuery()) {
                    return rs.rowUpdated();
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
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
              INDEX(username),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            CREATE TABLE IF NOT EXISTS tokens (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              INDEX(username),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
