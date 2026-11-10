
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
}

