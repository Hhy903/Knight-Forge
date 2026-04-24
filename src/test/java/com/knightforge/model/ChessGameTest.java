package com.knightforge.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testPromotionSelectionFlowEntersAwaitingPromotionAndIgnoresBoardClicks() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "wP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        chessGame.selectPosition(new ChessboardPosition(1, 0));
        assertEquals(GameMode.STAGING, chessGame.getState().mode());
        assertEquals(List.of(new ChessboardPosition(0, 0)), chessGame.getState().legalMoves());

        chessGame.selectPosition(new ChessboardPosition(0, 0));

        assertEquals(GameMode.AWAITING_PROMOTION, chessGame.getState().mode());
        assertEquals("Choose promotion piece.", chessGame.getState().statusMessage());
        assertEquals(ChessColor.WHITE, chessGame.getState().currentTurn());
        assertEquals(PieceType.PAWN, chessGame.getState().board()[1][0].getType());
        assertNull(chessGame.getState().board()[0][0]);

        chessGame.selectPosition(new ChessboardPosition(7, 4));

        assertEquals(GameMode.AWAITING_PROMOTION, chessGame.getState().mode());
        assertEquals("Choose promotion piece.", chessGame.getState().statusMessage());
        assertEquals(new ChessboardPosition(1, 0), chessGame.getState().selectedPosition());
        assertEquals(List.of(new ChessboardPosition(0, 0)), chessGame.getState().legalMoves());
        assertEquals(PieceType.PAWN, chessGame.getState().board()[1][0].getType());
    }

    @Test
    void testPromotionSelectionAppliesChosenPieceAndRefreshesCheckStatus() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bK",
                "-- -- -- -- -- -- wP --",
                "-- -- -- -- -- wK -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessGame chessGame = new ChessGame();
        chessGame.loadGameState(chessboard, ChessColor.WHITE, "-", null, 7);

        chessGame.selectPosition(new ChessboardPosition(1, 6));
        chessGame.selectPosition(new ChessboardPosition(0, 6));
        chessGame.handlePromotionSelection(PieceType.ROOK);

        assertEquals(GameMode.IDLE, chessGame.getState().mode());
        assertEquals("Black is in check.", chessGame.getState().statusMessage());
        assertEquals(ChessColor.BLACK, chessGame.getState().currentTurn());
        assertNull(chessGame.getState().board()[1][6]);
        assertNotNull(chessGame.getState().board()[0][6]);
        assertEquals(PieceType.ROOK, chessGame.getState().board()[0][6].getType());
        assertEquals(ChessColor.WHITE, chessGame.getState().board()[0][6].getColor());
    }

    @Test
    void testPromotionCanImmediatelyProduceCheckmate() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bK -- -- -- -- -- -- --",
                "-- wP wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessGame chessGame = new ChessGame(chessboard);

        chessGame.selectPosition(new ChessboardPosition(1, 1));
        chessGame.selectPosition(new ChessboardPosition(0, 1));
        chessGame.handlePromotionSelection(PieceType.QUEEN);

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Checkmate. White wins.", chessGame.getState().statusMessage());
        assertEquals(ChessColor.BLACK, chessGame.getState().currentTurn());
        assertEquals(PieceType.QUEEN, chessGame.getState().board()[0][1].getType());
        assertNull(chessGame.getState().board()[1][1]);
    }

    @Test
    void testSerializeAndLoadGameStateRoundTripPreservesMetadata() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bR -- -- -- bK -- -- bR",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));

        ChessGame originalGame = new ChessGame();
        originalGame.loadGameState(chessboard, ChessColor.WHITE, "Kq", new ChessboardPosition(2, 3), 12);

        List<String> serialized = originalGame.serializeGameState();

        Chessboard reloadedBoard = new Chessboard();
        reloadedBoard.loadFromLines(new ArrayList<>(serialized.subList(4, serialized.size())));

        ChessGame reloadedGame = new ChessGame();
        reloadedGame.loadGameState(reloadedBoard, ChessColor.WHITE, "Kq", new ChessboardPosition(2, 3), 12);

        assertEquals(serialized, reloadedGame.serializeGameState());
    }
}
