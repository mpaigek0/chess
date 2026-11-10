package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException{
        facade.clear();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        Assertions.assertNotNull(userData.authToken());
        Assertions.assertTrue(userData.authToken().length() >5); // check
    }

    @Test
    public void registerDuplicate() throws Exception{
        facade.register("player1", "password", "player1@gmail.com");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("player1", "password", "player1@gmail.com");
        });
    }

    @Test
    public void loginValid() throws Exception{
        facade.register("player1", "password", "player1@gmail.com");
        var userData = facade.login("player1", "password");
        Assertions.assertNotNull(userData.authToken());
    }

    @Test
    public void loginInValid() throws Exception{
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("NotRealPlayer", "WrongPassword");
        });
    }

    @Test
    public void logoutValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        Assertions.assertDoesNotThrow(()->{
            facade.logout(userData.authToken());
        });
    }

    @Test
    public void logoutInValid() throws Exception{
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout("this-isn't-a-logout!!");
        });
    }


    @Test
    public void createGameValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        int gameID = facade.createGame("Oh yeah testing chess game", userData.authToken());
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameInValid() throws Exception{
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.createGame("this game is evil", "evil token");
        });
    }

    @Test
    public void listGamesValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        facade.createGame("Super cool test game", userData.authToken());
        var games = facade.listGames(userData.authToken());
        Assertions.assertEquals(1, games.length);
        Assertions.assertEquals("Super cool test game", games[0].gameName());
    }

    @Test
    public void listGamesInValid() throws Exception{
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames("not valid game");
        });
    }

    @Test
    public void joinGameValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        int gameID = facade.createGame("Super cool test game", userData.authToken());
        Assertions.assertDoesNotThrow(()->{
            facade.joinGame("WHITE", gameID, userData.authToken());
        });
    }

    @Test
    public void joinGameInValid() throws Exception{
        var userData = facade.register("player1", "password", "player1@gmail.com");
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.joinGame("WHITE", 999999999, userData.authToken());
        });
    }

}
