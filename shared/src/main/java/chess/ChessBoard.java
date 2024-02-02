package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Array;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public int width;
    public int height;
    public final List<List<ChessPiece>> emptyBoard = Arrays.asList(
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null)
    );
    public final List<List<ChessPiece.PieceType>> regularSetup = Arrays.asList(
            Arrays.asList(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK),
            Arrays.asList(ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN),
            Arrays.asList(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK)
    );

    public final List<List<ChessGame.TeamColor>> teamSetup = Arrays.asList(
            Arrays.asList(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK),
            Arrays.asList(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE),
            Arrays.asList(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE)
    );

    public List<List<ChessPiece>> board;

    public ChessBoard() {
        height = 8;
        width = 8;
        this.board = Arrays.asList(
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null)
        );
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.get(height - position.getRow()).set(position.getColumn() - 1, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(height - position.getRow()).get(position.getColumn() - 1);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        int y = 0;
        int x = 0;
        for(var currentRow : regularSetup){
            x = 0;
            for(var space : currentRow){
                var currentPiece = regularSetup.get(y).get(x);
                if(currentPiece != null) {
                    board.get(y).set(x, new ChessPiece(teamSetup.get(y).get(x), currentPiece));
                }
                x++;
            }
            y++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return width == that.width && height == that.height && Objects.equals(emptyBoard, that.emptyBoard) && Objects.equals(regularSetup, that.regularSetup) && Objects.equals(teamSetup, that.teamSetup) && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, emptyBoard, regularSetup, teamSetup, board);
    }
}
