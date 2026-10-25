package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private UserService userService;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        userService.register(new RegisterRequest(
                "testUser",
                "password123",
                "test@byu.edu"));
        LoginResult loginResult = userService.login(new LoginRequest(
                "testUser",
                "password123"));
        authToken = loginResult.authToken();
    }

    @Test
    void listGamesSuccess() throws Exception {
        gameDAO.createGame("Game One");
        gameDAO.createGame("Game Two");

        ListGamesResult result = gameService.listGames(authToken);

        assertNotNull(result);
        Collection<GameData> games = result.games();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("invalid-token-xyz");
        });
    }

    @Test
    void createGameSuccess() throws Exception {
        CreateGameRequest req = new CreateGameRequest("Chess Game!!!!");
        CreateGameResult result = gameService.createGame(authToken, req);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);

        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("Chess Game!!!!", game.gameName());
    }

    @Test
    void createGameBadRequest() {
        CreateGameRequest req = new CreateGameRequest("");
        assertThrows(BadRequestException.class, () -> {
            gameService.createGame(authToken, req);
        });
    }

    @Test
    void joinGameSuccess() throws Exception {
        int gameID = gameDAO.createGame("Match to Join");
        JoinGameRequest req = new JoinGameRequest("WHITE", gameID);

        assertDoesNotThrow(() -> {
            gameService.joinGame(authToken, req);
        });

        GameData game = gameDAO.getGame(gameID);
        assertEquals("testUser", game.whiteUsername());
    }

    @Test
    void joinGameAlreadyTaken() throws Exception {
        int gameID = gameDAO.createGame("Taken Match");
        JoinGameRequest reqWhite = new JoinGameRequest("WHITE", gameID);

        //user takes white
        gameService.joinGame(authToken, reqWhite);

        // 2nd user joins in
        userService.register(new RegisterRequest(
                "secondUser",
                "pass456",
                "second@byu.edu"));
        LoginResult secondLogin = userService.login(new LoginRequest(
                "secondUser",
                "pass456"));

        // 2nd user also tries to take white
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(secondLogin.authToken(), reqWhite);
        });
    }
}