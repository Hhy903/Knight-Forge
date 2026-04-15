package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackPawnPositions = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK);
        List<ChessboardPosition> whitePawnPositions = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE);
        int expectedNumberOfPawnsPerColor = 8;

        assertEquals(expectedNumberOfPawnsPerColor, blackPawnPositions.size());
        assertEquals(expectedNumberOfPawnsPerColor, whitePawnPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> whitePawnPositions = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE);
        List<ChessboardPosition> blackPawnPositions = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK);

        // Test white pawns on edge and in middle
        ChessboardPosition whitePawnPositionOnEdge = whitePawnPositions.stream()
                .filter(pos -> pos.getY() == 0)
                .findFirst().orElseThrow();
        ChessboardPosition whitePawnPositionInMiddle = whitePawnPositions.stream()
                .filter(pos -> pos.getY() == 3)
                .findFirst().orElseThrow();

        assertEquals(2, chessGame.getAllPossibleMoves(whitePawnPositionOnEdge).size());
        assertEquals(2, chessGame.getAllPossibleMoves(whitePawnPositionInMiddle).size());

        chessGame.switchTurns();

        // Test black pawns on edge and in middle
        ChessboardPosition blackPawnPositionOnEdge = blackPawnPositions.stream()
                .filter(pos -> pos.getY() == 0)
                .findFirst().orElseThrow();
        ChessboardPosition blackPawnPositionInMiddle = blackPawnPositions.stream()
                .filter(pos -> pos.getY() == 3)
                .findFirst().orElseThrow();

        assertEquals(2, chessGame.getAllPossibleMoves(blackPawnPositionOnEdge).size());
        assertEquals(2, chessGame.getAllPossibleMoves(blackPawnPositionInMiddle).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- bP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whitePawnPosition = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(1, chessGame.getAllPossibleMoves(whitePawnPosition).size());
    }

    @Test
    void testOptionalAttack() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- bP -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whitePawnPosition = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(3, chessGame.getAllPossibleMoves(whitePawnPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- bB --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- wK -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whitePawnPosition = chessGame.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);

        assertEquals(0, chessGame.getAllPossibleMoves(whitePawnPosition).size());
    }
}