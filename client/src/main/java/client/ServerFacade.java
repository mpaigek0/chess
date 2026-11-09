package client;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;



public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
    }

    public record UserData(String username, String password, String email, String authToken){}
    public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game){}
    public record ListGamesResult(GameData[] games){}
    public record CreateGameRequest(String gameName){}
    public record CreateGameResult(int gameID){}

    public UserData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var body = Map.of("username", username, "password", password, "email", email);
        return this.makeRequest("POST", path, body, UserData.class, null);
    }

    public UserData login(String username, String password) throws ResponseException{
        var path = "/session";
        var body = Map.of("username", username, "password", password);
        return this.makeRequest("POST", path, body, UserData.class, null);
    }

    public void logout(String authToken) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }
}
