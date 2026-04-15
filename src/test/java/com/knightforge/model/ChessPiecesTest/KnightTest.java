package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KnightTest {
    @Test
    void testInitialSetupValidMoves(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition blackKnight1Position = new ChessboardPosition(0, 1);
        ChessboardPosition blackKnight2Position = new ChessboardPosition(0, 6);
        ChessboardPosition whiteKnight1Position = new ChessboardPosition(7, 1);
        ChessboardPosition whiteKnight2Position = new ChessboardPosition(7, 6);


        ChessPiece blackKnight1 = chessboard.getPieceAtPosition(blackKnight1Position);
        ChessPiece blackKnight2 = chessboard.getPieceAtPosition(blackKnight2Position);
        ChessPiece whiteKnight1 = chessboard.getPieceAtPosition(whiteKnight1Position);
        ChessPiece whiteKnight2 = chessboard.getPieceAtPosition(whiteKnight2Position);

        assertEquals("Knight", blackKnight1.getType());
        assertEquals(ChessColor.BLACK, blackKnight1.getColor());
        assertEquals("Knight", blackKnight2.getType());
        assertEquals(ChessColor.BLACK, blackKnight2.getColor());
        assertEquals("Knight", whiteKnight1.getType());
        assertEquals(ChessColor.WHITE, whiteKnight1.getColor());
        assertEquals("Knight", whiteKnight2.getType());
        assertEquals(ChessColor.WHITE, whiteKnight2.getColor());

        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnight1Position).size());
        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnight2Position).size());

        chessGame.switchTurns();
        assertEquals(2, chessGame.getAllPossibleMoves(blackKnight1Position).size());
        assertEquals(2, chessGame.getAllPossibleMoves(blackKnight2Position).size());
    }

    @Test
    void testBlockedMovementFromStart() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bP -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- wN -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition whiteKnightPosition = new ChessboardPosition(7, 1);

        assertEquals(2, chessGame.getAllPossibleMoves(whiteKnightPosition).size());
    }

    @Test
    void testCantCauseCheckWithMovement() {
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
        ChessGame chessGame = new ChessGame(chessboard);
        chessGame.switchTurns();

        ChessboardPosition blackKnightPosition = new ChessboardPosition(4, 3);

        assertEquals(0, chessGame.getAllPossibleMoves(blackKnightPosition).size());
    }
}