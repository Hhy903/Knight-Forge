package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;

public class MoveNew {
    ChessboardPosition from;
    ChessboardPosition to;
    ChessPiece activePiece;
    ChessPiece capturedPiece;
    boolean isEnPassant;
    boolean isCastleMove;

    public MoveNew(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece capturedPiece){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.capturedPiece = capturedPiece;
        this.isEnPassant = false;
        this.isCastleMove = false;
    }

    public MoveNew(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece capturedPiece, boolean isEnPassant, boolean isCastleMove){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.capturedPiece = capturedPiece;
        this.isEnPassant = isEnPassant;
        this.isCastleMove = isCastleMove;
    }

    public ChessboardPosition getTo() { return this.to;}
    public ChessboardPosition getFrom() {return this.from;}

    public boolean wasTwoSquarePawnMove() {
        return (activePiece.getType().equals("Pawn") && Math.abs(from.getX() - to.getX()) == 2);
    }
}
