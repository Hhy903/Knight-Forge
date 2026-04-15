package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BishopTest {
    @Test
    void testInitialSetupValidMoves(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();

        ChessboardPosition blackBishop1Position = new ChessboardPosition(0, 2);
        ChessboardPosition blackBishop2Position = new ChessboardPosition(0, 5);
        ChessboardPosition whiteBishop1Position = new ChessboardPosition(7, 2);
        ChessboardPosition whiteBishop2Position = new ChessboardPosition(7, 5);


        ChessPiece blackBishop1 = chessboard.getPieceAtPosition(blackBishop1Position);
        ChessPiece blackBishop2 = chessboard.getPieceAtPosition(blackBishop2Position);
        ChessPiece whiteBishop1 = chessboard.getPieceAtPosition(whiteBishop1Position);
        ChessPiece whiteBishop2 = chessboard.getPieceAtPosition(whiteBishop2Position);

        assertEquals("Bishop", blackBishop1.getType());
        assertEquals(ChessColor.BLACK, blackBishop1.getColor());
        assertEquals("Bishop", blackBishop2.getType());
        assertEquals(ChessColor.BLACK, blackBishop2.getColor());
        assertEquals("Bishop", whiteBishop1.getType());
        assertEquals(ChessColor.WHITE, whiteBishop1.getColor());
        assertEquals("Bishop", whiteBishop2.getType());
        assertEquals(ChessColor.WHITE, whiteBishop2.getColor());

        assertEquals(0, chessboard.getPotentiallyLegalMoves(blackBishop1Position, moveHistory).size());
        assertEquals(0, chessboard.getPotentiallyLegalMoves(blackBishop2Position, moveHistory).size());
        assertEquals(0, chessboard.getPotentiallyLegalMoves(whiteBishop1Position, moveHistory).size());
        assertEquals(0, chessboard.getPotentiallyLegalMoves(whiteBishop2Position, moveHistory).size());
    }

    @Test
    void testBishopMovementInCorner(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();

        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));

        ChessboardPosition bishopPosition = new ChessboardPosition(7, 0);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(7, chessboard.getPotentiallyLegalMoves(bishopPosition, moveHistory).size());
    }

    @Test
    void testBishopMovementInCornerWithOppositeColorBlocker(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));

        ChessboardPosition bishopPosition = new ChessboardPosition(7, 0);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(4, chessboard.getPotentiallyLegalMoves(bishopPosition, moveHistory).size());
    }
    @Test
    void testBishopMovementInCornerWithBlocker(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));

        ChessboardPosition bishopPosition = new ChessboardPosition(7, 0);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(3, chessboard.getPotentiallyLegalMoves(bishopPosition, moveHistory).size());
    }

    @Test
    void testBishopMovementComplex(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bP",
                "-- -- wQ -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wR -- -- -- --",
                "-- -- -- -- -- -- wP --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));

        ChessboardPosition bishopPosition = new ChessboardPosition(3, 4);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(7, chessboard.getPotentiallyLegalMoves(bishopPosition, moveHistory).size());
    }

    @Test
    void testBishopMoveCannotCauseCheck(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bB",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wB -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition bishopPosition = new ChessboardPosition(3, 4);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(4, chessGame.getAllPossibleMoves(bishopPosition).size());
    }
    @Test
    void testBishopMoveCannotCauseCheckMustTake(){
        Chessboard chessboard = new Chessboard();
        List<MoveNew> moveHistory = new ArrayList<>();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wB -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(chessboard);

        ChessboardPosition bishopPosition = new ChessboardPosition(4, 3);

        assertEquals("Bishop", chessboard.getPieceAtPosition(bishopPosition).getType());
        assertEquals(1, chessGame.getAllPossibleMoves(bishopPosition).size());
    }

}
