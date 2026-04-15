package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueenTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackQueenPositions = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.BLACK);
        List<ChessboardPosition> whiteQueenPositions = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE);
        int expectedNumberOfQueensPerColor = 1;

        assertEquals(expectedNumberOfQueensPerColor, blackQueenPositions.size());
        assertEquals(expectedNumberOfQueensPerColor, whiteQueenPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackQueenPositions = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.BLACK);
        List<ChessboardPosition> whiteQueenPositions = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE);

        assertEquals(0, chessGame.getAllPossibleMoves(whiteQueenPositions.get(0)).size());
        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(blackQueenPositions.get(0)).size());
    }

    @Test
    void testStandardMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteQueenPosition = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(25, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- bP -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteQueenPosition = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(15, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "wK -- -- -- wP -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- wQ -- -- -- -- --",
                "-- bP -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteQueenPosition = chessGame.getLocationsOfPiece(PieceType.QUEEN, ChessColor.WHITE).get(0);

        assertEquals(3, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
    }
}