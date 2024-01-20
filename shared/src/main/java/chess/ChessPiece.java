package chess;

import chess.pieces.Pawn;

import java.util.Collection;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    public PieceType piece;
    public ChessGame.TeamColor team;

    public ChessPiece(){
        team = ChessGame.TeamColor.WHITE;
        piece = PieceType.PAWN;
        OnCreate();
    }
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        team = pieceColor;
        piece = type;
        OnCreate();
    }

    public ChessPiece getActual(){
        if(piece == PieceType.PAWN){
            return new Pawn(team);
        }
        return null;
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
        PAWN,
        MOLEMAN,
        SQUIRREL
    }

    public enum Team {
        BLACK,
        WHITE,
        GRAVY
    }

    public void OnCreate(){

    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return null;
    }
}
