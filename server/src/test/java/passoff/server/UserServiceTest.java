package passoff.server;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "password123", "test@byu.edu");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());

        UserData userInDAO = userDAO.getUser("testUser");
        assertNotNull(userInDAO);
        assertEquals("testUser", userInDAO.username());
    }

    @Test
    void registerAlreadyTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("existingUser", "password123", "existing@byu.edu");
        userService.register(request);

        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    void loginSuccess() throws Exception {
        RegisterRequest regReq = new RegisterRequest("loginUser", "myPassword", "login@byu.edu");
        userService.register(regReq);

        LoginRequest loginReq = new LoginRequest("loginUser", "myPassword");
        LoginResult result = userService.login(loginReq);

        assertNotNull(result);
        assertEquals("loginUser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
    }

    @Test
    void loginUnauthorizedWrongPassword() throws Exception {
        RegisterRequest regReq = new RegisterRequest("validUser", "correctPassword", "valid@byu.edu");
        userService.register(regReq);

        LoginRequest loginReq = new LoginRequest("validUser", "wrongPassword");
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginReq);
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        RegisterRequest regReq = new RegisterRequest("logoutUser", "password123", "logout@byu.edu");
        RegisterResult regResult = userService.register(regReq);

        String authToken = regResult.authToken();

        assertDoesNotThrow(() -> {
            userService.logout(authToken);
        });

        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void logoutUnauthorizedInvalidToken() {
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout("invalid-token-12345");
        });
    }
}
