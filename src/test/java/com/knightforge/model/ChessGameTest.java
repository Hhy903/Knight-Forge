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
}
