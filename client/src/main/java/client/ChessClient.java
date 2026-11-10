
package client;

import exception.ResponseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


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
                case "logout" -> logout(); // logs out the user via the server api and transitions client back to prelogin UI
                case "create" -> createGame(params); // prompts the user for a game name and calls the server create API. This creates the game on the server but doesn't auto join
                case "list" -> listGames(); // lists all existing games on the server, in a numbered list w/ game name & current players
                case "play" -> playGame(params); // allows the user to join a game by specifying the list number & their desired color
                case "observe" -> observeGame(params); // allows the user to specify a game to observe using the list number
                default -> "Unrecognized command. Type 'help' to see valid commands";
            };
        }
    }
}

