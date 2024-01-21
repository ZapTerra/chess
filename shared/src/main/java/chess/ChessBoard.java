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
    public List<List<ChessPiece>> Board = Arrays.asList();
    public List<List<ChessPiece.PieceType>> PieceSetup = Arrays.asList(
            Arrays.asList(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK),
            Arrays.asList(ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(null, null, null, null, null, null, null, null),
            Arrays.asList(ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN),
            Arrays.asList(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK)
        );
    ;
    public List<List<ChessGame.TeamColor>> TeamSetup = Arrays.asList(
            Arrays.asList(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK),
            Arrays.asList(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK),
            Arrays.asList(ChessGame.TeamColor.CHAOS, ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS),
            Arrays.asList(ChessGame.TeamColor.CHAOS, ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS),
            Arrays.asList(ChessGame.TeamColor.CHAOS, ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS),
            Arrays.asList(ChessGame.TeamColor.CHAOS, ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS,ChessGame.TeamColor.CHAOS),
            Arrays.asList(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE),
            Arrays.asList(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE,ChessGame.TeamColor.WHITE)
        );
    ;
    public ChessBoard() {
        wipeBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        Board.get(width - position.getRow()).set(position.getColumn(), piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return Board.get(position.getRow() - 1).get(position.getColumn() - 1);
    }

    public void wipeBoard(){
        Board = Arrays.asList(
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
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        int y = 0;
        int x = 0;
        wipeBoard();
        for(List<ChessPiece> row : Board){
            for(ChessPiece piece : row){
                ChessPiece.PieceType pieceType = PieceSetup.get(y).get(x);
                ChessGame.TeamColor teamColor = TeamSetup.get(y).get(x);
                if(pieceType != null && teamColor != null){
                    Board.get(y).set(x, new ChessPiece(teamColor, pieceType));
                }else{
                    Board.get(y).set(x, null);
                }
                x++;
            }
            x = 0;
            y++;
        }
    }
}
