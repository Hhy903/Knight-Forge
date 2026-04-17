package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueenTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackQueenPositions = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.BLACK);
        List<ChessboardPosition> whiteQueenPositions = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE);

        assertEquals(1, blackQueenPositions.size());
        assertEquals(1, whiteQueenPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whiteQueenPositions = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE);
        List<ChessboardPosition> blackQueenPositions = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.BLACK);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteQueenPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackQueenPositions.get(0)).size());
    }

    @Test
    void testStandardMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteQueenPosition = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(25, moveHandler.getValidMoves(ChessColor.WHITE, whiteQueenPosition).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- bP -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteQueenPosition = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(15, moveHandler.getValidMoves(ChessColor.WHITE, whiteQueenPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "wK -- -- -- wP -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- bP -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteQueenPosition = chessboard.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(3, moveHandler.getValidMoves(ChessColor.WHITE, whiteQueenPosition).size());
    }
}