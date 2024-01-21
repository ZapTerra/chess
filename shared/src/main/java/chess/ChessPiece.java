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
    private PieceType piece;
    private PieceRules rules;
    private ChessGame.TeamColor team;

    public ChessPiece(){
        team = ChessGame.TeamColor.WHITE;
        piece = PieceType.PAWN;
        rules = new Pawn(this);
    }
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        team = pieceColor;
        piece = type;
        if(type == PieceType.PAWN){
            rules = new Pawn(this);
        }
    }

    public void setPiece(PieceType piece){
        this.piece = piece;
    }

    public PieceType getPiece(){
        return piece;
    }

    public void setTeam(ChessGame.TeamColor teamColor){
        this.team = teamColor;
    }

    public ChessGame.TeamColor getTeam(){
        return team;
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
        for(var i : rules.pieceMoves(board, myPosition)){
            System.out.println(i.getStartPosition().getRow());
            System.out.println(i.getStartPosition().getColumn());
            System.out.println(i.getEndPosition().getRow());
            System.out.println(i.getEndPosition().getColumn());
        }
        return rules.pieceMoves(board, myPosition);
    }
}
