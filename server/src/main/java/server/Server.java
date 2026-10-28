package server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import dataaccess.*;
import service.*;

public class Server {

    private final Javalin javalin;

    // initialize the DAOs and services for the game
    private final UserDAO userDAO = new SqlUserDAO(); // these change from memory to SQL for phase 4
    private final AuthDAO authDAO = new SqlAuthDAO();
    private final GameDAO gameDAO = new SqlGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    private void clear(Context context) {
        try {
            clearService.clear();
            context.status(200);
            context.result("{}");
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void register(Context context) {
        try {
            var serializer = new Gson();
            RegisterRequest req = serializer.fromJson(context.body(), RegisterRequest.class);
            RegisterResult result = userService.register(req);
            context.status(200);
            context.result(serializer.toJson(result));
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void login(Context context) {
        try {
            var serializer = new Gson();
            LoginRequest req = serializer.fromJson(context.body(), LoginRequest.class);
            LoginResult result = userService.login(req);
            context.status(200);
            context.result(serializer.toJson(result));
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void logout(Context context) {
        try {
            String authToken = context.header("authorization");
            userService.logout(authToken);
            context.status(200);
            context.result("{}");
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void listGames(Context context) {
        try {
            String authToken = context.header("authorization");
            ListGamesResult result = gameService.listGames(authToken);
            context.status(200);
            context.result(new Gson().toJson(result));
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void createGame(Context context) {
        try {
            String authToken = context.header("authorization");
            var serializer = new Gson();
            CreateGameRequest req = serializer.fromJson(context.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, req);
            context.status(200);
            context.result(serializer.toJson(result));
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void joinGame(Context context) {
        try {
            String authToken = context.header("authorization");
            var serializer = new Gson();
            JoinGameRequest req = serializer.fromJson(context.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, req);
            context.status(200);
            context.result("{}");
        } catch (Exception e) {
            handleException(context, e);
        }
    }

    private void handleException(Context context, Exception e) {
        var serializer = new Gson();
        record ErrorResponse(String message) {}

        if (e instanceof BadRequestException) {
            context.status(400);
        } else if (e instanceof UnauthorizedException) {
            context.status(401);
        } else if (e instanceof AlreadyTakenException) {
            context.status(403);
        } else {
            context.status(500);
        }
        context.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
    }

    public int run(int desiredPort) {
        try{
            DatabaseManager.configureDataBase();
        } catch (DataAccessException e){
            System.err.println("Can't start database");
        }

        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}