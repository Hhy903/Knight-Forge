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

    @Test
    void testStandardCastling() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        // Kingside castle
        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(castleKingside);

        moveHandler.executeMove(castleKingside);

        // Verify king and rook positions after castling
        assertNull(loadedChessboard.getPieceAtPosition(7, 4));
        assertNull(loadedChessboard.getPieceAtPosition(7, 7));
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 6));
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 5));
        assertEquals(PieceType.KING, loadedChessboard.getPieceAtPosition(7, 6).getType());
        assertEquals(PieceType.ROOK, loadedChessboard.getPieceAtPosition(7, 5).getType());
    }

    @Test
    void testCannotCastleWhileInCheck() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bR -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        // Should not be able to castle (kingside or queenside)
        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNull(castleKingside);

        MoveNew castleQueenside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 2)
                .findFirst()
                .orElse(null);
        assertNull(castleQueenside);
    }

    @Test
    void testCannotCastleThroughAttack() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- bR -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        // Should not be able to castle kingside (f1 is under attack)
        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleAfterRookMoved() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        // Move the kingside rook and then move it back
        ChessboardPosition rookPosition = loadedChessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).stream()
                .filter(pos -> pos.getY() == 7)
                .findFirst()
                .orElse(null);
        assertNotNull(rookPosition);

        List<MoveNew> rookMoves = moveHandler.getValidMoves(ChessColor.WHITE, rookPosition);
        MoveNew moveRookAway = rookMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(moveRookAway);
        moveHandler.executeMove(moveRookAway);

        // Move rook back to original position
        ChessboardPosition movedRookPosition = loadedChessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).stream()
                .filter(pos -> pos.getY() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(movedRookPosition);

        List<MoveNew> rookMovesBack = moveHandler.getValidMoves(ChessColor.WHITE, movedRookPosition);
        MoveNew moveRookBack = rookMovesBack.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 7)
                .findFirst()
                .orElse(null);
        assertNotNull(moveRookBack);
        moveHandler.executeMove(moveRookBack);

        // Now try to castle - should not be allowed
        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleIntoCheck() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- bR --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        // Should not be able to castle kingside (g1 is under attack where king would land)
        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleQueensideWithPieceInWay() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR wN -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition kingPosition = loadedChessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        // Should not be able to castle queenside (knight is blocking the path)
        MoveNew castleQueenside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 2)
                .findFirst()
                .orElse(null);
        assertNull(castleQueenside);
    }
}