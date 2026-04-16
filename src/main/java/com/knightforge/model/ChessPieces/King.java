package com.knightforge.model.ChessPieces;

import com.knightforge.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class King extends ChessPiece {
    private static final int DEFAULT_START_COLUMN = 4;
    private static final int QUEENSIDE_ROOK_COLUMN = 0;
    private static final int KINGSIDE_ROOK_COLUMN = 7;
    static String PIECE_NAME = "KING";
    private final int startRow;

    public King(ChessColor color) {
        super(color, PIECE_NAME);
        startRow = color == ChessColor.BLACK ? 0 : 7;
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return (dx <= 1 && dy <= 1);
    }

    @Override
    public List<MoveNew> getPossibleSpecialMoves(ChessboardPosition currentPosition, Chessboard board, List<MoveNew> moveHistory) {
        List<MoveNew> specialMoves = new ArrayList<>();

        if (thisPieceHasMoved(moveHistory) || !thisPieceAtStartingLocation(board)) { return specialMoves; }

        ChessboardPosition possibleTargetPosition = new ChessboardPosition(currentPosition.getX(), currentPosition.getY()-2);
        ChessboardPosition involvedRookPosition = new ChessboardPosition(currentPosition.getX(), QUEENSIDE_ROOK_COLUMN);
        if (rookHasNotMoved(moveHistory, currentPosition.getX(), QUEENSIDE_ROOK_COLUMN, board) &&
                pathIsClear(currentPosition, possibleTargetPosition, board) &&
                pathIsClear(currentPosition, involvedRookPosition, board)){
            specialMoves.add(new MoveNew(currentPosition,
                    possibleTargetPosition,
                    board.getPieceAtPosition(currentPosition),
                    null,
                    null,
                    new ChessboardPosition(currentPosition.getX(), QUEENSIDE_ROOK_COLUMN)));
        }

        possibleTargetPosition = new ChessboardPosition(currentPosition.getX(), currentPosition.getY()+2);
        involvedRookPosition = new ChessboardPosition(currentPosition.getX(), KINGSIDE_ROOK_COLUMN);
        if (rookHasNotMoved(moveHistory, currentPosition.getX(), KINGSIDE_ROOK_COLUMN, board) &&
                pathIsClear(currentPosition, possibleTargetPosition, board)&&
                pathIsClear(currentPosition, involvedRookPosition, board)){
            specialMoves.add(new MoveNew(currentPosition,
                    new ChessboardPosition(currentPosition.getX(), currentPosition.getY()+2),
                    board.getPieceAtPosition(currentPosition),
                    null,
                    null,
                    new ChessboardPosition(currentPosition.getX(), KINGSIDE_ROOK_COLUMN)));
        }
        return specialMoves;
    }

    private boolean thisPieceAtStartingLocation(Chessboard chessboard) {
        ChessboardPosition thisPiecePosition = chessboard.getLocationsOfPiece(PieceType.KING, color).get(0);
        if (thisPiecePosition.getX() != startRow || thisPiecePosition.getY() != DEFAULT_START_COLUMN) {
            return false;
        }
        return true;
    }

    private boolean rookHasNotMoved(List<MoveNew> moveHistory, int currentPieceRow, int desiredRookColumn, Chessboard chessboard) {
        if (!Objects.equals(chessboard.getPieceAtPosition(currentPieceRow, desiredRookColumn).getType(), PieceType.ROOK.name()) ||
                chessboard.getPieceAtPosition(currentPieceRow, desiredRookColumn).getColor() != this.color) {
            return false;
        }
        for (MoveNew move : moveHistory) {
            ChessPiece activePiece = move.getActivePiece();
            if (activePiece.getColor() == this.color &&
                    Objects.equals(activePiece.getType(), PieceType.ROOK.name()) &&
                    move.getFrom().getX() == currentPieceRow &&
                    move.getFrom().getY() == desiredRookColumn) {
                return false;
            }
        }
        return true;
    }

    private boolean thisPieceHasMoved(List<MoveNew> moveHistory) {
        for (MoveNew move : moveHistory) {
            ChessPiece activePiece = move.getActivePiece();
            if (activePiece.getColor() == this.color && Objects.equals(activePiece.getType(), this.type)){
                return true;
            }
        }
        return false;
    }
}
