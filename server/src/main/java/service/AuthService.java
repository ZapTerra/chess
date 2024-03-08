package service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;

public class AuthService {
    private final DataAccess dataAccess;
    record LoginRequest(String username, String password){}
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData createUser(Request req, Response res) throws DataAccessException, ResponseException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        if(userData == null || userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            res.body("Error: bad request");
            return null;
        }
        var existingUser = dataAccess.getUser(userData.username());
        if(existingUser != null){
            res.status(403);
            res.body("Error: already taken");
            return null;
        }
        dataAccess.createUser(userData);
        var auth = addAuth(userData.username());
        res.status(200);
        res.body(new Gson().toJson(auth));
        return auth;
    }

    public AuthData login(Request req, Response res) throws DataAccessException, ResponseException {
        var loginData = new Gson().fromJson(req.body(), LoginRequest.class);
        if(loginData == null || loginData.username() == null || loginData.password() == null){
            res.status(400);
            res.body("Error: bad request");
            return null;
        }
        var existingUser = dataAccess.getUser(loginData.username());
        if(existingUser == null || !Objects.equals(existingUser.password(), loginData.password())){
            res.status(401);
            res.body("Error: unauthorized");
            return null;
        }

        var auth = addAuth(loginData.username);
        res.status(200);
        res.body(new Gson().toJson(auth));
        return auth;
    }

    public void logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        if(auth == null){
            res.status(500);
            res.body("Error: bad request");
        }else if(dataAccess.deleteAuth(auth)){
            res.status(200);
        }else{
            res.status(401);
            System.out.println("unauthorized");
            res.body("Error: unauthorized");
        }
        res.body();
    }

    private AuthData addAuth(String username) throws DataAccessException, ResponseException {
        var token = generateNewToken();
        var data = new AuthData(username, token);
        dataAccess.createAuth(data);
        return data;
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
