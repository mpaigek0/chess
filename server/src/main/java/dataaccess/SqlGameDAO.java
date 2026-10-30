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




}
