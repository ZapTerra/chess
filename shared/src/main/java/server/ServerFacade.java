package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, null, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, null, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        this.makeRequest("DELETE", path, null, headers, AuthData.class);
    }

    public ChessGame[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        record ListGameResponse(ChessGame[] games) {}
        var response = this.makeRequest("GET", path, null, headers, ListGameResponse.class);
        return response.games;
    }

    public int createGame(CreateGameRequest createGameRequest, String authToken) throws  ResponseException {
        var path = "/game";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        record ListGameResponse(ChessGame[] games) {}
        return this.makeRequest("POST", path, createGameRequest, headers, Integer.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException{
        var path = "/game";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        this.makeRequest("POST", path, joinGameRequest, headers, null);
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

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
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
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
