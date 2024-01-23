package chess.pieces;

import chess.*;

import java.util.*;

public class Rook extends PieceRules {
    public Rook(ChessPiece body){
        this.body = body;
        OnCreate();
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<ChessMove>() {};
        int[][] movementVectors = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };
        int[] startPos = {myPosition.getRow(), myPosition.getColumn()};
        int[] iterPos = {0, 0};
        for(int[] vector : movementVectors){
            iterPos[0] = startPos[0];
            iterPos[1] = startPos[1];
            do {
                iterPos[0] += vector[0];
                iterPos[1] += vector[1];
                if(0 < iterPos[0] && iterPos[0] <= 8 && 0 < iterPos[1] && iterPos[1] <= 8) {
                    var pos = new ChessPosition(iterPos[0], iterPos[1]);
                    var occupant = board.getPiece(pos);
                    if(occupant == null || occupant.getTeam() != body.getTeam()) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }while (board.getPiece(new ChessPosition(iterPos[0], iterPos[1])) == null);
        }
        return moves;
    }
}
