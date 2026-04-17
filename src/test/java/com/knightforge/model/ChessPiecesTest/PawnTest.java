package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackPawnPositions = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK);
        List<ChessboardPosition> whitePawnPositions = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE);

        assertEquals(8, blackPawnPositions.size());
        assertEquals(8, whitePawnPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whitePawnPositions = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE);
        List<ChessboardPosition> blackPawnPositions = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK);

        ChessboardPosition whitePawnOnEdge = whitePawnPositions.stream()
                .filter(pos -> pos.getY() == 0)
                .findFirst().orElseThrow();
        ChessboardPosition whitePawnInMiddle = whitePawnPositions.stream()
                .filter(pos -> pos.getY() == 3)
                .findFirst().orElseThrow();

        assertEquals(2, moveHandler.getValidMoves(ChessColor.WHITE, whitePawnOnEdge).size());
        assertEquals(2, moveHandler.getValidMoves(ChessColor.WHITE, whitePawnInMiddle).size());

        ChessboardPosition blackPawnOnEdge = blackPawnPositions.stream()
                .filter(pos -> pos.getY() == 0)
                .findFirst().orElseThrow();
        ChessboardPosition blackPawnInMiddle = blackPawnPositions.stream()
                .filter(pos -> pos.getY() == 3)
                .findFirst().orElseThrow();

        assertEquals(2, moveHandler.getValidMoves(ChessColor.BLACK, blackPawnOnEdge).size());
        assertEquals(2, moveHandler.getValidMoves(ChessColor.BLACK, blackPawnInMiddle).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- bP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whitePawnPosition = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(1, moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition).size());
    }

    @Test
    void testOptionalAttack() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- bP -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whitePawnPosition = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(3, moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- bB --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- wK -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition whitePawnPosition = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovementBlack() {
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