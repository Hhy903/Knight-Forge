package com.knightforge.model.ChessPiecesTest;

import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BishopTest {
    @Test
    void testInitialSetup(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackBishopPositions = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK);
        List<ChessboardPosition> whiteBishopPositions = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE);
        int expectedNumberOfBishopsPerColor = 2;

        assertEquals(expectedNumberOfBishopsPerColor, blackBishopPositions.size());
        assertEquals(expectedNumberOfBishopsPerColor, whiteBishopPositions.size());
    }

    @Test
    void testInitialSetupValidMoves(){
        Chessboard defaultChessboard = new Chessboard();
        ChessGame chessGame = new ChessGame(defaultChessboard);

        List<ChessboardPosition> blackBishopPositions = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK);
        List<ChessboardPosition> whiteBishopPositions = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE);

        assertEquals(0, chessGame.getAllPossibleMoves(whiteBishopPositions.get(0)).size());
        assertEquals(0, chessGame.getAllPossibleMoves(whiteBishopPositions.get(1)).size());
        chessGame.switchTurns();
        assertEquals(0, chessGame.getAllPossibleMoves(blackBishopPositions.get(0)).size());
        assertEquals(0, chessGame.getAllPossibleMoves(blackBishopPositions.get(1)).size());
    }

    @Test
    void testBishopMovementInCorner(){
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
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(7, chessGame.getAllPossibleMoves(bishopPosition).size());
    }

    @Test
    void testBishopMovementInCornerWithOppositeColorBlocker(){
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
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(4, chessGame.getAllPossibleMoves(bishopPosition).size());
    }
    @Test
    void testBishopMovementInCornerWithBlocker(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bP -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "bB -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(3, chessGame.getAllPossibleMoves(bishopPosition).size());
    }

    @Test
    void testBishopMovementComplex(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bP",
                "-- -- wQ -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wR -- -- -- --",
                "-- -- -- -- -- -- wP --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);
        chessGame.switchTurns();

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.BLACK).get(0);

        assertEquals(7, chessGame.getAllPossibleMoves(bishopPosition).size());
    }

    @Test
    void testBishopMoveCannotCauseCheck(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- bB",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wB -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE).get(0);

        assertEquals(4, chessGame.getAllPossibleMoves(bishopPosition).size());
    }
    @Test
    void testBishopMoveCannotCauseCheckMustTake(){
        Chessboard loadedChessboard = new Chessboard();
        loadedChessboard.loadFromLines(List.of(
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- bB -- -- --",
                "-- -- -- wB -- -- -- --",
                "-- -- wK -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --"));
        ChessGame chessGame = new ChessGame(loadedChessboard);

        ChessboardPosition bishopPosition = chessGame.getLocationsOfPiece(PieceType.BISHOP, ChessColor.WHITE).get(0);

        assertEquals(1, chessGame.getAllPossibleMoves(bishopPosition).size());
    }

}
