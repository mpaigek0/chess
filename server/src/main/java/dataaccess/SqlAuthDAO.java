package dataaccess;

import model.AuthData;
import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO{

    @Override
    public AuthData createAuth(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);

        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)){
            ps.setString(1, authData.authToken());
            ps.setString(2, authData.username());
            ps.executeUpdate();
            return authData;
        }
        catch(SQLException e){
            throw new DataAccessException("Failed to create auth token");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()){
                if (rs.next()){
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Failed to get auth token");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM auth WHERE authToken = ?";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Failed to delete auth token");
        }
    }


    @Override
    public void clear() throws DataAccessException{
        var statement = "DELETE FROM auth";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Failed to clear auth");
        }
    }







}
