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







}
