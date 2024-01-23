package chess;

import chess.*;

import java.util.*;

public class Pawn extends PieceRules {
    boolean hasMoved = true;
    int yDirection = 0;
    int xDirection = 0;
    boolean yAxisPromotes = false;
    int promotionCoord = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pawn pawn = (Pawn) o;
        return hasMoved == pawn.hasMoved && yDirection == pawn.yDirection && xDirection == pawn.xDirection && yAxisPromotes == pawn.yAxisPromotes && promotionCoord == pawn.promotionCoord && Arrays.equals(possiblePromotions, pawn.possiblePromotions);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), hasMoved, yDirection, xDirection, yAxisPromotes, promotionCoord);
        result = 31 * result + Arrays.hashCode(possiblePromotions);
        return result;
    }

    public Pawn(ChessPiece body){
        this.body = body;
        OnCreate();
    }

    public void OnCreate(){
        ChessGame.TeamColor color = body.getTeamColor();
        yDirection = (color == ChessGame.TeamColor.WHITE ? 1 : yDirection);
        yDirection = (color == ChessGame.TeamColor.BLACK ? -1 : yDirection);
        yAxisPromotes = color == ChessGame.TeamColor.BLACK || color == ChessGame.TeamColor.WHITE;
        promotionCoord = color == ChessGame.TeamColor.BLACK ? 1 : 8;
    }
    ChessPiece.PieceType[] possiblePromotions = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT};

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<ChessMove>() {};

        int y = myPosition.getRow();
        int x = myPosition.getColumn();


        ChessPosition standardDestination = new ChessPosition(y + yDirection, x + xDirection);

        //check if pawn has moved, because tests assume that pawns can only start on their second rank
        ChessGame.TeamColor color = body.getTeamColor();
        if(color == ChessGame.TeamColor.BLACK){
            System.out.println("CHEQUE");
            if(y == 7){
                System.out.println("ISPOWERED");
                hasMoved = false;
            }else{
                hasMoved = true;
            }
        }
        if(color == ChessGame.TeamColor.WHITE){
            if(y == 2){
                hasMoved = false;
            }else{
                hasMoved = true;
            }
        }

        //Standard advance
        if(board.getPiece(standardDestination) == null){
            moves.add(new ChessMove(myPosition,standardDestination,null));
            if(!hasMoved) {
                ChessPosition firstStrike = new ChessPosition(y + yDirection * 2, x + xDirection * 2);
                if (board.getPiece(firstStrike) == null){
                    moves.add(new ChessMove(myPosition, firstStrike, null));
                }
            }
        }

        //Diagonal kill
        var diag1 = new ChessPosition(y - xDirection + yDirection, x - yDirection + xDirection);
        var diag2 = new ChessPosition(y + xDirection + yDirection, x + yDirection + xDirection);
        if(board.getPiece(diag1) != null && board.getPiece(diag1).getTeam() != body.getTeam()){
            moves.add(new ChessMove(myPosition, diag1, null));
        }
        if(board.getPiece(diag2) != null && board.getPiece(diag2).getTeam() != body.getTeam()){
            moves.add(new ChessMove(myPosition, diag2, null));
        }

        //Promotions
        List<ChessMove> promotions = new ArrayList<ChessMove>() {};

        for(ChessMove move : moves){
            if(yAxisPromotes && move.getEndPosition().getRow() == promotionCoord
            || !yAxisPromotes && move.getEndPosition().getColumn() == promotionCoord){
                promotions.add(move);
            }
        }
        for(ChessMove move : promotions){
            for(ChessPiece.PieceType p : possiblePromotions){
                moves.add(new ChessMove(myPosition, move.getEndPosition(), p));
            }
            moves.remove(move);
        }

        return moves;
    }
}
