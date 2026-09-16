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

    // helper function that checks that the sliding moves for bishop, rook, & queen are valid
    private void slideSquares(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int dRow, int dCol){
        int r = myPosition.getRow() + dRow; // gets current position and adds rol/col to see where trying to move
        int c = myPosition.getColumn() + dCol;

        // iterate over the board, make sure it stays within bounds
        while (r >= 1 && r <= 8 && c >= 1 && c <= 8){
            ChessPosition nextPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(nextPos);

            // nothing is on the estimated square so check next move
            if (occupant == null){
                moves.add(new ChessMove(myPosition, nextPos, null));

            }
            else{
                // if there's a piece at the spot and it's not your color, then it can be overtaken but no more sliding
                if (occupant.getTeamColor() != getTeamColor()){
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
                break;
            }
            // check the next square in the same direction. Stop checking once a same color piece is there
            r += dRow;
            c += dCol;
        }

    }

    // helper function for at least the knight and king. these pieces can't continually move in a direction so no while loop
    private void jumpSquares(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int dRow, int dCol){
        int r = myPosition.getRow() + dRow; // gets current position and adds rol/col to see where trying to move
        int c = myPosition.getColumn() + dCol;

        if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition nextPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(nextPos);
            if (occupant == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));

            } else {
                // if there's a piece at the spot and it's not your color, then it can be overtaken but no more sliding
                if (occupant.getTeamColor() != getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
            }
        }
    }


    // FUNCTION OUTLINE GIVEN TO US:
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // make an array list to fill with each piece's possible moves
        PieceType type = getPieceType();

        // find out the piece's type and it's possible moves
        if (type == PieceType.BISHOP) {
            // bishop can move in the 4 diagonals from where he currently is.
            //bishop_directs = {{1,1}, {1, -1}, {-1,1}, {-1,-1}};
            slideSquares(board, myPosition, moves, 1, 1);
            slideSquares(board, myPosition, moves, 1, -1);
            slideSquares(board, myPosition, moves, -1, 1);
            slideSquares(board, myPosition, moves, -1, -1);
        }
        else if (type == PieceType.ROOK){
            // rook can move up, down, left, or right. (row,col)
            // rook_directs = {{1,0}, {-1, 0}, {0,-1}, {0,1}};
            slideSquares(board, myPosition, moves, 1, 0);
            slideSquares(board, myPosition, moves, -1, 0);
            slideSquares(board, myPosition, moves, 0, 1);
            slideSquares(board, myPosition, moves, 0, -1);

        }

        else if (type == PieceType.QUEEN){
            // queen moves in a combination of what rook and bishop do
            // queen_directs = {{1,1}, {1, -1}, {-1,1}, {-1,-1}, {1,0}, {-1, 0}, {0,-1}, {0,1}};
            slideSquares(board, myPosition, moves, 1, 0);
            slideSquares(board, myPosition, moves, -1, 0);
            slideSquares(board, myPosition, moves, 0, 1);
            slideSquares(board, myPosition, moves, 0, -1);
            slideSquares(board, myPosition, moves, 1, 1);
            slideSquares(board, myPosition, moves, 1, -1);
            slideSquares(board, myPosition, moves, -1, 1);
            slideSquares(board, myPosition, moves, -1, -1);


        }
        else if (type == PieceType.KNIGHT){
        // knight moves in an L-shape pattern: two squares in one direction (either vertically
            // or horizontally, and then one square perpendicular to that direction
            // it's the only piece that can jump over other pieces during its move
            // every time a knight moves, it always lands on a square of the opposite color
            // it captures a piece by landing on its square
            // knight_directs = {1,2}, {2,1}, {1, -2}, {2, -1}, {-1, 2}, {-2, 1}, {-1,-2} {-2,-1}
            jumpSquares(board, myPosition, moves, 1,2);
            jumpSquares(board, myPosition, moves, 2,1);
            jumpSquares(board, myPosition, moves, 1,-2);
            jumpSquares(board, myPosition, moves, 2,-1);
            jumpSquares(board, myPosition, moves, -1,2);
            jumpSquares(board, myPosition, moves, -2,1);
            jumpSquares(board, myPosition, moves, -1,-2);
            jumpSquares(board, myPosition, moves, -2,-1);
            }

        else if (type == PieceType.KING){
            // king can move ONE spot any adjacent direction. r, c
            // king directions = {1, 0}, {1,1}, {1,-1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0} {-1, 1}
            jumpSquares(board, myPosition, moves, 1,0);
            jumpSquares(board, myPosition, moves, 1,1);
            jumpSquares(board, myPosition, moves, 1,-1);
            jumpSquares(board, myPosition, moves, 0,-1);
            jumpSquares(board, myPosition, moves, 0,1);
            jumpSquares(board, myPosition, moves, -1,-1);
            jumpSquares(board, myPosition, moves, -1,0);
            jumpSquares(board, myPosition, moves, -1,1);

        }

        else if (type == PieceType.PAWN){
            // pawns can only move forward, not backwards sideways or diagonally when advancing
            // initial two-square jump: on a pawn's first move from it's starting rank, it has the option to advance 1 or 2
            // after a pawn has left its starting square, it may only move one square forward per turn
            // if a piece sits directly in front of a pawn, the pawn is blocked
            // capturing: diagonal capture: pawns capture one square diagonally forward to the left or right
            // ** en passant is extra credit ** ** so is castling **
            // promotion: if a pawn reaches the opposite side of the board (the 8th rank for white, the 1st rank
            // for black), it transforms into a queen, rook, bishop, or knight

        }
        return moves; // return the moves available for the piece to take
    }
}
