package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.SqlUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private final UserDAO userDAO = new SqlUserDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("player1", "password123", "p1@byu.edu");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("player1");
        assertNotNull(retrieved, "User should be found in the database");
        assertEquals("player1", retrieved.username());
        assertEquals("p1@byu.edu", retrieved.email());
        assertNotEquals("password123", retrieved.password(), "Password should be hashed");
    }

    @Test
    public void createUserDuplicate() throws DataAccessException {
        UserData user = new UserData("player1", "password123", "p1@byu.edu");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        UserData retrieved = userDAO.getUser("nonexistent");
        assertNull(retrieved, "Getting a non-existent user should return null");
    }

    @Test
    public void clearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "pass", "email@byu.edu"));
        userDAO.clear();
        assertNull(userDAO.getUser("player1"), "User table should be empty after clear");
    }
}