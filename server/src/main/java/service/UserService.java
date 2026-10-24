package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException, BadRequestException, AlreadyTakenException{
        if (req.username() == null || req.password() == null || req.email() == null
        || req.username().isEmpty() || req.password().isEmpty() || req.email().isEmpty()){
            throw new BadRequestException("Error: bad request");
        }

        if(userDAO.getUser(req.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }

        UserData newUser = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(newUser);

        AuthData auth = authDAO.createAuth(req.username());
        return new RegisterResult(auth.username(), auth.authToken());
    }


    public LoginResult login(LoginRequest req) throws DataAccessException, BadRequestException, UnauthroizedException{
        if (req.username() == null || req.password() == null){
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(req.username());
        if (user == null || !user.password().equals(req.password())){
            throw new UnauthroizedException("Error: unauthorized");
        }

        AuthData auth = authDAO.createAuth(req.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public void logout(String authToken) throws DataAccessException, UnauthroizedException{
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthroizedException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}

