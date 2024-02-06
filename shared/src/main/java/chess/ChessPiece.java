package chess;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    public ChessGame.TeamColor color;
    public PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return color == piece.color && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
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
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
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
    public List<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(type == PieceType.PAWN){
            return ChessRules.Pawn(board, color, myPosition);
        }
        if(type == PieceType.ROOK){
            return ChessRules.Rook(board, color, myPosition);
        }
        if(type == PieceType.BISHOP){
            return ChessRules.Bishop(board, color, myPosition);
        }
        if(type == PieceType.QUEEN){
            return ChessRules.Queen(board, color, myPosition);
        }
        if(type == PieceType.KNIGHT){
            return ChessRules.Knight(board, color, myPosition);
        }
        if(type == PieceType.KING){
            return ChessRules.King(board, color, myPosition);
        }
        return null;
    }
}
