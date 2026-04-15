package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RookTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackRookPositions = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.BLACK);
        List<ChessboardPosition> whiteRookPositions = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE);
        int expectedNumberOfRooksPerColor = 2;

        assertEquals(expectedNumberOfRooksPerColor, blackRookPositions.size());
        assertEquals(expectedNumberOfRooksPerColor, whiteRookPositions.size());
    }

    @Test
    void testInitialSetupValidMoves(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackRookPositions = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.BLACK);
        List<ChessboardPosition> whiteRookPositions = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE);

        assertEquals(0, chessGame.getAllPossibleMoves(whiteRookPositions.get(0)).size());
        assertEquals(0, chessGame.getAllPossibleMoves(whiteRookPositions.get(1)).size());
        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(blackRookPositions.get(0)).size());
        assertEquals(0, chessGame.getAllPossibleMoves(blackRookPositions.get(1)).size());
    }

    @Test
    void testStandardMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wR -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteRookPosition = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(11, chessGame.getAllPossibleMoves(whiteRookPosition).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "bP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteRookPosition = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(8, chessGame.getAllPossibleMoves(whiteRookPosition).size());
    }

    @Test
    void testCannotCauseCheckWithMovement() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK -- wR -- -- -- bR --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition whiteRookPosition = chessGame.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).get(0);

        assertEquals(5, chessGame.getAllPossibleMoves(whiteRookPosition).size());
    }
}