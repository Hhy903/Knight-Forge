package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KingTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackKingPositions = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK);
        List<ChessboardPosition> whiteKingPositions = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE);

        assertEquals(1, blackKingPositions.size());
        assertEquals(1, whiteKingPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whiteKingPositions = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE);
        List<ChessboardPosition> blackKingPositions = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteKingPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackKingPositions.get(0)).size());
    }

    @Test
    void testCanMoveInAllDirections() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);

        assertEquals(8, moveHandler.getValidMoves(ChessColor.WHITE, kingPosition).size());
    }

    @Test
    void testCorrectMovementWithBlockers() {
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
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.BLACK).get(0);

        assertEquals(6, moveHandler.getValidMoves(ChessColor.BLACK, kingPosition).size());
    }

    @Test
    void testStandardCastling() throws PromotionRequiredException {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(castleKingside);

        moveHandler.executeMove(castleKingside);

        assertNull(chessboard.getPieceAtPosition(7, 4));
        assertNull(chessboard.getPieceAtPosition(7, 7));
        assertNotNull(chessboard.getPieceAtPosition(7, 6));
        assertNotNull(chessboard.getPieceAtPosition(7, 5));
        assertEquals(PieceType.KING, chessboard.getPieceAtPosition(7, 6).getType());
        assertEquals(PieceType.ROOK, chessboard.getPieceAtPosition(7, 5).getType());
    }

    @Test
    void testCannotCastleWhileInCheck() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- bR -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

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
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- bR -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        List<MoveNew> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleAfterRookMoved() throws PromotionRequiredException {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition rookPosition = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).stream()
                .filter(pos -> pos.getY() == 7)
                .findFirst()
                .orElseThrow();

        MoveNew moveRookAway = moveHandler.getValidMoves(ChessColor.WHITE, rookPosition).stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(moveRookAway);

        ChessboardPosition movedRookPosition = chessboard.getLocationsOfPiece(PieceType.ROOK, ChessColor.WHITE).stream()
                .filter(pos -> pos.getY() == 6)
                .findFirst()
                .orElseThrow();

        MoveNew moveRookBack = moveHandler.getValidMoves(ChessColor.WHITE, movedRookPosition).stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 7)
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(moveRookBack);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        MoveNew castleKingside = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition).stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);

        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleIntoCheck() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- bR --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        MoveNew castleKingside = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition).stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);

        assertNull(castleKingside);
    }

    @Test
    void testCannotCastleQueensideWithPieceInWay() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR wN -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition kingPosition = chessboard.getLocationsOfPiece(PieceType.KING, ChessColor.WHITE).get(0);
        MoveNew castleQueenside = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition).stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 2)
                .findFirst()
                .orElse(null);

        assertNull(castleQueenside);
    }
}