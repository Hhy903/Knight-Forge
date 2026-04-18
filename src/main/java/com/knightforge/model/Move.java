package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;

public class Move {
    private ChessboardPosition from;
    private ChessboardPosition to;
    private ChessPiece activePiece;
    private ChessPiece involvedPiece;
    private ChessboardPosition enPassantCaptureLocation;
    private ChessboardPosition rookPositionInvolvedInCastle;

    public Move(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece involvedPiece){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.involvedPiece = involvedPiece;
        this.enPassantCaptureLocation = null;
        this.rookPositionInvolvedInCastle = null;
    }

    public Move(ChessboardPosition from, ChessboardPosition to, ChessPiece activePiece, ChessPiece involvedPiece, ChessboardPosition enPassantCapture, ChessboardPosition rookPositionInvolvedInCastle){
        this.from = from;
        this.to = to;
        this.activePiece = activePiece;
        this.involvedPiece = involvedPiece;
        this.enPassantCaptureLocation = enPassantCapture;
        this.rookPositionInvolvedInCastle = rookPositionInvolvedInCastle;
    }

    public ChessboardPosition getTo() { return this.to;}
    public ChessboardPosition getFrom() {return this.from;}
    public ChessPiece getActivePiece() {return this.activePiece; }
    public ChessPiece getInvolvedPiece() { return this.involvedPiece; }

    public boolean wasTwoSquarePawnMove() {
        return (activePiece.getType().equals(PieceType.PAWN) && Math.abs(from.getX() - to.getX()) == 2);
    }

    public boolean isEnPassant() { return (enPassantCaptureLocation != null); }
    public boolean isCastleMove() { return (rookPositionInvolvedInCastle != null); }

    public ChessboardPosition getEnPassantCaptureLocation() {return enPassantCaptureLocation; }
    public ChessboardPosition getRookPositionInvolvedInCastle() {return rookPositionInvolvedInCastle; }
}
