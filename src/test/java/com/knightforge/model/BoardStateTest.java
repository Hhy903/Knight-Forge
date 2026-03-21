package com.knightforge.model;

import com.knightforge.controller.GamePhase;
import com.knightforge.controller.GameSession;
import com.knightforge.view.ChessboardPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardStateTest {

    @Test
    void pawnReachingLastRankTriggersPromotion() {
        BoardState boardState = new BoardState();
        boardState.loadFromLines(List.of(
                "CURRENT:BLACK",
                "CASTLE:-",
                "EN_PASSANT:-",
                "HALFMOVE:0",
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bP -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"
        ));

        Move move = boardState.applyMove(new ChessboardPoint(6, 0), new ChessboardPoint(7, 0));

        assertNotNull(move);
        assertTrue(boardState.isPromotionRequired(move));
    }

    @Test
    void enPassantCaptureRemovesCapturedPawn() {
        BoardState boardState = new BoardState();
        boardState.loadFromLines(List.of(
                "CURRENT:WHITE",
                "CASTLE:-",
                "EN_PASSANT:2,5",
                "HALFMOVE:0",
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP bP -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"
        ));

        Move move = boardState.applyMove(new ChessboardPoint(3, 4), new ChessboardPoint(2, 5));

        assertNotNull(move);
        assertNull(boardState.getPieceAt(3, 5));
        assertNotNull(boardState.getPieceAt(2, 5));
        assertEquals(PieceType.PAWN, boardState.getPieceAt(2, 5).getType());
        assertEquals(ChessColor.WHITE, boardState.getPieceAt(2, 5).getColor());
    }

    @Test
    void castlingMovesKingAndRookTogether() {
        BoardState boardState = new BoardState();
        boardState.loadFromLines(List.of(
                "CURRENT:WHITE",
                "CASTLE:K",
                "EN_PASSANT:-",
                "HALFMOVE:0",
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- wR"
        ));

        Move move = boardState.applyMove(new ChessboardPoint(7, 4), new ChessboardPoint(7, 6));

        assertNotNull(move);
        assertEquals(PieceType.KING, boardState.getPieceAt(7, 6).getType());
        assertEquals(PieceType.ROOK, boardState.getPieceAt(7, 5).getType());
        assertNull(boardState.getPieceAt(7, 4));
        assertNull(boardState.getPieceAt(7, 7));
    }

    @Test
    void foolsMatePositionTriggersCheckmate() {
        GameSession gameSession = new GameSession(new BoardState());
        gameSession.loadGame(List.of(
                "CURRENT:BLACK",
                "CASTLE:KQkq",
                "EN_PASSANT:5,6",
                "HALFMOVE:0",
                "bR bN bB bQ bK bB bN bR",
                "bP bP bP bP -- bP bP bP",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- -- -- -- -- wP --",
                "-- -- -- -- -- wP -- --",
                "wP wP wP wP wP -- -- wP",
                "wR wN wB wQ wK wB wN wR"
        ));

        assertTrue(gameSession.handleSquareClick(new ChessboardPoint(0, 3)));
        assertTrue(gameSession.handleSquareClick(new ChessboardPoint(4, 7)));

        assertEquals(GamePhase.GAME_OVER, gameSession.getPhase());
        assertNotNull(gameSession.getGameResult());
        assertTrue(gameSession.getGameResult().contains("checkmate"));
    }

    @Test
    void kingVersusKingIsInsufficientMaterial() {
        BoardState boardState = new BoardState();
        boardState.loadFromLines(List.of(
                "CURRENT:BLACK",
                "CASTLE:-",
                "EN_PASSANT:-",
                "HALFMOVE:0",
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"
        ));

        assertTrue(boardState.isInsufficientMaterial());
    }
}
