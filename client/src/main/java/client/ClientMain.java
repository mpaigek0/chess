package client;


import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ChessClient client = new ChessClient(port);
        client.run();
    }
}
