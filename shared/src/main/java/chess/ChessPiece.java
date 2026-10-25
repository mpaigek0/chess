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

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    private void slideSquares(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int dRow, int dCol) {
        int r = myPosition.getRow() + dRow;
        int c = myPosition.getColumn() + dCol;

        while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition nextPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(nextPos);

            if (occupant == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else {
                if (occupant.getTeamColor() != getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
                break;
            }
            r += dRow;
            c += dCol;
        }
    }

    private void jumpSquares(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int dRow, int dCol) {
        int r = myPosition.getRow() + dRow;
        int c = myPosition.getColumn() + dCol;

        if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition nextPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(nextPos);
            if (occupant == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else {
                if (occupant.getTeamColor() != getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
            }
        }
    }

    private void pawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int dRow, int dCol, boolean capture) {
        int r = myPosition.getRow() + dRow;
        int c = myPosition.getColumn() + dCol;

        if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition nextPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(nextPos);
            boolean promotion = (getTeamColor() == ChessGame.TeamColor.WHITE && r == 8) || (getTeamColor() == ChessGame.TeamColor.BLACK && r == 1);

            if (capture) {
                if (occupant != null && occupant.getTeamColor() != getTeamColor()) {
                    addPawnMove(myPosition, nextPos, promotion, moves);
                }
            } else {
                if (occupant == null) {
                    addPawnMove(myPosition, nextPos, promotion, moves);
                }
            }
        }
    }

    private void addPawnMove(ChessPosition from, ChessPosition to, boolean promotion, Collection<ChessMove> moves) {
        if (promotion) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        slideSquares(board, myPosition, moves, 1, 1);
        slideSquares(board, myPosition, moves, 1, -1);
        slideSquares(board, myPosition, moves, -1, 1);
        slideSquares(board, myPosition, moves, -1, -1);
    }

    private void addRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        slideSquares(board, myPosition, moves, 1, 0);
        slideSquares(board, myPosition, moves, -1, 0);
        slideSquares(board, myPosition, moves, 0, 1);
        slideSquares(board, myPosition, moves, 0, -1);
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addRookMoves(board, myPosition, moves);
        addBishopMoves(board, myPosition, moves);
    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        jumpSquares(board, myPosition, moves, 1, 2);
        jumpSquares(board, myPosition, moves, 2, 1);
        jumpSquares(board, myPosition, moves, 1, -2);
        jumpSquares(board, myPosition, moves, 2, -1);
        jumpSquares(board, myPosition, moves, -1, 2);
        jumpSquares(board, myPosition, moves, -2, 1);
        jumpSquares(board, myPosition, moves, -1, -2);
        jumpSquares(board, myPosition, moves, -2, -1);
    }

    private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        jumpSquares(board, myPosition, moves, 1, 0);
        jumpSquares(board, myPosition, moves, 1, 1);
        jumpSquares(board, myPosition, moves, 1, -1);
        jumpSquares(board, myPosition, moves, 0, -1);
        jumpSquares(board, myPosition, moves, 0, 1);
        jumpSquares(board, myPosition, moves, -1, -1);
        jumpSquares(board, myPosition, moves, -1, 0);
        jumpSquares(board, myPosition, moves, -1, 1);
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int direction = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

        pawnMoves(board, myPosition, moves, direction, 0, false);

        if (myPosition.getRow() == startRow) {
            int rowAhead = myPosition.getRow() + direction;
            ChessPosition squareAhead = new ChessPosition(rowAhead, myPosition.getColumn());
            if (board.getPiece(squareAhead) == null) {
                pawnMoves(board, myPosition, moves, direction * 2, 0, false);
            }
        }

        pawnMoves(board, myPosition, moves, direction, -1, true);
        pawnMoves(board, myPosition, moves, direction, 1, true);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        PieceType type = getPieceType();

        if (type == PieceType.BISHOP) {
            addBishopMoves(board, myPosition, moves);
        } else if (type == PieceType.ROOK) {
            addRookMoves(board, myPosition, moves);
        } else if (type == PieceType.QUEEN) {
            addQueenMoves(board, myPosition, moves);
        } else if (type == PieceType.KNIGHT) {
            addKnightMoves(board, myPosition, moves);
        } else if (type == PieceType.KING) {
            addKingMoves(board, myPosition, moves);
        } else if (type == PieceType.PAWN) {
            addPawnMoves(board, myPosition, moves);
        }
        return moves;
    }
}