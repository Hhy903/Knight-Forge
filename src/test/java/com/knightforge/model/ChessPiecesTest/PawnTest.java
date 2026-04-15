package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PawnTest {
    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whitePawnPositionOnEdge = new ChessboardPosition(6, 0);
        ChessboardPosition whitePawnPositionInMiddle = new ChessboardPosition(6, 3);
        ChessboardPosition blackPawnPositionOnEdge = new ChessboardPosition(1, 0);
        ChessboardPosition blackPawnPositionInMiddle = new ChessboardPosition(1, 3);

        assertEquals(2, chessGame.getAllPossibleMoves(whitePawnPositionOnEdge).size());
        assertEquals(2, chessGame.getAllPossibleMoves(whitePawnPositionInMiddle).size());
        chessGame.switchTurns();
        assertEquals(2, chessGame.getAllPossibleMoves(blackPawnPositionOnEdge).size());
        assertEquals(2, chessGame.getAllPossibleMoves(blackPawnPositionInMiddle).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- bP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whitePawnPosition = new ChessboardPosition(6, 2);

        assertEquals(1, chessGame.getAllPossibleMoves(whitePawnPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whitePawnPosition = new ChessboardPosition(6, 2);

        assertEquals(3, chessGame.getAllPossibleMoves(whitePawnPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whitePawnPosition = new ChessboardPosition(6, 2);

        assertEquals(0, chessGame.getAllPossibleMoves(whitePawnPosition).size());
    }
}