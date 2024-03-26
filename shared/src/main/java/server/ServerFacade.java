package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        UserData userData = new UserData(username, password, email);
        var path = "/user";
        return this.makeRequest("POST", path, userData, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, null, AuthData.class);
    }

    public boolean logout(String authToken) throws ResponseException {
        var path = "/session";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        return this.makeRequest("DELETE", path, null, headers, boolean.class);
    }

    public ChessGame[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        record ListGameResponse(ChessGame[] games) {}
        var response = this.makeRequest("GET", path, null, headers, ListGameResponse.class);
        return response.games;
    }

    public String createGame(String gameName, String authToken) throws  ResponseException {
        var path = "/game";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        return new Gson().toJson(this.makeRequest("POST", path, createGameRequest, headers, LinkedTreeMap.class));
    }

    public String joinGame(int gameID, String playerColor, String authToken) throws ResponseException{
        var path = "/game";
        JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        return new Gson().toJson(this.makeRequest("PUT", path, joinGameRequest, headers, LinkedTreeMap.class));
    }

    public <T> T makeRequest(String method, String path, Object request, Map<String, String> headers, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    http.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            //Write body
            if (request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();

            //Throw if not successful
            var status = http.getResponseCode();
            if (!(status / 100 == 2)) {
                throw new ResponseException(status, "failure: " + status);
            }

            //Read body
            T response = null;
            if (http.getContentLength() < 0) {
                try (InputStream respBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    if (responseClass != null) {
                        response = new Gson().fromJson(reader, responseClass);
                    }
                }
            }
            return response;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
