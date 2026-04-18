package com.knightforge.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChessGameTest {
    @Test
    void testOnlyKingsIsImmediateDraw() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bK",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK -- -- -- -- -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Draw by insufficient material.", chessGame.getState().statusMessage());
    }

    @Test
    void testKingAndBishopVersusKingIsImmediateDraw() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bK",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK -- wB -- -- -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Draw by insufficient material.", chessGame.getState().statusMessage());
    }

    @Test
    void testKingAndRookVersusKingIsNotDraw() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bK",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK -- wR -- -- -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        assertEquals(GameMode.IDLE, chessGame.getState().mode());
        assertEquals("White to move.", chessGame.getState().statusMessage());
    }

    @Test
    void testCapturingLastMinorPieceTriggersDraw() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bK",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- bN -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);
        chessGame.selectPosition(new ChessboardPosition(4, 2));
        chessGame.selectPosition(new ChessboardPosition(3, 3));

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Draw by insufficient material.", chessGame.getState().statusMessage());
    }

    @Test
    void testCheckStatusIsShown() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bK -- -- -- bR -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        assertEquals(GameMode.IDLE, chessGame.getState().mode());
        assertEquals("White is in check.", chessGame.getState().statusMessage());
    }

    @Test
    void testCheckmateIsDetected() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bR bN bB -- bK bB bN bR",
                "bP bP bP bP -- bP bP bP",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- -- -- -- -- wP bQ",
                "-- -- -- -- -- wP -- --",
                "wP wP wP wP wP -- -- wP",
                "wR wN wB wQ wK wB wN wR"));

        ChessGame chessGame = new ChessGame();
        chessGame.loadGameState(chessboard, ChessColor.WHITE, "KQkq", null, 1);

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Checkmate. Black wins.", chessGame.getState().statusMessage());
    }

    @Test
    void testStalemateIsDetected() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bK -- -- -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessGame chessGame = new ChessGame();
        chessGame.loadGameState(chessboard, ChessColor.BLACK, "-", null, 0);

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Draw by stalemate.", chessGame.getState().statusMessage());
    }
}
