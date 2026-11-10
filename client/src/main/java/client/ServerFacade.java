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
        HttpURLConnection http = openConnection("POST", path, null);
        writeBody(http, body);
        checkResponse(http);
        return readResponse(http, UserData.class);
    }

    public UserData login(String username, String password) throws ResponseException{
        var path = "/session";
        var body = Map.of("username", username, "password", password);

        HttpURLConnection http = openConnection("POST", path, null);
        writeBody(http, body);
        checkResponse(http);
        return readResponse(http, UserData.class);
    }

    public void logout(String authToken) throws ResponseException{
        var path = "/session";
        HttpURLConnection http = openConnection("DELETE", path, authToken);
        checkResponse(http);
    }

    public int createGame(String gameName, String authToken) throws ResponseException{
        var path = "/game";
        var body = new CreateGameRequest(gameName);

        HttpURLConnection http = openConnection("POST", path, authToken);
        writeBody(http, body);
        checkResponse(http);
        CreateGameResult result = readResponse(http, CreateGameResult.class);
        return result.gameID();
    }

    public GameData[] listGames(String authToken) throws ResponseException{
        var path = "/game";
        HttpURLConnection http = openConnection("GET", path, authToken);
        checkResponse(http);
        ListGamesResult result = readResponse(http, ListGamesResult.class);
        return result.games() != null ? result.games() : new GameData[0];
    }

    public void joinGame(String playerColor, int gameID, String authToken) throws ResponseException{
        var path = "/game";
        Object body;
        if(playerColor == null || playerColor.isEmpty()){
            body = Map.of("gameID", gameID);
        } else {
            body = Map.of("playerColor", playerColor, "gameID", gameID);
        }
        HttpURLConnection http = openConnection("PUT", path, authToken);
        writeBody(http, body);
        checkResponse(http);
    }

    public void clear() throws ResponseException{
        var path = "/db";
        HttpURLConnection http = openConnection("DELETE", path, null);
        checkResponse(http);
    }

    private HttpURLConnection openConnection(String method, String path, String authToken) throws ResponseException{
        try{
            URL url = new URI(serverUrl+path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if(authToken != null && !authToken.isEmpty()){
                http.addRequestProperty("authorization", authToken);
            }

            if(method.equals("POST") || method.equals("PUT")){
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
            }
            return http;
        } catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void writeBody(HttpURLConnection http, Object body) throws ResponseException{
        try{
            String jsonBody = new Gson().toJson(body);
            try(var outputStream = http.getOutputStream()){
                outputStream.write(jsonBody.getBytes());
            }
        } catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void checkResponse(HttpURLConnection http) throws ResponseException{
        try{
            int status = http.getResponseCode();
            if(status < 200 || status >= 300){
                throw new ResponseException(status, "Error: Status " + status);
            }
        } catch (ResponseException e){
            throw e;
        } catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    private <T> T readResponse(HttpURLConnection http, Class<T> responseClass) throws ResponseException{
        try(var responseBod = http.getInputStream()){
            InputStreamReader reader = new InputStreamReader(responseBod);
            return new Gson().fromJson(reader, responseClass);
        } catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }


    }


