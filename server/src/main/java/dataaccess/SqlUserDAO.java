package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO{
    @Override
    public void createUser(UserData u) throws DataAccessException{
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());

        storeUserInDataBase(u.username(), hashedPassword, u.email());
    }

    private void storeUserInDataBase(String username, String hashedPassword, String email) throws DataAccessException{
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Couldn't create user");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        var statement = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
        var ps = conn.prepareStatement(statement)){
            ps.setString(1, username);
            try (var rs = ps.executeQuery()){
                if (rs.next()){
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to get user");
        }
        return null;
    }


    @Override
    public void clear() throws DataAccessException{
        var statement = "DELETE FROM user";
        try (var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)){
    ps.executeUpdate();
        } catch (SQLException e){
    throw new DataAccessException("Failed to clear users");
        }
    }
}
