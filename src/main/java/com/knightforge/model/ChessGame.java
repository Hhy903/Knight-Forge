package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    Chessboard chessboard;
    ChessColor whoseTurn;
    private final List<MoveNew> moveHistory = new ArrayList<>();

    public ChessGame(Chessboard chessboard) {
        this.chessboard = chessboard;
        this.whoseTurn = ChessColor.WHITE;
    }
    public ChessGame() {
        this.chessboard = new Chessboard();
        this.whoseTurn = ChessColor.WHITE;
    }

    public void switchTurns() { whoseTurn = oppositeColor(whoseTurn); }

    public List<ChessboardPosition> getAllPossibleMoves(ChessboardPosition currentPosition) {
        List<ChessboardPosition> possiblyLegalMoves = chessboard.getPotentiallyLegalMoves(currentPosition, moveHistory);
        return getLegalMoves(currentPosition, possiblyLegalMoves);
    }

    private List<ChessboardPosition> getLegalMoves(ChessboardPosition currentPosition, List<ChessboardPosition> possiblyLegalMoves){
        if (chessboard.getPieceAtPosition(currentPosition).getColor() != whoseTurn) {
            throw new IllegalStateException("Attempting to find legal moves for a piece whose turn it is not.");
        }
        return possiblyLegalMoves.stream()
                .filter(possibleMove -> !wouldLeaveKingInCheck(currentPosition, possibleMove))
                .toList();
    }

    private boolean wouldLeaveKingInCheck(ChessboardPosition from, ChessboardPosition to) {
        // Make the move, store if current player is in check.
        ChessPiece activePiece = chessboard.getPieceAtPosition(from);
        ChessPiece capturedPiece = chessboard.movePiece(from, to);
        boolean inCheck = currentPlayerInCheck(this.whoseTurn);

        // Undo the move
        chessboard.reverseMove(to, from, activePiece, capturedPiece);

        return inCheck;
    }

    public boolean currentPlayerInCheck(ChessColor attackerColor) {
        // Find king location
        // Aggregate list of all positions the opponent color is currently attacking.
        // if king location in that list, return true, if not false.
        ChessboardPosition kingPosition = chessboard.getKingLocation(this.whoseTurn);
        List<ChessboardPosition> opponentsAttackableSquares = chessboard.getAttackableSquares(oppositeColor(attackerColor), moveHistory);
        return opponentsAttackableSquares.contains(kingPosition);
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    public boolean makeMove(ChessboardPosition from, ChessboardPosition to) {
        if (chessboard.getPieceAtPosition(from).getColor() != whoseTurn) {
            return false;
        }
        ChessPiece capturedPiece = chessboard.movePiece(from, to);
        // TODO: Update Move object
        //moveHistory.add(new Move(from, to, capturedPiece));

        return true;
    }
}
