package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;

public class MoveNew {
    private ChessboardPosition from;
    private ChessboardPosition to;
    private ChessPiece activePiece;
    private ChessPiece capturedPiece;
    private ChessboardPosition enPassantCapture;
    private ChessboardPosition rookPositionInvolvedInCastle;

    public MoveNew(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece capturedPiece){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.capturedPiece = capturedPiece;
        this.enPassantCapture = null;
        this.rookPositionInvolvedInCastle = null;
    }

    public MoveNew(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece capturedPiece, ChessboardPosition enPassantCapture, ChessboardPosition rookPositionInvolvedInCastle){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.capturedPiece = capturedPiece;
        this.enPassantCapture = enPassantCapture;
        this.rookPositionInvolvedInCastle = rookPositionInvolvedInCastle;
    }

    public ChessboardPosition getTo() { return this.to;}
    public ChessboardPosition getFrom() {return this.from;}
    public ChessPiece getActivePiece() {return this.activePiece; }

    public boolean wasTwoSquarePawnMove() {
        return (activePiece.getType().equals("PAWN") && Math.abs(from.getX() - to.getX()) == 2);
    }

    public boolean isEnPassant() { return (enPassantCapture != null); }
    public boolean isCastleMove() { return (rookPositionInvolvedInCastle != null); }

    public ChessboardPosition getEnPassantCaptureLocation() {return enPassantCapture; }
    public ChessboardPosition getRookPositionInvolvedInCastle() {return rookPositionInvolvedInCastle; }
}
