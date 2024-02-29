package chess;

import java.util.*;
import java.util.List;

public class ChessRules {
    private static final ArrayList<ChessMove> moves = new ArrayList<>();
    private static final ArrayList<ChessPosition> positions = new ArrayList<>();
    private static final int[][] rookMoves = new int[][]{
            {1, 0},
            {-1, 0},
            {0, -1},
            {0, 1}
    };
    private static final int[][] bishopMoves = new int[][]{
            {1, -1},
            {1, 1},
            {-1, -1},
            {-1, 1}
    };
    private static final int[][] knightMoves = new int[][]{
            {2, 1},
            {2, -1},
            {-2, 1},
            {-2, -1},
            {1, 2},
            {-1, 2},
            {1, -2},
            {-1, -2}
    };

    public static ArrayList<ChessMove> pawn(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        List<ChessPiece.PieceType> possiblePromotions = Arrays.asList(ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN);

        boolean amBlack = color == ChessGame.TeamColor.BLACK;
        boolean hasMoved = amBlack ? (position.getRow() != 7) : (position.getRow() != 2);
        int direction = amBlack ? -1 : 1;

        ChessPosition standardMove = new ChessPosition(position.getRow() + direction, position.getColumn());
        if(board.getPiece(standardMove) == null){
            positions.add(standardMove);
            ChessPosition doubleMove = new ChessPosition(position.getRow() + direction * 2, position.getColumn());
            if(!hasMoved && board.getPiece(doubleMove) == null) {
                positions.add(doubleMove);
            }
        }

        for(int i = -1; i < 2; i++){
            if(i != 0){
                ChessPosition killPosition = new ChessPosition(position.getRow() + direction, position.getColumn() + i);
                ChessPosition enPassant = new ChessPosition(position.getRow(), position.getColumn() + i);
                ChessPosition passantVacancy = new ChessPosition(position.getRow() + direction * 2, position.getColumn() + i);
                if(killPosition.validPosition()){
                    boolean laBrutalite = board.getPiece(enPassant) != null && board.getPiece(enPassant).getTeamColor() != color;
                    if(laBrutalite && board.boardHistory.size() > 1){
                        var lastTurn = board.boardHistory.get(board.boardHistory.size() - 2);
                        var spotLastTurn = lastTurn.get(8 - passantVacancy.getRow()).get(passantVacancy.getColumn() - 1);
                        var adjacentPieceThisTurn = board.getPiece(enPassant);
                        if(adjacentPieceThisTurn != null && adjacentPieceThisTurn.getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(killPosition) == null && spotLastTurn != null && spotLastTurn.getPieceType() == ChessPiece.PieceType.PAWN){
                            positions.add(killPosition);
                        }
                    }
                    if(board.getPiece(killPosition) != null && board.getPiece(killPosition).getTeamColor() != color){
                        positions.add(killPosition);
                    }
                }
            }
        }

        for(ChessPosition pos : positions){
            if(pos.validPosition()){
                if(amBlack ? pos.getRow() != 1 : pos.getRow() != 8){
                    moves.add(new ChessMove(position, pos, null));
                }else{
                    for(ChessPiece.PieceType piece : possiblePromotions) {
                        moves.add(new ChessMove(position, pos, piece));
                    }
                }
            }
        }

        return moves;
    }

    public static ArrayList<ChessMove> rook(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        simplePiece(rookMoves, true, board, color, position);
        return moves;
    }

    public static ArrayList<ChessMove> bishop(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        simplePiece(bishopMoves, true, board, color, position);
        return moves;
    }

    public static ArrayList<ChessMove> knight(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        simplePiece(knightMoves, false, board, color, position);
        return moves;
    }

    public static ArrayList<ChessMove> queen(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        simplePiece(rookMoves, true, board, color, position);
        simplePiece(bishopMoves, true, board, color, position);
        return moves;
    }

    public static ArrayList<ChessMove> king(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        clearMoves();
        simplePiece(rookMoves, false, board, color, position);
        simplePiece(bishopMoves, false, board, color, position);
        addCastle(board, color, position);
        return moves;
    }

    public static boolean rookIsGood(ChessGame.TeamColor color, ChessBoard board, ChessPosition position, int pathStartIndex, int pathEndIndex, int rookColumn){
        var rookPos = new ChessPosition(position.getRow(), rookColumn);
        var piece = board.getPiece(rookPos);
        if(piece == null){
            return false;
        }
        if(piece.getTeamColor() != color){
            return false;
        }
        if(piece.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }
        if(board.pieceHasMoved(board, rookPos)){
            return false;
        }
        for(int i = pathStartIndex; i <= pathEndIndex; i++){
            if(board.getPiece(new ChessPosition(position.getRow(), i)) != null){
                return false;
            }
        }
        return true;
    }

    public static void clearMoves(){
        moves.clear();
        positions.clear();
    }

    public static void simplePiece(int[][] pieceMoves, boolean recurse, ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        addMoves(recurse, board, color, position, pieceMoves);
    }

    public static void addMoves(boolean recurse, ChessBoard board, ChessGame.TeamColor color, ChessPosition position, int[][] moveDirections){
        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos;
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
            do{
                iterPos[0] += dir[0];
                iterPos[1] += dir[1];
                positions.add(new ChessPosition(iterPos[0], iterPos[1]));
            }while(recurse && (0 < iterPos[0] && iterPos[0] < 9 && 0 < iterPos[1] && iterPos[1] < 9 && board.getPiece(new ChessPosition(iterPos[0], iterPos[1])) == null));
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }
    }

    public static void addCastle(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        if(!board.pieceHasMoved(board, position)){
            if(rookIsGood(color, board, position, 2, 4, 1)){
                var castleLeft = new ChessPosition(position.getRow(), position.getColumn() - 2);
                moves.add(new ChessMove(position, castleLeft, null));
            }
            if(rookIsGood(color, board, position, 6, 7, 8)){
                var castleRight = new ChessPosition(position.getRow(), position.getColumn() + 2);
                moves.add(new ChessMove(position, castleRight, null));
            }
        }
    }
}
