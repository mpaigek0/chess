package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthroizedException{
        verifyAuth(authToken);
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws DataAccessException, UnauthroizedException, BadRequestException{
        verifyAuth(authToken);
        if(req.gameName() == null || req.gameName().isEmpty()){
            throw new BadRequestException("Error: bad request");
        }
        int gameID = gameDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest req) throws DataAccessException, UnauthroizedException, BadRequestException, AlreadyTakenException{
        AuthData auth = verifyAuth(authToken);
        if (req == null || req.gameID() <= 0){
            throw new BadRequestException("Error: bad request");
        }
        GameData game = gameDAO.getGame(req.gameID());
        if (game == null){
            throw new BadRequestException("Error: bad request");
        }
        String playerColor = req.playerColor();
        if(playerColor == null || playerColor.isEmpty()){
            throw new BadRequestException("Error: bad request");
        }

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (playerColor.equalsIgnoreCase("WHITE")){
            if(whiteUser != null && !whiteUser.isEmpty()){
                throw new AlreadyTakenException("Error: already taken");
            }
            whiteUser = auth.username();
        }
        else if(playerColor.equalsIgnoreCase("BLACK")){
            if(blackUser != null && !blackUser.isEmpty()){
                throw new AlreadyTakenException("Error: already taken");
            }
            blackUser = auth.username();
        }
        else{
            throw new BadRequestException("Error: bad request");
        }
        GameData updatedGame = new GameData(game.gameID(), whiteUser, blackUser, game.gameName(), game.game());
        gameDAO.updateGame(updatedGame);
    }

    private AuthData verifyAuth(String authToken) throws DataAccessException, UnauthroizedException{
        if(authToken == null || authToken.isEmpty()){
            throw new UnauthroizedException("Error: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new UnauthroizedException("Error: unauthorized");
        }
        return auth;
    }
}
