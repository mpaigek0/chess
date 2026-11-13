package ui;

import chess.*;
import static ui.EscapeSequences.*;

public class BoardRender {
    public static void drawBoard(ChessBoard board, ChessGame.TeamColor perspective){
        // if you're an observer, your perspective defaults to white
        boolean whitePlayer = (perspective == null || perspective == ChessGame.TeamColor.WHITE);

        System.out.println();
        printHeaders(whitePlayer);

        int startRow = whitePlayer ? 8 : 1;
        int endRow = whitePlayer ? 0 : 9;
        int rowStep = whitePlayer ? -1 : 1;

        for (int row = startRow; row != endRow; row += rowStep){
            printRow(board, row, whitePlayer);
        }

        printHeaders(whitePlayer);
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static void printHeaders(boolean whitePlayer){
        String[] headers = whitePlayer ?
                new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} :
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "   ");
        for (String colName : headers){
            System.out.print(" " + colName + " ");
        }
        System.out.println("  " + RESET_BG_COLOR);
    }

    private static void printRow(ChessBoard board, int row, boolean whitePlayer){
        // prints pieces
    }
}
