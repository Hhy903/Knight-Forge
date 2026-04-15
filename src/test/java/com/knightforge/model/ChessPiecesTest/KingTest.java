package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KingTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackKingPositions = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK);
        List<ChessboardPosition> whiteKingPositions = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE);
        int expectedNumberOfKingsPerColor = 1;

        assertEquals(expectedNumberOfKingsPerColor, blackKingPositions.size());
        assertEquals(expectedNumberOfKingsPerColor, whiteKingPositions.size());
    }

    @Test
    void testInitialSetupValidMoves(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackKingPositions = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK);
        List<ChessboardPosition> whiteKingPositions = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE);

        assertEquals(0, chessGame.getAllPossibleMoves(whiteKingPositions.get(0)).size());
        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(blackKingPositions.get(0)).size());
    }

    @Test
    void testCanMoveInAllDirections(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition kingPosition = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);

        assertEquals(8, chessGame.getAllPossibleMoves(kingPosition).size());
    }

    @Test
    void testCorrectMovementWithBlockers(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- bK bP -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition kingPosition = chessGame.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK).get(0);

        assertEquals(6, chessGame.getAllPossibleMoves(kingPosition).size());
    }
}