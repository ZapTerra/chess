package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<List<List<ChessPiece>>> boardHistory = new ArrayList<>();
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

    public boolean pieceHasMoved(ChessBoard board, ChessPosition position){
        ChessPiece piece = board.getPiece(position);
        ChessPiece.PieceType pieceType = piece.getPieceType();
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        boolean iHaveMoved = false;
        for(int i = 0; i < board.boardHistory.size() - 1; i++){
            var pieceAtTime = board.boardHistory.get(i).get(8 - position.getRow()).get(position.getColumn() - 1);
            if(pieceAtTime == null || (pieceAtTime.getPieceType() != pieceType || pieceAtTime.getTeamColor() != teamColor)){
                iHaveMoved = true;
                break;
            }
        }
        return iHaveMoved;
    }

    public void saveMove(){
        List<List<ChessPiece>> boardCopy = Arrays.asList(
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null),
                Arrays.asList(null, null, null, null, null, null, null, null)
        );
        int y = 0;
        for(var row : board){
            int x = 0;
            for(var column : row){
                var pieceRef = board.get(y).get(x);
                if(pieceRef != null){
                    boardCopy.get(y).set(x, new ChessPiece(pieceRef.getTeamColor(), pieceRef.getPieceType()));
                }
                x++;
            }
            y++;
        }
        boardHistory.add(boardCopy);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        int y = 0;
        int x;
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
        boardHistory.clear();
        saveMove();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return width == that.width && height == that.height && Objects.equals(regularSetup, that.regularSetup) && Objects.equals(teamSetup, that.teamSetup) && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, regularSetup, teamSetup, board);
    }
}
