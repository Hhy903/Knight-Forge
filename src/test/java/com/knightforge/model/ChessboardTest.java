package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChessboardTest {

    @Test
    void testChessboardInitialSetup() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();

        int numExpectedEmptySquares = 32;
        long numActualEmptySquares = Arrays.stream(board)
                                        .flatMap(Arrays::stream) // Flattens 2D array into a Stream of elements
                                        .filter(Objects::isNull) // Retains only null values
                                        .count();

        int numExpectedPiecesPerPlayer = 16;
        long numActualPiecesBlack = Arrays.stream(board)
                .flatMap(Arrays::stream) // Flattens 2D array into a Stream of elements
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == ChessColor.BLACK) // Retains only null values
                .count();
        long numActualPiecesWhite = Arrays.stream(board)
                .flatMap(Arrays::stream) // Flattens 2D array into a Stream of elements
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == ChessColor.WHITE) // Retains only null values
                .count();

        assertEquals(numExpectedEmptySquares, numActualEmptySquares);
        assertEquals(numExpectedPiecesPerPlayer, numActualPiecesBlack);
        assertEquals(numExpectedPiecesPerPlayer, numActualPiecesWhite);
    }

    @Test
    void testChessboardInitialSetupCreatesCorrectPieces() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();

        String[] chessPiecesWhite = Arrays.stream(board)
                .flatMap(Arrays::stream) // Flattens 2D array into a Stream of elements
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == ChessColor.WHITE) // Retains only null values
                .map(ChessPiece::getType)
                .toArray(String[]::new);
        String[] chessPiecesBlack = Arrays.stream(board)
                .flatMap(Arrays::stream) // Flattens 2D array into a Stream of elements
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == ChessColor.BLACK) // Retains only null values
                .map(ChessPiece::getType)
                .toArray(String[]::new);
        String[] chessPiecesExpected =
                {"PAWN", "PAWN",   "PAWN",   "PAWN",  "PAWN", "PAWN",   "PAWN",   "PAWN",
                 "ROOK", "KNIGHT", "BISHOP", "QUEEN", "KING", "BISHOP", "KNIGHT", "ROOK"};
        Arrays.sort(chessPiecesWhite);
        Arrays.sort(chessPiecesBlack);
        Arrays.sort(chessPiecesExpected);

        assertArrayEquals(chessPiecesWhite, chessPiecesExpected);
        assertArrayEquals(chessPiecesBlack, chessPiecesExpected);
    }

    @Test
    void testGetAllPositionsForGivenPiece(){
        Chessboard chessboard = new Chessboard();

        List<ChessboardPosition> whitePawnPositions = chessboard.getLocationsOfPiece(PieceType.PAWN, ChessColor.WHITE);
        int expectedNumberOfPawnsPerPlayer = 8;

        assertEquals(expectedNumberOfPawnsPerPlayer, whitePawnPositions.size());
    }
}
