package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private final GameDAO gameDAO = new SqlGameDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("epicTestingChessGame!");
        assertTrue(gameID > 0, "Game ID should be a positive number");

        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game, "Game should be found in the database");
        assertEquals("epicTestingChessGame!", game.gameName());
        assertNotNull(game.game(), "ChessGame object should be deserialized from JSON");
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        GameData game = gameDAO.getGame(99999);
        assertNull(game, "Non-existent game ID should return null");
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");

        var games = gameDAO.listGames();
        assertEquals(2, games.size(), "List should contain 2 games");
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("MyGame");
        GameData original = gameDAO.getGame(gameID);

        GameData updated = new GameData(
                original.gameID(),
                "whiteUser",
                "blackUser",
                original.gameName(),
                original.game()
        );

        gameDAO.updateGame(updated);

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("whiteUser", retrieved.whiteUsername());
        assertEquals("blackUser", retrieved.blackUsername());
    }

    @Test
    public void clearGames() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty(), "Game table should be empty after clear");
    }
}