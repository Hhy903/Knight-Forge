package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KingTest {
    @Test
    void testInitialSetupValidMoves(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();

        ChessboardPosition initalKingPositionBlack = new ChessboardPosition(0, 4);
        ChessboardPosition initalKingPositionWhite = new ChessboardPosition(7, 4);

        ChessPiece blackKing = chessboard.getPieceAtPosition(initalKingPositionBlack);
        ChessPiece whiteKing = chessboard.getPieceAtPosition(initalKingPositionWhite);

        assertEquals("King", blackKing.getType());
        assertEquals(ChessColor.BLACK, blackKing.getColor());
        assertEquals("King", whiteKing.getType());
        assertEquals(ChessColor.WHITE, whiteKing.getColor());

        assertEquals(0, chessboard.getPotentiallyLegalMoves(initalKingPositionBlack, moveHistory).size());
        assertEquals(0, chessboard.getPotentiallyLegalMoves(initalKingPositionWhite, moveHistory).size());
    }

    @Test
    void testCanMoveInAllDirections(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();

        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessboardPosition kingPosition = new ChessboardPosition(1, 2);

        assertEquals(8, chessboard.getPotentiallyLegalMoves(kingPosition, moveHistory).size());
    }

    @Test
    void testCorrectMovementWithBlockers(){
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- bK bP -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(chessboard);
        chessGame.switchTurns();

        ChessboardPosition kingPosition = new ChessboardPosition(1, 2);

        assertEquals(6, chessGame.getAllPossibleMoves(kingPosition).size());
    }
}
