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
    protected boolean finalPositionIsValid(ChessboardPosition to, ChessPiece[][] chessboard) {
        ChessPiece targetPiece = chessboard[to.getX()][to.getY()];
        return targetPiece == null;
    }

    // Adds special-case diagonal movement (attacking and En Passant)
    @Override
    public List<ChessboardPosition> getPossibleSpecialMoves(ChessboardPosition position, ChessPiece[][] board, List<MoveNew> moveHistory) {
        List<ChessboardPosition> positions = new ArrayList<>();
        // Standard Attacking
        //Digonal 1
        if (position.getY() + 1 < board[0].length &&
                board[position.getX()+direction][position.getY()+1] != null &&
                board[position.getX()+direction][position.getY()+1].getColor() != color) {
            positions.add(new ChessboardPosition(position.getX(), position.getY()+1));
        }
        //Diagonal 2
        if (position.getY() - 1 >= 0  &&
                board[position.getX()+direction][position.getY()-1] != null &&
                board[position.getX()+direction][position.getY()-1].getColor() != color) {
            positions.add(new ChessboardPosition(position.getX(), position.getY()-1));
        }
        if (moveHistory.isEmpty()) { return positions; }
        MoveNew opponentsLastMove = moveHistory.get(moveHistory.size()-1);
        // En Passant
        if (opponentsLastMove.wasTwoSquarePawnMove() &&
                position.getX() == opponentsLastMove.getTo().getX() &&
                Math.abs(position.getY() - opponentsLastMove.getTo().getY()) == 1){
            positions.add(new ChessboardPosition(position.getX()+direction, opponentsLastMove.getTo().getY()));
        }
        return positions;
    }
}
