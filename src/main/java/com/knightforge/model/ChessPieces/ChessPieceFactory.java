package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.PieceType;

public class ChessPieceFactory {
    public ChessPiece createPiece(PieceType type, ChessColor color){
        return switch (type) {
            case KING -> new King(color);
            case QUEEN -> new Queen(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            case ROOK -> new Rook(color);
            case PAWN -> new Pawn(color);
        };
    }
}
