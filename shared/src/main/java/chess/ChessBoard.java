package chess;
import java.lang.reflect.Array;
import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public enum SpaceType {
        BLACK,
        WHITE,
        UNUSED
    }
    private int height = 8;
    private int width = 8;
    private int maxFairyHeight = 8;
    private int maxFairyWidth = 8;
    public List<List<ChessPiece>> Board = new ArrayList<List<ChessPiece>>()
        {

        }
    ;
    List<List<SpaceType>> BoardSpaces = Arrays.asList(
            Arrays.asList(SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK),
            Arrays.asList(SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE),
            Arrays.asList(SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK),
            Arrays.asList(SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE),
            Arrays.asList(SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK),
            Arrays.asList(SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE),
            Arrays.asList(SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK),
            Arrays.asList(SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE, SpaceType.BLACK, SpaceType.WHITE)
    );
    ;
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        Board.get(position.getRow()).set(position.getColumn(), piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return Board.get(position.getRow()).get(position.getColumn());
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

    }
}
