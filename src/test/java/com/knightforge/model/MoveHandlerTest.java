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
        List<Move> possibleBishopMoves =moveHandler.getValidMoves(whoseTurn,bishopPosition);

        Move moveToOppositeCorner = possibleBishopMoves.stream()
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
        List<Move> possibleBishopMoves = moveHandler.getValidMoves(whoseTurn, bishopPosition);

        Move capturePawn = possibleBishopMoves.stream()
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
        List<Move> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        Move blackPawnTwoSquares = blackPawnMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(blackPawnTwoSquares);

        moveHandler.executeMove(blackPawnTwoSquares);

        // Second move: White pawn captures black pawn en passant
        ChessboardPosition whitePawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);
        List<Move> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        Move enPassantCapture = whitePawnMoves.stream()
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
        List<Move> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        Move blackPawnTwoSquares = blackPawnMoves.stream()
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
        List<Move> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        assertEquals(1, whitePawnMoves.size());

        Move enPassantCapture = whitePawnMoves.stream()
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
        List<Move> possibleBishopMoves = moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition);

        Move moveToOppositeCorner = possibleBishopMoves.stream()
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
        List<Move> possibleBishopMoves = moveHandler.getValidMoves(ChessColor.BLACK, bishopPosition);

        Move capturePawn = possibleBishopMoves.stream()
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
        List<Move> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, kingPosition);

        Move castleKingside = kingMoves.stream()
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
        List<Move> blackPawnMoves = moveHandler.getValidMoves(ChessColor.BLACK, blackPawnPosition);

        Move blackPawnTwoSquares = blackPawnMoves.stream()
                .filter(move -> move.getTo().getX() == 3 && move.getTo().getY() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(blackPawnTwoSquares);

        moveHandler.executeMove(blackPawnTwoSquares);

        // Second move: White pawn captures en passant
        ChessboardPosition whitePawnPosition = loadedChessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE).get(0);
        List<Move> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, whitePawnPosition);

        Move enPassantCapture = whitePawnMoves.stream()
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

    @Test
    void testPromotionMoveRequiresExplicitPromotionChoice() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "wP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition pawnPosition = new ChessboardPosition(1, 0);
        Move promotionMove = moveHandler.getValidMoves(ChessColor.WHITE, pawnPosition).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(0, 0)))
                .findFirst()
                .orElse(null);

        assertNotNull(promotionMove);
        assertThrows(PromotionRequiredException.class, () -> moveHandler.executeMove(promotionMove));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(1, 0).getType());
        assertNull(loadedChessboard.getPieceAtPosition(0, 0));
    }

    @Test
    void testPromotionCreatesChosenPieceAndUndoRestoresPawn() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "-- -- -- wP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition pawnPosition = new ChessboardPosition(1, 3);
        Move promotionMove = moveHandler.getValidMoves(ChessColor.WHITE, pawnPosition).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(0, 3)))
                .findFirst()
                .orElseThrow();

        moveHandler.executePromotionMove(promotionMove, PieceType.KNIGHT);

        assertNull(loadedChessboard.getPieceAtPosition(1, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(0, 3));
        assertEquals(PieceType.KNIGHT, loadedChessboard.getPieceAtPosition(0, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(0, 3).getColor());

        assertTrue(moveHandler.undoLastMove());
        assertNotNull(loadedChessboard.getPieceAtPosition(1, 3));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(1, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(1, 3).getColor());
        assertNull(loadedChessboard.getPieceAtPosition(0, 3));
    }

    @Test
    void testPromotionCaptureRestoresCapturedPieceOnUndo() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- bR -- -- bK -- -- --",
                "wP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        ChessboardPosition pawnPosition = new ChessboardPosition(1, 0);
        Move promotionCapture = moveHandler.getValidMoves(ChessColor.WHITE, pawnPosition).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(0, 1)))
                .findFirst()
                .orElseThrow();

        moveHandler.executePromotionMove(promotionCapture, PieceType.QUEEN);

        assertNull(loadedChessboard.getPieceAtPosition(1, 0));
        assertNotNull(loadedChessboard.getPieceAtPosition(0, 1));
        assertEquals(PieceType.QUEEN, loadedChessboard.getPieceAtPosition(0, 1).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(0, 1).getColor());

        assertTrue(moveHandler.undoLastMove());
        assertNotNull(loadedChessboard.getPieceAtPosition(1, 0));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(1, 0).getType());
        assertNotNull(loadedChessboard.getPieceAtPosition(0, 1));
        assertEquals(PieceType.ROOK, loadedChessboard.getPieceAtPosition(0, 1).getType());
        assertEquals(ChessColor.BLACK, loadedChessboard.getPieceAtPosition(0, 1).getColor());
    }

    @Test
    void testEnPassantExpiresIfNotTakenImmediately() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "-- -- -- bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- wN --",
                "-- -- -- -- wK -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        Move blackPawnTwoSquares = moveHandler.getValidMoves(ChessColor.BLACK, new ChessboardPosition(1, 3)).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(3, 3)))
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(blackPawnTwoSquares);

        Move whiteKnightMove = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(6, 6)).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(4, 5)))
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(whiteKnightMove);

        List<Move> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(3, 2));
        Move expiredEnPassant = whitePawnMoves.stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(2, 3)))
                .findFirst()
                .orElse(null);

        assertNull(expiredEnPassant);
    }

    @Test
    void testCannotCastleAfterKingMovedAwayAndBack() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);

        Move moveKingAway = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(7, 4)).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(6, 4)))
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(moveKingAway);

        Move moveKingBack = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(6, 4)).stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(7, 4)))
                .findFirst()
                .orElseThrow();
        moveHandler.executeMove(moveKingBack);

        List<Move> kingMoves = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(7, 4));
        Move castleKingside = kingMoves.stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(7, 6)))
                .findFirst()
                .orElse(null);
        Move castleQueenside = kingMoves.stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(7, 2)))
                .findFirst()
                .orElse(null);

        assertNull(castleKingside);
        assertNull(castleQueenside);
    }

    @Test
    void testLoadMetadataDisablesCastlingRights() {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "bR -- -- -- bK -- -- bR",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);
        moveHandler.loadMetadata(ChessColor.WHITE, "-", null);

        List<Move> whiteKingMoves = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(7, 4));
        List<Move> blackKingMoves = moveHandler.getValidMoves(ChessColor.BLACK, new ChessboardPosition(0, 4));

        assertNull(whiteKingMoves.stream().filter(move -> move.isCastleMove()).findFirst().orElse(null));
        assertNull(blackKingMoves.stream().filter(move -> move.isCastleMove()).findFirst().orElse(null));
        assertEquals("-", moveHandler.getCastleRightsToken());
    }

    @Test
    void testLoadMetadataRestoresEnPassantTargetForImmediateCaptureOnly() throws PromotionRequiredException {
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));
        MoveHandler moveHandler = new MoveHandler(loadedChessboard);
        moveHandler.loadMetadata(ChessColor.WHITE, "-", new ChessboardPosition(2, 3));

        List<Move> whitePawnMoves = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(3, 2));
        Move enPassantCapture = whitePawnMoves.stream()
                .filter(move -> move.getTo().equals(new ChessboardPosition(2, 3)))
                .findFirst()
                .orElse(null);

        assertNotNull(enPassantCapture);
        assertEquals(new ChessboardPosition(2, 3), moveHandler.getEnPassantTarget());

        moveHandler.executeMove(enPassantCapture);

        assertNull(moveHandler.getEnPassantTarget());
        assertNull(loadedChessboard.getPieceAtPosition(3, 3));
        assertNotNull(loadedChessboard.getPieceAtPosition(2, 3));
        assertEquals(PieceType.PAWN, loadedChessboard.getPieceAtPosition(2, 3).getType());
        assertEquals(ChessColor.WHITE, loadedChessboard.getPieceAtPosition(2, 3).getColor());
    }
}
