package chess;
import java.util.*;
import java.util.List;

public class ChessRules {
    private static final ArrayList<ChessMove> moves = new ArrayList<>();
    private static final ArrayList<ChessPosition> positions = new ArrayList<>();

    public static ArrayList<ChessMove> Pawn(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();
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
                if(killPosition.validPosition()){
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
    public static ArrayList<ChessMove> Rook(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();

        int[][] moveDirections = new int[][]{
                {1, 0},
                {-1, 0},
                {0, -1},
                {0, 1}
        };

        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos;
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
            do{
                iterPos[0] += dir[0];
                iterPos[1] += dir[1];
                positions.add(new ChessPosition(iterPos[0], iterPos[1]));
            }while(0 < iterPos[0] && iterPos[0] < 9 && 0 < iterPos[1] && iterPos[1] < 9 && board.getPiece(new ChessPosition(iterPos[0], iterPos[1])) == null);
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }

        return moves;
    }
    public static ArrayList<ChessMove> Bishop(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();

        int[][] moveDirections = new int[][]{
                {1, -1},
                {1, 1},
                {-1, -1},
                {-1, 1}
        };

        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos = startPos.clone();
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
                do{
                iterPos[0] += dir[0];
                iterPos[1] += dir[1];
                positions.add(new ChessPosition(iterPos[0], iterPos[1]));
            }while(0 < iterPos[0] && iterPos[0] < 9 && 0 < iterPos[1] && iterPos[1] < 9 && board.getPiece(new ChessPosition(iterPos[0], iterPos[1])) == null);
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }

        return moves;
    }
    public static ArrayList<ChessMove> Knight(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();

        int[][] moveDirections = new int[][]{
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1},
                {1, 2},
                {-1, 2},
                {1, -2},
                {-1, -2}
        };

        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos = startPos.clone();
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
            iterPos[0] += dir[0];
            iterPos[1] += dir[1];
            positions.add(new ChessPosition(iterPos[0], iterPos[1]));
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }

        return moves;
    }
    public static ArrayList<ChessMove> Queen(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();

        int[][] moveDirections = new int[][]{
                {1, -1},
                {1, 1},
                {-1, -1},
                {-1, 1},
                {1, 0},
                {-1, 0},
                {0, -1},
                {0, 1}
        };

        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos = startPos.clone();
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
            do{
                iterPos[0] += dir[0];
                iterPos[1] += dir[1];
                positions.add(new ChessPosition(iterPos[0], iterPos[1]));
            }while(0 < iterPos[0] && iterPos[0] < 9 && 0 < iterPos[1] && iterPos[1] < 9 && board.getPiece(new ChessPosition(iterPos[0], iterPos[1])) == null);
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }

        return moves;
    }

    public static boolean rookIsGood(ChessBoard board, ChessPosition position, int pathStartIndex, int pathEndIndex, int rookColumn){
        var rookPos = new ChessPosition(position.getRow(), rookColumn);
        var piece = board.getPiece(rookPos);
        if(piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK){
            if(board.pieceHasMoved(board, rookPos)){
                return false;
            }
            for(int i = pathStartIndex; i <= pathEndIndex; i++){
                if(board.getPiece(new ChessPosition(position.getRow(), i)) != null){
                    return false;
                }
            }
        }
        return true;
    }

    public static ArrayList<ChessMove> King(ChessBoard board, ChessGame.TeamColor color, ChessPosition position){
        moves.clear();
        positions.clear();

        int[][] moveDirections = new int[][]{
                {1, -1},
                {1, 1},
                {-1, -1},
                {-1, 1},
                {1, 0},
                {-1, 0},
                {0, -1},
                {0, 1}
        };

        int[] startPos = new int[]{position.getRow(), position.getColumn()};
        int[] iterPos = startPos.clone();
        for(int[] dir : moveDirections){
            iterPos = startPos.clone();
            iterPos[0] += dir[0];
            iterPos[1] += dir[1];
            positions.add(new ChessPosition(iterPos[0], iterPos[1]));
        }

        for(ChessPosition pos : positions){
            if(0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9){
                if(board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != color){
                    moves.add(new ChessMove(position, pos, null));
                }
            }
        }

        if(!board.pieceHasMoved(board, position)){
            if(rookIsGood(board, position, 2, 4, 1)){
                var castleLeft = new ChessPosition(position.getRow(), position.getColumn() - 2);
                moves.add(new ChessMove(position, castleLeft, null));
            }
            if(rookIsGood(board, position, 6, 7, 8)){
                var castleRight = new ChessPosition(position.getRow(), position.getColumn() + 2);
                moves.add(new ChessMove(position, castleRight, null));
            }
        }

        return moves;
    }
}
