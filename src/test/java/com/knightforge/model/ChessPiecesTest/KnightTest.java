package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KnightTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackKnightPositions = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK);
        List<ChessboardPosition> whiteKnightPositions = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE);

        assertEquals(2, blackKnightPositions.size());
        assertEquals(2, whiteKnightPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whiteKnightPositions = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE);
        List<ChessboardPosition> blackKnightPositions = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK);

        assertEquals(2, moveHandler.getValidMoves(ChessColor.WHITE, whiteKnightPositions.get(0)).size());
        assertEquals(2, moveHandler.getValidMoves(ChessColor.WHITE, whiteKnightPositions.get(1)).size());
        assertEquals(2, moveHandler.getValidMoves(ChessColor.BLACK, blackKnightPositions.get(0)).size());
        assertEquals(2, moveHandler.getValidMoves(ChessColor.BLACK, blackKnightPositions.get(1)).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bP -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- wN -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteKnightPosition = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE).get(0);

        assertEquals(2, moveHandler.getValidMoves(ChessColor.WHITE, whiteKnightPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- wB -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- bN -- -- -- --",
                "-- -- bK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition blackKnightPosition = chessboard.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK).get(0);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackKnightPosition).size());
    }
}