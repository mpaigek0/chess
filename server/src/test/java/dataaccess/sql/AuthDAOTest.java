package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    private final AuthDAO authDAO = new SqlAuthDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("player1");
        assertNotNull(auth.authToken(), "Auth token should be created");
        assertEquals("player1", auth.username());

        AuthData retrieved = authDAO.getAuth(auth.authToken());
        assertNotNull(retrieved, "Auth token not able to be gotten");
        assertEquals(auth.authToken(), retrieved.authToken());
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("null_token");
        assertNull(retrieved, "Non-existent auth token should return null");
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("player1");
        authDAO.deleteAuth(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()), "Deleted auth token should no longer exist");
    }

    @Test
    public void clearAuth() throws DataAccessException {
        AuthData auth = authDAO.createAuth("player1");
        authDAO.clear();
        assertNull(authDAO.getAuth(auth.authToken()), "Auth table should be empty after clear");
    }
}