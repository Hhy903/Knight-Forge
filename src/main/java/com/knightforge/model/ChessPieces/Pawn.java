package com.knightforge.model.ChessPieces;

import com.knightforge.model.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPiece {
    static String PIECE_NAME = "PAWN";
    int direction;
    int startRow;
    public Pawn(ChessColor color) {
        super(color, PIECE_NAME);
        direction = color == ChessColor.BLACK ? 1 : -1;
        startRow = color == ChessColor.BLACK ? 1 : 6;
    }

    // Handles forward movement (dx)
    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        int dx = to.getX() - from.getX();
        if ((dx == direction || (from.getX() == startRow && dx == 2 * direction)) && from.getY() == to.getY()){
            return true;
        }
        return false;
    }

    @Override
    protected boolean finalPositionIsValid(ChessboardPosition to, Chessboard chessboard) {
        ChessPiece targetPiece = chessboard.getPieceAtPosition(to);
        return targetPiece == null;
    }

    // Adds special-case diagonal movement (attacking and En Passant)
    @Override
    public List<MoveNew> getPossibleSpecialMoves(ChessboardPosition currentPosition, Chessboard board, List<MoveNew> moveHistory) {
        List<MoveNew> possibleMoves = new ArrayList<>();
        // Standard Attacking
        //Digonal 1
        if (currentPosition.getY() + 1 < board.getLength() &&
                board.getPieceAtPosition(currentPosition.getX()+direction, currentPosition.getY()+1) != null &&
                board.getPieceAtPosition(currentPosition.getX()+direction, currentPosition.getY()+1).getColor() != color) {
            ChessboardPosition targetPosition = new ChessboardPosition(currentPosition.getX()+direction, currentPosition.getY()+1);
            possibleMoves.add(new MoveNew(currentPosition, targetPosition, board.getPieceAtPosition(currentPosition), board.getPieceAtPosition(targetPosition)));
        }
        //Diagonal 2
        if (currentPosition.getY() - 1 >= 0  &&
                board.getPieceAtPosition(currentPosition.getX()+direction, currentPosition.getY()-1) != null &&
                board.getPieceAtPosition(currentPosition.getX()+direction, currentPosition.getY()-1).getColor() != color) {
            ChessboardPosition targetPosition = new ChessboardPosition(currentPosition.getX()+direction, currentPosition.getY()-1);
            possibleMoves.add(new MoveNew(currentPosition, targetPosition, board.getPieceAtPosition(currentPosition), board.getPieceAtPosition(targetPosition)));
        }
        if (moveHistory.isEmpty()) { return possibleMoves; }
        MoveNew opponentsLastMove = moveHistory.get(moveHistory.size()-1);
        // En Passant
        if (opponentsLastMove.wasTwoSquarePawnMove() &&
                currentPosition.getX() == opponentsLastMove.getTo().getX() &&
                Math.abs(currentPosition.getY() - opponentsLastMove.getTo().getY()) == 1){
            ChessboardPosition targetPosition = new ChessboardPosition(currentPosition.getX()+direction, opponentsLastMove.getTo().getY());
            possibleMoves.add(new MoveNew(currentPosition,
                    targetPosition,
                    board.getPieceAtPosition(currentPosition),
                    board.getPieceAtPosition(opponentsLastMove.getTo().getX(), opponentsLastMove.getTo().getY()),
                    opponentsLastMove.getTo(),
                    null));
        }
        return possibleMoves;
    }
}
