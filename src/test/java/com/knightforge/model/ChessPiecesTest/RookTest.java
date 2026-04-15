package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RookTest {
    @Test
    void testInitialSetupValidMoves(){
        Chessboard chessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition blackRook1Position = new ChessboardPosition(0, 0);
        ChessboardPosition blackRook2Position = new ChessboardPosition(0, 7);
        ChessboardPosition whiteRook1Position = new ChessboardPosition(7, 0);
        ChessboardPosition whiteRook2Position = new ChessboardPosition(7, 7);

        ChessPiece blackRook1 = chessboard.getPieceAtPosition(blackRook1Position);
        ChessPiece blackRook2 = chessboard.getPieceAtPosition(blackRook2Position);
        ChessPiece whiteRook1 = chessboard.getPieceAtPosition(whiteRook1Position);
        ChessPiece whiteRook2 = chessboard.getPieceAtPosition(whiteRook2Position);

        assertEquals("Rook", blackRook1.getType());
        assertEquals(ChessColor.BLACK, blackRook1.getColor());
        assertEquals("Rook", blackRook2.getType());
        assertEquals(ChessColor.BLACK, blackRook2.getColor());
        assertEquals("Rook", whiteRook1.getType());
        assertEquals(ChessColor.WHITE, whiteRook1.getColor());
        assertEquals("Rook", whiteRook2.getType());
        assertEquals(ChessColor.WHITE, whiteRook2.getColor());

        assertEquals(0, chessGame.getAllPossibleMoves(whiteRook1Position).size());
        assertEquals(0, chessGame.getAllPossibleMoves(whiteRook2Position).size());

        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(blackRook1Position).size());
        assertEquals(0, chessGame.getAllPossibleMoves(blackRook2Position).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteRookPosition = new ChessboardPosition(3, 2);

        assertEquals(11, chessGame.getAllPossibleMoves(whiteRookPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteRookPosition = new ChessboardPosition(5, 0);

        assertEquals(8, chessGame.getAllPossibleMoves(whiteRookPosition).size());
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
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteRookPosition = new ChessboardPosition(3, 2);

        assertEquals(5, chessGame.getAllPossibleMoves(whiteRookPosition).size());
    }
}
