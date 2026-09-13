package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    // checks where pieces are
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // make an array list to fill with each piece's possible moves
        PieceType type = getPieceType();

        // find out the piece's type and it's possible moves
        if (type == PieceType.BISHOP){
            // bishop can move in the 4 diagonals from where he currently is.
            int[][] bishop_directs = {{1,1}, {1, -1}, {-1,1}, {-1,-1}};
        }
        else if (type == PieceType.ROOK){
            // rook can move up, down, left, or right. (row,col)
            int[][] rook_directs = {{1,0}, {-1, 0}, {0,-1}, {0,1}};

        }

        else if (type == PieceType.QUEEN){
            // queen moves in a combination of what rook and bishop do
            int[][] queen_directs = {{1,1}, {1, -1}, {-1,1}, {-1,-1}, {1,0}, {-1, 0}, {0,-1}, {0,1}};

        }
        else if (type == PieceType.KNIGHT){

        }

        else if (type == PieceType.KING){

        }
        else if (type == PieceType.PAWN){

        }
        return moves; // return the moves available for the piece to take
    }
}
