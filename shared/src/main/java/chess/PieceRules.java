package chess;

import java.util.Collection;

public abstract class PieceRules {
    public ChessPiece body;
    public PieceRules(){
        body = null;
    }
    public PieceRules(ChessPiece body){
        this.body = body;
        OnCreate();
    }

    public void OnCreate() {

    }
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
