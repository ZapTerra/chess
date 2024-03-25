import chess.*;
import dataAccess.MemoryDataAccess;
import dataAccess.SqlDataAccess;
import server.*;

public class Main {
    public static void main(String[] args) {
        var server = new Server(new SqlDataAccess()).run(8080);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
    }
}