package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;
import java.util.List;

public record GameState(
        ChessPiece[][] board,
        ChessboardPosition selectedPosition,
        List<ChessboardPosition> legalMoves,
        ChessColor currentTurn,
        GameMode mode,
        String statusMessage
){}
