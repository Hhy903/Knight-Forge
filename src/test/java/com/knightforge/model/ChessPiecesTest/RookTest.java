package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RookTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackRookPositions = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.BLACK);
        List<ChessboardPosition> whiteRookPositions = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE);

        assertEquals(2, blackRookPositions.size());
        assertEquals(2, whiteRookPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whiteRookPositions = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE);
        List<ChessboardPosition> blackRookPositions = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.BLACK);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteRookPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteRookPositions.get(1)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackRookPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackRookPositions.get(1)).size());
    }

    @Test
    void testStandardMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteRookPosition = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(11, moveHandler.getValidMoves(ChessColor.WHITE, whiteRookPosition).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "bP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteRookPosition = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(8, moveHandler.getValidMoves(ChessColor.WHITE, whiteRookPosition).size());
    }

    @Test
    void testCannotCauseCheckWithMovement() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK -- wR -- -- -- bR --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whiteRookPosition = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(5, moveHandler.getValidMoves(ChessColor.WHITE, whiteRookPosition).size());
    }
}