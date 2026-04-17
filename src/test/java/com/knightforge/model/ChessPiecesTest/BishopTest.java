package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BishopTest {

    @Test
    void testInitialSetup() {
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> blackBishopPositions = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK);
        List<ChessboardPosition> whiteBishopPositions = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE);

        assertEquals(2, blackBishopPositions.size());
        assertEquals(2, whiteBishopPositions.size());
    }

    @Test
    void testInitialSetupValidMoves() {
        Chessboard chessboard = new Chessboard();
        MoveHandler moveHandler = new MoveHandler(chessboard);

        List<ChessboardPosition> whiteBishopPositions = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE);
        List<ChessboardPosition> blackBishopPositions = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK);

        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteBishopPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.WHITE, whiteBishopPositions.get(1)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackBishopPositions.get(0)).size());
        assertEquals(0, moveHandler.getValidMoves(ChessColor.BLACK, blackBishopPositions.get(1)).size());
    }

    @Test
    void testBishopMovementInCorner() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(7, moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition).size());
    }

    @Test
    void testBishopMovementInCornerWithOppositeColorBlocker() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(4, moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition).size());
    }

    @Test
    void testBishopMovementInCornerWithBlocker() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(3, moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition).size());
    }

    @Test
    void testBishopMovementComplex() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bP",
                "-- -- wQ -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wR -- -- -- --",
                "-- -- -- -- -- -- wP --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(7, moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition).size());
    }

    @Test
    void testBishopMoveCannotCauseCheck() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bB",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wB -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE).get(0);

        assertEquals(4, moveHandler.getValidMoves(ChessColor.WHITE, bishopPosition).size());
    }

    @Test
    void testBishopMoveCannotCauseCheckMustTake() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wB -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);

        ChessboardPosition bishopPosition = chessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE).get(0);

        assertEquals(1, moveHandler.getValidMoves(ChessColor.WHITE, bishopPosition).size());
    }
}