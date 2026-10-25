package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessGame.TeamColor color;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.color = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return color == chessGame.color && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, getBoard());
    }

    public TeamColor getTeamTurn() {
        return color;
    }

    public void setTeamTurn(TeamColor team) {
        color = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();

        for (ChessMove move : potentialMoves) {
            if (checkSafety(move, piece.getTeamColor())) {
                validMoveList.add(move);
            }
        }
        return validMoveList;
    }

    private boolean checkSafety(ChessMove move, TeamColor teamColor) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece movingPiece = board.getPiece(start);
        ChessPiece goalPiece = board.getPiece(end);

        board.addPiece(end, movingPiece);
        board.addPiece(start, null);

        boolean safeStatus = !isInCheck(teamColor);

        board.addPiece(start, movingPiece);
        board.addPiece(end, goalPiece);

        return safeStatus;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition origin = move.getStartPosition();
        ChessPosition goal = move.getEndPosition();
        ChessPiece movingPiece = board.getPiece(origin);

        if (movingPiece == null) {
            throw new InvalidMoveException("There is no piece to move");
        }

        if (movingPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("It's not your turn!");
        }

        Collection<ChessMove> validMoveList = validMoves(origin);
        if (validMoveList == null || !validMoveList.contains(move)) {
            throw new InvalidMoveException("That move isn't allowed");
        }

        ChessPiece placingPiece = movingPiece;
        if (move.getPromotionPiece() != null) {
            placingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(goal, placingPiece);
        board.addPiece(origin, null);

        TeamColor nextTeam = (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(nextTeam);
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        TeamColor enemyColor = (teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
        return isPositionAttacked(kingPosition, enemyColor);
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition checkSpot = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(checkSpot);

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return checkSpot;
                }
            }
        }
        return null;
    }

    private boolean isPositionAttacked(ChessPosition targetPosition, TeamColor enemyColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition checkSpot = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(checkSpot);

                if (piece != null && piece.getTeamColor() == enemyColor) {
                    if (pieceAttacksPosition(piece, checkSpot, targetPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean pieceAttacksPosition(ChessPiece piece, ChessPosition piecePos, ChessPosition targetPosition) {
        Collection<ChessMove> enemyMoves = piece.pieceMoves(board, piecePos);
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(targetPosition)) {
                return true;
            }
        }
        return false;
    }

    private boolean boardHasValidMoves(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition checkSpot = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(checkSpot);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(checkSpot);

                    if (moves != null && !moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            if (!boardHasValidMoves(teamColor)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            if (!boardHasValidMoves(teamColor)) {
                return true;
            }
        }
        return false;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }
}