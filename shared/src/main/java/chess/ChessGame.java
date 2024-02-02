package chess;

import java.util.*;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor activeTeam = null;
    private List<List<List<ChessPiece>>> boardHistory;
    private ChessBoard gameBoard;


    public ChessGame() {

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return activeTeam;
    }


    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        activeTeam = team;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return activeTeam == chessGame.activeTeam;
    }


    @Override
    public int hashCode() {
        return Objects.hash(activeTeam);
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var board = getGameBoard();
        final List<ChessMove> allMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        var legalMoves = new ArrayList<ChessMove>();

        var allMovesCopy = new ArrayList<>(allMoves);
        for (ChessMove move : allMovesCopy) {
            if (validMove(move)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    public boolean validMove(ChessMove move){
        return validMove(move, false);
    }
    public boolean validMove(ChessMove move, boolean takeTheShot){
        var startPos = move.getStartPosition();
        if(!startPos.validPosition()){
            System.out.println("Invalid start position.");
            return false;
        }

        var startPiece = gameBoard.getPiece(startPos);
        if(startPiece == null){
            System.out.println("Piece not found at position.");
            return false;
        }

        String name = startPiece.getPieceType().toString().toLowerCase();
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);

        var teamColor = startPiece.getTeamColor();
        if(activeTeam != null && teamColor != getTeamTurn()){
            System.out.println("It's not your turn.");
            return false;
        }

        var endPos = move.getEndPosition();
        if(!endPos.validPosition()){
            System.out.println("Invalid end position.");
            return false;
        }

        if(!startPiece.pieceMoves(gameBoard, startPos).contains(move)){
            System.out.println("The " + name + " is afraid of what the other pieces might think if it makes that move.");
            return false;
        }else{
            System.out.println("The " + name + " is not a coward.");
        }

        var killedPiece = gameBoard.getPiece(endPos);

        var endPieceType = move.getPromotionPiece() != null ? move.getPromotionPiece() : startPiece.getPieceType();
        var endPiece = new ChessPiece(teamColor, endPieceType);
        gameBoard.addPiece(endPos, endPiece);
        gameBoard.addPiece(startPos, null);

        boolean validMove = true;
        if(isInCheck(teamColor)){
            System.out.println("The " + name + " is too loyal to the king to do that.");
            validMove = false;
        }

        if(!(takeTheShot && validMove)){
            gameBoard.addPiece(endPos, killedPiece);
            gameBoard.addPiece(startPos, startPiece);
        }

        return validMove;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!validMove(move, true)) {
            throw new InvalidMoveException();
        }else{
            activeTeam = activeTeam == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        }
    }

    public List<ChessMove> allTeamMoves(TeamColor teamColor){
        var whoseTurnItActuallyShouldBeRightNow = activeTeam;
        activeTeam = teamColor;
        var yPos = 8;
        var xPos = 1;
        var allMoves = new ArrayList<ChessMove>();
        for(var y : gameBoard.board){
            xPos = 1;
            for(var x : y){
                if(x != null && x.getTeamColor() == teamColor){
                    var moves = x.pieceMoves(gameBoard, new ChessPosition(yPos, xPos));
                    allMoves.addAll(moves);
                }
                xPos++;
            }
            yPos--;
        }
        activeTeam = whoseTurnItActuallyShouldBeRightNow;
        return allMoves;
    }

;    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingCoords = getKingPos(teamColor);
        if(kingCoords == null){
            return false;
        }

        var enemyMoves = new ArrayList<ChessMove>();
        for(TeamColor color : TeamColor.values()){
            enemyMoves.addAll(allTeamMoves(color));
        }

        for(ChessMove move : enemyMoves){
            if(move.getEndPosition().getRow() == kingCoords.getRow() && move.getEndPosition().getColumn() == kingCoords.getColumn()){
                return true;
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
        return isInStalemate(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var whoseTurnItActuallyShouldBeRightNow = activeTeam;
        activeTeam = teamColor;
        var uncheckedMoves = allTeamMoves(teamColor);
        var legalMoves = new ArrayList<ChessMove>();
        boolean stalemate = true;
        for(var move : uncheckedMoves){
            if(validMove(move)){
                stalemate = false;
            }
        }
        activeTeam = whoseTurnItActuallyShouldBeRightNow;
        return stalemate;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param gameBoard the new board to use
     */
    public void setGameBoard(ChessBoard gameBoard) {
        this.gameBoard = gameBoard;
    }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getGameBoard() {
        return gameBoard;
    }

    public ChessPosition getKingPos(ChessGame.TeamColor color){
        var yPos = 8;
        var xPos = 1;
        for(var y : gameBoard.board){
            xPos = 1;
            for(var x : y){
                if(x != null && x.getPieceType() == ChessPiece.PieceType.KING && x.getTeamColor() == color){
                    return new ChessPosition(yPos, xPos);
                }
                xPos++;
            }
            yPos--;
        }
        return null;
    }
}



