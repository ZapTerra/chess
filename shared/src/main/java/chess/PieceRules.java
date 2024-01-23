package chess;

import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceRules that = (PieceRules) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
