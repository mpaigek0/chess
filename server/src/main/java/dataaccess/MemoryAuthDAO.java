package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private Map<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        auths.put(authToken, authData);
        return authData;
    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        return auths.get(authToken);
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        auths.remove(authToken);
    }


    @Override
    public void clear() throws DataAccessException{
        auths.clear();
    }
}
