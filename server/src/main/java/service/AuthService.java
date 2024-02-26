package service;

public class AuthService {
    record LoginRequest(String username, String password){}
    record LoginResult(String username, String authToken) {}

}

