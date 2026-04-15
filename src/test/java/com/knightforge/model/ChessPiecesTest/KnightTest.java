package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KnightTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackKnightPositions = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK);
        List<ChessboardPosition> whiteKnightPositions = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE);
        int expectedNumberOfKnightsPerColor = 2;

        assertEquals(expectedNumberOfKnightsPerColor, blackKnightPositions.size());
        assertEquals(expectedNumberOfKnightsPerColor, whiteKnightPositions.size());
    }

    @Test
    void testInitialSetupValidMoves(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackKnightPositions = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK);
        List<ChessboardPosition> whiteKnightPositions = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE);

        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnightPositions.get(0)).size());
        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnightPositions.get(1)).size());
        chessGame.switchTurns();
        assertEquals(2, chessGame.getAllPossibleMoves(blackKnightPositions.get(0)).size());
        assertEquals(2, chessGame.getAllPossibleMoves(blackKnightPositions.get(1)).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bP -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- wN -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteKnightPosition = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.WHITE).get(0);

        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnightPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- wB -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- bN -- -- -- --",
                "-- -- bK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition blackKnightPosition = chessGame.getLocationsOfPiece(PieceType.KNIGHT, ChessColor.BLACK).get(0);

        assertEquals(0, chessGame.getAllPossibleMoves(blackKnightPosition).size());
    }
}