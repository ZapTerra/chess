package service;

import java.util.Collection;
import dataAccess.*;

public class AuthService {
    private final DataAccess dataAccess;
    record LoginRequest(String username, String password){}

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
