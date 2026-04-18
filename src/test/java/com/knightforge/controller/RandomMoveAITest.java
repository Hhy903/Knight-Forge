package com.knightforge.controller;

import com.knightforge.model.ChessColor;
import com.knightforge.model.Chessboard;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.Move;
import com.knightforge.model.MoveHandler;
import com.knightforge.model.PieceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RandomMoveAITest {
    @Test
    void testChooseMoveReturnsOneOfLegalMoves() {
        Chessboard chessboard = new Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wK wQ -- -- -- -- -- --"));
        MoveHandler moveHandler = new MoveHandler(chessboard);
        List<Move> legalMoves = moveHandler.getValidMoves(ChessColor.WHITE, new ChessboardPosition(7, 1));

        RandomMoveAI randomMoveAI = new RandomMoveAI(new Random(0));
        Move chosenMove = randomMoveAI.chooseMove(legalMoves);

        assertNotNull(chosenMove);
        assertEquals(PieceType.QUEEN, chosenMove.getActivePiece().getType());
    }
}
