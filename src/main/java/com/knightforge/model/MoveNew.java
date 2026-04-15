package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;

public class MoveNew {
    ChessboardPosition from;
    ChessboardPosition to;
    ChessPiece activePiece;
    ChessPiece capturedPiece;

    public MoveNew(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece capturedPiece){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.capturedPiece = capturedPiece;
    }

    public ChessboardPosition getTo() { return this.to;}

    public boolean wasTwoSquarePawnMove() {
        return (activePiece.getType().equals("Pawn") && Math.abs(from.getX() - to.getX()) == 2);
    }
}
