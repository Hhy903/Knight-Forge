package com.knightforge.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MoveHandlerTest {
    @Test
    void testMakeSimpleMove() throws PromotionRequiredException {
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
        assertTrue(Objects.equals(loadedChessboard.getPieceAtPosition(0, 7).getType(), PieceType.BISHOP));
    }

    @Test
    void testMakeCaptureMove() throws PromotionRequiredException {
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
        assertEquals(PieceType.BISHOP, loadedChessboard.getPieceAtPosition(3, 4).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(3, 4).getColor());
    }

    @Test
    void testEnPassantCapture() throws PromotionRequiredException {
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
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(2, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(2, 3).getColor());
        assertNull(loadedChessboard.getPieceAtPosition(3, 2));
    }

    @Test
    void testEnPassantCapturesCheckingPawn() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- wK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        // First move: Black pawn moves two squares, giving check to the white king
        ChessboardPosition blackPawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK).get(0);
        List<MoveNew> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        MoveNew blackPawnTwoSquares = blackPawnMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(blackPawnTwoSquares);

        moveHandler.executeMove(blackPawnTwoSquares);

        // Verify black pawn is giving check (diagonally attacking the king at row 4, col 4)
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(4, 4));

        // Second move: White pawn captures en passant to escape check
        ChessboardPosition whitePawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);
        List<MoveNew> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        assertEquals(1, whitePawnMoves.size());

        MoveNew enPassantCapture = whitePawnMoves.stream()
                .filter(move -> move.getTo().getX() == 2 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(enPassantCapture);

        moveHandler.executeMove(enPassantCapture);

        // Verify: Black pawn is captured via en passant, check is resolved
        assertNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(2, 3));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(2, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(2, 3).getColor());
        assertNotNull(loadedChessboard.getPieceAtPosition(4, 4));
        assertEquals(PieceType.KING, loadedChessboard.getPieceAtPosition(4, 4).getType());
    }

    @Test
    void testUndoBasicMove() throws PromotionRequiredException {
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
        List<MoveNew> possibleBishopMoves = moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition);

        MoveNew moveToOppositeCorner = possibleBishopMoves.stream()
                .filter(move -> move.getTo().getX() == 0 && move.getTo().getY() == 7)
                .findFirst()
                .orElse(null);
        assertNotNull(moveToOppositeCorner);

        moveHandler.executeMove(moveToOppositeCorner);

        // Verify move was executed
        assertNull(loadedChessboard.getPieceAtPosition(7, 0));
        assertNotNull(loadedChessboard.getPieceAtPosition(0, 7));

        moveHandler.undoLastMove();

        // Verify board is back to original state
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 0));
        assertNull(loadedChessboard.getPieceAtPosition(0, 7));
        assertEquals(PieceType.BISHOP, loadedChessboard.getPieceAtPosition(7, 0).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(7, 0).getColor());
    }

    @Test
    void testUndoCaptureMove() throws PromotionRequiredException {
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
        List<MoveNew> possibleBishopMoves = moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition);

        MoveNew capturePawn = possibleBishopMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 4)
                .findFirst()
                .orElse(null);
        assertNotNull(capturePawn);

        moveHandler.executeMove(capturePawn);

        // Verify capture was executed
        assertNull(loadedChessboard.getPieceAtPosition(7, 0));
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 4));
        assertEquals(PieceType.BISHOP, loadedChessboard.getPieceAtPosition(3, 4).getType());

        moveHandler.undoLastMove();

        // Verify board is back to original state with captured pawn restored
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 0));
        assertEquals(PieceType.BISHOP, loadedChessboard.getPieceAtPosition(7, 0).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(7, 0).getColor());
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 4));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(3, 4).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(3, 4).getColor());
    }

    @Test
    void testUndoCastlingMove() throws PromotionRequiredException {
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

        MoveNew castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().getX() == 7 && move.getTo().getY() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(castleKingside);

        moveHandler.executeMove(castleKingside);

        // Verify castling was executed
        assertNull(loadedChessboard.getPieceAtPosition(7, 4));
        assertNull(loadedChessboard.getPieceAtPosition(7, 7));
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 6));
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 5));

        moveHandler.undoLastMove();

        // Verify board is back to original state
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 4));
        assertEquals(PieceType.KING, loadedChessboard.getPieceAtPosition(7, 4).getType());
        assertNotNull(loadedChessboard.getPieceAtPosition(7, 7));
        assertEquals(PieceType.ROOK, loadedChessboard.getPieceAtPosition(7, 7).getType());
        assertNull(loadedChessboard.getPieceAtPosition(7, 6));
        assertNull(loadedChessboard.getPieceAtPosition(7, 5));
    }

    @Test
    void testUndoEnPassantMove() throws PromotionRequiredException {
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

        // First move: Black pawn moves two squares
        ChessboardPosition blackPawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.BLACK).get(0);
        List<MoveNew> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        MoveNew blackPawnTwoSquares = blackPawnMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(blackPawnTwoSquares);

        moveHandler.executeMove(blackPawnTwoSquares);

        // Second move: White pawn captures en passant
        ChessboardPosition whitePawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);
        List<MoveNew> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        MoveNew enPassantCapture = whitePawnMoves.stream()
                .filter(move -> move.getTo().getX() == 2 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(enPassantCapture);

        moveHandler.executeMove(enPassantCapture);

        // Verify en passant was executed
        assertNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(2, 3));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(2, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(2, 3).getColor());

        moveHandler.undoLastMove();

        // Verify board is back to state before en passant
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 2));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(3, 2).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(3, 2).getColor());
        assertNotNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(3, 3).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(3, 3).getColor());
        assertNull(loadedChessboard.getPieceAtPosition(2, 3));
    }
}
