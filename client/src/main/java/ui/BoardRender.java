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
        // prints the top & bottom rows
        String[] headers = whitePlayer ?
                new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} :
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "   ");
        for (String colName : headers){
            System.out.print(" " + colName + " ");
        }
        System.out.println("   " + RESET_BG_COLOR);
    }

    private static void printRow(ChessBoard board, int row, boolean whitePlayer){
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " ");

        int startCol = whitePlayer ? 1 : 8;
        int endCol = whitePlayer ? 9 : 0;
        int colStep = whitePlayer ? 1 : -1;

        for (int col = startCol; col != endCol; col += colStep){
            boolean lightSquare = (row + col) % 2 == 1; // white coords always are even when added
            String bgColor = lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_DARK_GREEN;
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            String pieceText = getPieceSymbol(piece);
            System.out.print(bgColor + pieceText);
        }
        System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " " + RESET_BG_COLOR);
    }

    private static String getPieceSymbol(ChessPiece piece){
        if (piece == null){
            return "   ";
        }

        boolean whitePiece = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);
        String textColor = whitePiece ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;

        String letter = switch (piece.getPieceType()){
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
        return textColor + " " + letter + " ";
    }
}
