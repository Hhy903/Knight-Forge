package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueenTest {
    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition initialQueenPositionBlack = new ChessboardPosition(0, 3);
        ChessboardPosition initialQueenPositionWhite = new ChessboardPosition(7, 3);

        ChessPiece blackQueen = chessboard.getPieceAtPosition(initialQueenPositionBlack);
        ChessPiece whiteQueen = chessboard.getPieceAtPosition(initialQueenPositionWhite);

        assertEquals("Queen", blackQueen.getType());
        assertEquals(ChessColor.BLACK, blackQueen.getColor());
        assertEquals("Queen", whiteQueen.getType());
        assertEquals(ChessColor.WHITE, whiteQueen.getColor());

        assertEquals(0, chessGame.getAllPossibleMoves(initialQueenPositionWhite).size());
        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(initialQueenPositionBlack).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteQueenPosition = new ChessboardPosition(3, 2);

        assertEquals(25, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteQueenPosition = new ChessboardPosition(3, 2);

        assertEquals(15, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteQueenPosition = new ChessboardPosition(3, 2);

        assertEquals(3, chessGame.getAllPossibleMoves(whiteQueenPosition).size());
    }

}
