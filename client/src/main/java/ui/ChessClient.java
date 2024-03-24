package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.mysql.cj.log.Log;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import exception.ResponseException;
import server.ServerFacade;
import model.*;

import java.util.Arrays;

public class ChessClient {
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private String authToken = "";

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout(params);
                case "creategame" -> createGame(params);
                case "listgames" -> listGames(params);
                case "joingame" -> joinGame(params);
                case "viewgame" -> viewGame(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if(state.equals(State.SIGNEDIN)){
            return "You're already inside the arena!";
        }
        if (params.length == 2) {
            authToken = server.login(params[0], params[1]).authToken();
            state = State.SIGNEDIN;
            return "Welcome to the arena.";
        }
        return "Expects: login <username> <password>";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            authToken = server.register(params[0], params[1], params[2]).authToken();
            state = State.SIGNEDIN;
            return "Registered!1";
        }
        return "Expects: login <username> <password> <email>";
    }

    public String logout(String... params) throws ResponseException {
        if(server.logout(authToken)) {
            return "Signed out.";
        }
        return "Y'ain't signed in";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            return server.createGame(params[0], authToken);
        }
        return "Expects: creategame <BATTLENAME>";
    }

    public String listGames(String... params) throws ResponseException {
        return new Gson().toJson(server.listGames(authToken));
    }

    public String joinGame(String... params) throws ResponseException {
        if(params.length == 2){
            PrintChessboard.printChessboard(true);
            PrintChessboard.printChessboard(false);
            return server.joinGame(Integer.parseInt(params[0]), params[1], authToken);
        }
        return "Expects: joinGame <ID> <COLOR(chess)>";
    }

    public String viewGame(String... params) throws ResponseException {
        if(params.length == 1){
            return server.joinGame(Integer.parseInt(params[0]), null, authToken);
        }
        return "Expects: viewGame <ID>";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
        return """
                    Y'roue valid moves:
                    - help (I will render assistance)
                    - quit (There is no way back. The only escape is death.)
                    - login <USER> <PASS> (Request admittance)
                    - register <USER> <PASS> <EMAIL> (Become an officially recognized gladiator)
                """;
        }
        return """
                    - help (you should know this by now)
                    - logout (rest before your next battle)
                    - creategame <BATTLENAME> (reserve an arena timeslot)
                    - listgames (produce a list of ongoing battles)
                    - joingame <ID> <COLOR(chess)> (enter the arena)
                    - viewgame <ID> (spectate a match)
                """;
    }
}
