import chess.*;
import client.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.LESHY, ChessPiece.PieceType.MOLEMAN);
        System.out.println("♕ 240 Chess Client: " + piece);
        new Repl().run();
    }
}