package passoff.server;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearSuccess() throws Exception {
        // fill in the fields
        userDAO.createUser(new UserData("testUser", "pass", "email@byu.edu"));
        AuthData auth = authDAO.createAuth("testUser");
        int gameID = gameDAO.createGame("Test Chess Game");

        assertNotNull(userDAO.getUser("testUser"));
        assertNotNull(authDAO.getAuth(auth.authToken()));
        assertNotNull(gameDAO.getGame(gameID));

        clearService.clear();

        // make sure everything was actually cleared out
        assertNull(userDAO.getUser("testUser"));
        assertNull(authDAO.getAuth(auth.authToken()));
        assertNull(gameDAO.getGame(gameID));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}