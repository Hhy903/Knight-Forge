package com.knightforge.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MoveHandlerTest {
    @Test
    void testMakeSimpleMove() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition bishopPosition = loadedChessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);
        ChessColor whoseTurn = ChessColor.BLACK;
        List<MoveNew> possibleBishopMoves =moveHandler.getValidMoves(whoseTurn,bishopPosition);

        MoveNew moveToOppositeCorner = possibleBishopMoves.stream()
                .filter(move -> move.getTo().getX() == 0 && move.getTo().getY() == 7)
                .findFirst()
                .orElse(null);
        assertNotNull(moveToOppositeCorner);

        moveHandler.executeMove(moveToOppositeCorner);

        assertNull(loadedChessboard.getPieceAtPosition(7,0));
        assertNotNull(loadedChessboard.getPieceAtPosition(0,7));
        assertTrue(Objects.equals(loadedChessboard.getPieceAtPosition(0, 7).getType(), PieceType.BISHOP.name()));
    }

    @Test
    void testMakeCaptureMove() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition bishopPosition = loadedChessboard.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);
        ChessColor whoseTurn = ChessColor.BLACK;
        List<MoveNew> possibleBishopMoves = moveHandler.getValidMoves(whoseTurn, bishopPosition);

        MoveNew capturePawn = possibleBishopMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 4)
                .findFirst()
                .orElse(null);
        assertNotNull(capturePawn);

        moveHandler.executeMove(capturePawn);

        assertNull(loadedChessboard.getPieceAtPosition(7, 0));
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 4));
        assertEquals(PieceType.BISHOP.name(), loadedChessboard.getPieceAtPosition(3, 4).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(3, 4).getColor());
    }

    @Test
    void testEnPassantCapture() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        // First move: Black pawn moves two squares forward
        ChessboardPosition blackPawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK).get(0);
        List<MoveNew> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        MoveNew blackPawnTwoSquares = blackPawnMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(blackPawnTwoSquares);

        moveHandler.executeMove(blackPawnTwoSquares);

        // Second move: White pawn captures black pawn en passant
        ChessboardPosition whitePawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);
        List<MoveNew> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        MoveNew enPassantCapture = whitePawnMoves.stream()
                .filter(move -> move.getTo().getX() == 2 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(enPassantCapture);

        moveHandler.executeMove(enPassantCapture);

        // Verify: Black pawn is captured, white pawn is in the en passant position
        assertNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(2, 3));
        assertEquals(PieceType.PAWN.name(), loadedChessboard.getPieceAtPosition(2, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(2, 3).getColor());
        assertNull(loadedChessboard.getPieceAtPosition(3, 2));
    }
}
