package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO implements GameDAO {

    @Override
    public int createGame(String gameName) throws DataAccessException{
        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();
        String jsonGame = new Gson().toJson(newGame);

        var statement = "INSERT INTO game (gameName, game) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, gameName);
            ps.setString(2, jsonGame);
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    return rs.getInt(1)
;                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Couldn't create game");
        }
        throw new DataAccessException("Failed to get game from given ID")
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)){
            ps.setInt(1, gameID);
            try(var rs = ps.executeQuery()){
                if(rs.next()){
                    return readGameData(rs); // return the found game
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Couldn't get game ID");
        }
        return null; // couldn't find the game
    }


    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        Collection <GameData> games = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement); // ps --> prepare statement
             var rs = ps.executeQuery()) { // rs --> result statement
            while (rs.next()){
                games.add(readGameData(rs));
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to list games");
        }
        return games;
    }


    @Override
    public void updateGame(GameData gameData) throws DataAccessException{
        // need to turn the chessgame into a json
        String jsonGame = new Gson().toJson(gameData.game());

        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)){
            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, jsonGame);
            ps.setInt(4, gameData.gameID());
            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to update game ID");
        }
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "DELETE FROM game";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to clear game");
        }
    }




}
