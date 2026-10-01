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

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        color = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    // takes as input a position on the chessboard & returns all moves the piece there
    // can legally make. If there is no piece at that location, return null. A move is valid
    // if it is a "piece move" for the piece at the input location and doesn't leave king in danger
    // of check
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null){
            return null;
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();

        for(ChessMove move : potentialMoves){
            if(checkSafety(move, piece.getTeamColor())){
                validMoveList.add(move);
            }
        }
        return validMoveList;
    }


    // checks for the kings safety while going through the list of potential moves
    private boolean checkSafety(ChessMove move, TeamColor teamColor) {
        ChessPosition start = move.getStartPosition(); // gets the piece your trying to move's starting location
        ChessPosition end = move.getEndPosition(); // and where you're trying to move it

        ChessPiece movingPiece = board.getPiece(start); // gets the piece type of your piece
        ChessPiece goalPiece = board.getPiece(end); //and of the piece where you might move

        board.addPiece(end, movingPiece); // moves your piece & leaves its starter square empty
        board.addPiece(start, null);

        boolean safeStatus = !isInCheck(teamColor); // after its 'moved', checks if that would cause issues

        board.addPiece(start, movingPiece); // reset the board to how it was
        board.addPiece(end, goalPiece);

        return safeStatus;
    }


    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */

    // make move should get the moves starting position and ending position, and make sure that
    // the ending position is within the moves valid moves, and if it is, then it moves over
    // and the starting position is replaced with a "null"
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition origin = move.getStartPosition();
        ChessPosition goal = move.getEndPosition();
        ChessPiece movingPiece = board.getPiece(origin);

        if (movingPiece == null){
            throw new InvalidMoveException("There is no piece to move");
        }

        if (movingPiece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("It's not your turn!");
        }


        Collection <ChessMove> validMoveList = validMoves(origin);
        if(validMoveList == null || !validMoveList.contains(move)){
            throw new InvalidMoveException("That move isn't allowed");
        }

        ChessPiece placingPiece = movingPiece;
        if(move.getPromotionPiece() != null){
            placingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(goal, placingPiece);
        board.addPiece(origin, null);

        TeamColor nextTeam = (getTeamTurn() == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(nextTeam);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */


    // using the teamcolor find the location of the team's king. Then, go through all the possible moves of the other
    // team's pieces, and if any of those potential moves equal the kings possible, then return true!
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        for(int r = 1; r <= 8; r++){ // iterate through every position on the board to find the king!
            for(int c = 1; c <= 8; c++){
                ChessPosition checkSpot = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(checkSpot);

                // find the king
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    kingPosition = checkSpot;
                }
            }
        }

        TeamColor enemyColor = (teamColor == TeamColor.BLACK)? TeamColor.WHITE : TeamColor.BLACK;
        // then, need to iterate through opposite color's potential piece moves, and if movRow = kingRow && movCol = kingCol return true,
        // else, return false.
        for(int r = 1; r <=8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition checkSpot = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(checkSpot);

                if(piece != null && piece.getTeamColor() == enemyColor){
                    Collection <ChessMove> enemyMoves = piece.pieceMoves(board, checkSpot);

                    for(ChessMove move : enemyMoves){
                        if(move.getEndPosition().equals(kingPosition)){ // piece is going to move into kings spot!!
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return true; //not correct!!
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
