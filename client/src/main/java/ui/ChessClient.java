package ui;

import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

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
                case "signin" -> signIn(params);
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        return "";
    }

    public String register(String... params) throws ResponseException {
        return "";
    }

    public String signIn(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
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
                    - creategame <BATTLENAME> (reserve a time in the arena)
                    - listgames (produce a list of ongoing battles)
                    - joingame <ID> <COLOR(chess)> (enter the arena)
                    - viewgame <ID> (spectate a match)
                """;
    }
}