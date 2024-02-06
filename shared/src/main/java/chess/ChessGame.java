package chess;

import java.util.*;

import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor activeTeam = null;
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
        var board = getBoard();
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
            //System.out.println("Invalid start position.");
            return false;
        }

        var startPiece = gameBoard.getPiece(startPos);
        if(startPiece == null){
            //System.out.println("Piece not found at position.");
            return false;
        }

        String name = startPiece.getPieceType().toString().toLowerCase();
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);

        var teamColor = startPiece.getTeamColor();
        if(activeTeam != null && teamColor != getTeamTurn() && takeTheShot){
            //System.out.println("It's not your turn.");
            return false;
        }

        var endPos = move.getEndPosition();
        if(!endPos.validPosition()){
            //System.out.println("Invalid end position.");
            return false;
        }

        if(!startPiece.pieceMoves(gameBoard, startPos).contains(move)){
            //System.out.println("The " + name + " is afraid of what the other pieces might think if it makes that move.");
            return false;
        }

        var killedPiece = gameBoard.getPiece(endPos);

        int lrMove = startPos.getColumn() - endPos.getColumn();
        boolean moveIsCastle = startPiece.getPieceType() == ChessPiece.PieceType.KING && abs(lrMove) == 2;

        ChessPiece rookPiece = null;
        ChessPosition rookPos = null;
        ChessPosition endRookPos = null;
        if(moveIsCastle){
            var moveDir =  lrMove / 2;
            rookPiece = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
            rookPos = new ChessPosition(startPos.getRow(), lrMove > 0 ? 1 : 8);
            endRookPos = new ChessPosition(startPos.getRow(), startPos.getColumn() - lrMove / 2);

            for(int i = startPos.getColumn(); i != rookPos.getColumn(); i -= moveDir){
                if(isUnderAttack(new ChessPosition(startPos.getRow(), i), teamColor)){
                    //System.out.println("The King refuses to change while a " + (teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE) + " piece can see him.");
                    return false;
                }
            }

            gameBoard.addPiece(endRookPos, rookPiece);
            gameBoard.addPiece(rookPos, null);
        }

        boolean enPassant = startPiece.getPieceType() == ChessPiece.PieceType.PAWN && killedPiece == null && startPos.getColumn() != endPos.getColumn();
        var sceneDeCrime = new ChessPosition(startPos.getRow(), endPos.getColumn());
        var victimeDePassage = gameBoard.getPiece(sceneDeCrime);
        if(enPassant && victimeDePassage != null){
            gameBoard.addPiece(sceneDeCrime, null);
        }

        var endPieceType = move.getPromotionPiece() != null ? move.getPromotionPiece() : startPiece.getPieceType();
        var endPiece = new ChessPiece(teamColor, endPieceType);
        gameBoard.addPiece(endPos, endPiece);
        gameBoard.addPiece(startPos, null);

        boolean validMove = true;
        if(isInCheck(teamColor)){
            //System.out.println("The " + name + " is too loyal to the monarchy to do that.");
            validMove = false;
        }

        if(!(takeTheShot && validMove)){
            gameBoard.addPiece(endPos, killedPiece);
            gameBoard.addPiece(startPos, startPiece);
            if(moveIsCastle){
                gameBoard.addPiece(rookPos, rookPiece);
                gameBoard.addPiece(endRookPos, null);
            }
            if(enPassant){
                gameBoard.addPiece(sceneDeCrime, victimeDePassage);
            }
        }

        if(moveIsCastle && takeTheShot && validMove){
            //System.out.println("Castled!");
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
            gameBoard.saveMove();
            //System.out.println(activeTeam + " took a turn!");
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

    public boolean isUnderAttack(ChessPosition pos, TeamColor teamColor){
        if(pos == null){
            return false;
        }

        var enemyMoves = new ArrayList<ChessMove>();
        for(TeamColor color : TeamColor.values()){
            if(color != teamColor){
                enemyMoves.addAll(allTeamMoves(color));
            }
        }

        for(ChessMove move : enemyMoves){
            if(move.getEndPosition().getRow() == pos.getRow() && move.getEndPosition().getColumn() == pos.getColumn()){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isUnderAttack(getKingPos(teamColor), teamColor);
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
    public void setBoard(ChessBoard gameBoard) {
        this.gameBoard = gameBoard;
        gameBoard.saveMove();
    }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
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



