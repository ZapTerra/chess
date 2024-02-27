package service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;

import com.google.gson.Gson;
import dataAccess.*;
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

    public AuthData createUser(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        System.out.println(userData);
        if(userData == null || userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            res.body("Error: bad request");
            return null;
        }
        var existingUser = dataAccess.getUser(userData);
        if(existingUser != null){
            res.status(403);
            res.body("Error: already taken");
            return null;
        }
        var auth = new AuthData(userData.username(), generateNewToken());
        dataAccess.createUser(userData, auth);
        res.status(200);
        res.body(new Gson().toJson(auth));
        return auth;
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
