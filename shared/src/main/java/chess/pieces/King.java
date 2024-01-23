package chess.pieces;

import chess.*;

import java.util.*;

public class King extends PieceRules {
    public King(ChessPiece body){
        this.body = body;
        OnCreate();
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<ChessMove>() {};
        int[][] movementVectors = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1},
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };
        int[] startPos = {myPosition.getRow(), myPosition.getColumn()};
        for(int[] vector : movementVectors){
            int[] checkPos = {startPos[0] + vector[0], startPos[1] + vector[1]};
            if(0 < checkPos[0] && checkPos[0] <= 8 && 0 < checkPos[1] && checkPos[1] <= 8) {
                var pos = new ChessPosition(checkPos[0], checkPos[1]);
                var occupant = board.getPiece(pos);
                if(occupant == null || occupant.getTeam() != body.getTeam()) {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
            }
        }
        return moves;
    }
}
