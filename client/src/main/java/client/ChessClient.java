
package client;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import ui.BoardRender;


import chess.*;

public class ChessClient{
    private final ServerFacade facade;
    private String authToken = null;
    private State state = State.SIGNED_OUT;

    private final Map<Integer, ServerFacade.GameData> gameMap = new HashMap<>();

    private enum State {
        SIGNED_OUT,
        SIGNED_IN
    }

    public ChessClient(int port){
        facade = new ServerFacade(port);
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 240 chess. Type help to get started");

        String result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e){
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printPrompt(){
        if (state == State.SIGNED_OUT){
            System.out.print("\n[LOGGED_OUT] >>> ");
        }
        else{
            System.out.print("\n[LOGGED_IN] >>> ");
        }
    }

    public String eval(String input){
        var tokens = input.trim().split("\\s+");
        var cmd = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        var params = java.util.Arrays.copyOfRange(tokens, 1, tokens.length);

        if(cmd.equals("help")){
            return showHelp(); // should display text informing user of available actions
        }

        if(state == State.SIGNED_OUT){
            return switch (cmd){
                case "quit" -> "quit"; // exit the program
                case "login" -> login(params); // prompt the user for login creds. Once successful -> postlogin UI
                case "register" -> register(params); // prompts the user for registration information
                default -> "Unrecognized command. Type 'help' to see valid commands";
            };
        } else{
            return switch (cmd){
                case "quit" -> "quit";
                case "logout" -> logout(); // logs out the user via the server api and transitions client back to prelogin UI
                case "create" -> createGame(params); // prompts the user for a game name and calls the server create API. This creates the game on the server but doesn't auto join
                case "list" -> listGames(); // lists all existing games on the server, in a numbered list w/ game name & current players
                case "join" -> joinGame(params); // allows the user to join a game by specifying the list number & their desired color
                case "observe" -> observeGame(params); // allows the user to specify a game to observe using the list number
                default -> "Unrecognized command. Type 'help' to see valid commands";

            };
        }
    }

    private String showHelp(){
        if (state == State.SIGNED_OUT){
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
        }
        else{
            return """
                    - create <NAME>
                    - list
                    - join <ID> [WHITE|BLACK]
                    - observe <ID>
                    - logout
                    - quit
                    - help
                    """;
        }
    }

    private String grandmaErrors(client.ResponseException e){
        String msg = e.getMessage().toLowerCase();
        if (msg.contains("401")){
            return "That username or password is incorrect!";
        }
        else if (msg.contains("403")){
            return "That username is already taken";
        }
        else if (msg.contains("404")){
            return "That couldn't be found!";
        }
        else if (msg.contains("500")){
            return "Our server had an error, try again later";
        }
        return e.getMessage();
    }


    private String register(String[] params){
        if(params.length < 3){
            return "Register expects: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            var userData = facade.register(params[0], params[1], params[2]);
            authToken = userData.authToken();
            state = State.SIGNED_IN;
            return "Successfully registered in as " + userData.username();
        } catch(client.ResponseException e){
            return "Failed to register: " + e.getMessage();
        }
    }


    private String login(String[] params){
        if(params.length <2){
            return "Login expects: login <USERNAME> <PASSWORD>";
        }
        try {
            var userData = facade.login(params[0], params[1]);
            authToken = userData.authToken();
            state = State.SIGNED_IN;
            return "Successfully logged in as " + userData.username();
        } catch(client.ResponseException e){
            return "Failed to login: " + e.getMessage();
        }
    }

    private String logout(){
        try{
            facade.logout(authToken);
            authToken = null;
            state = State.SIGNED_OUT;
            gameMap.clear();
            return "Logged out successfully";
        } catch (client.ResponseException e){
            return "Failed to logout: " + e.getMessage();
        }
    }

    private String createGame(String[] params){
        if (params.length < 1){
            return "Expected: create <NAME>";
        }
        try {
            facade.createGame(params[0], authToken);
            return "Game " + params[0] + " created successfully!";
        } catch (client.ResponseException e){
            return "Couldn't create game: " + e.getMessage() + "\n";
        }
    }

    private String listGames(){
        try{
            var games = facade.listGames(authToken);
            gameMap.clear();

            if (games.length == 0){
                return "there are no available games right now\n";
            }

            StringBuilder sb = new StringBuilder("Current Games:\n");
            for (int i = 0; i < games.length; i++){
                int displayIndex = i + 1;
                gameMap.put(displayIndex, games[i]);

                String white = games[i].whiteUsername() != null ? games[i].whiteUsername() : "[Missing]";
                String black = games[i].blackUsername() != null ? games[i].blackUsername() : "[Missing]";

                sb.append(String.format("%d. %s | White: %s | Black: %s\n",
                        displayIndex, games[i].gameName(), white, black));
            }
            return sb.toString();
        } catch (client.ResponseException e){
            return "Couldn't list games: " + e.getMessage() + "\n";
        }
    }

    private String joinGame(String[] params){
        if (params.length <2){
            return "Expected: join <GameID> <WHITE|BLACK>";
        }
        try{
            int gameIndex = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();
            if(!color.equals("WHITE") && !color.equals("BLACK")){
                return "Invalid color! Expecting white or black";
            }

            if(!gameMap.containsKey(gameIndex)){
                return "That game doesn't exist";
            }

            var gameData = gameMap.get(gameIndex);
            facade.joinGame(color, gameData.gameID(), authToken);
            // connecting to draw da board:
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            ChessGame.TeamColor teamColor = color.equals("WHITE") ?
                    ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            BoardRender.drawBoard(board, teamColor);


            return "Successfully joined game " + gameData.gameName() + " as the " + color + " player!\n";
        } catch (NumberFormatException e){
            return "Game number isn't a valid number";
        } catch (client.ResponseException e){
            return "Couldn't join game " + e.getMessage() + "\n";
        }
    }

    private String observeGame(String[] params){
        if(params.length < 1){
            return "Expected: observe <gameNumber>\n";
        }
        try{
            int gameIndex = Integer.parseInt(params[0]);

            if(!gameMap.containsKey(gameIndex)){
                return "That game number doesn't exist!";
            }

            var gameData = gameMap.get(gameIndex);
            facade.joinGame(null, gameData.gameID(), authToken);

            // draw the board they're looking at:
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE;
            BoardRender.drawBoard(board, teamColor);

            return "You're now observing game " + gameData.gameName() + "\n";
        } catch (NumberFormatException e){
            return "Game number isn't a valid number";
        } catch (client.ResponseException e){
            return "Couldn't observe game: " + e.getMessage() + "\n";
        }
    }





}

