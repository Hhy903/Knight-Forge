package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class MoveHandler implements IMoveHandler{
    Chessboard chessboard;
    private final List<MoveNew> moveHistory = new ArrayList<>();

    public MoveHandler(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    @Override
    public List<ChessboardPosition> getValidMoves(ChessColor whoseTurn, ChessboardPosition position) {
        List<ChessboardPosition> possiblyLegalMoves = getPotentiallyLegalMoves(position);
        return getLegalMoves(position, possiblyLegalMoves, whoseTurn);
    }

    private List<ChessboardPosition> getPotentiallyLegalMoves(ChessboardPosition position) {
        ChessPiece pieceAtPosition = chessboard.getPieceAtPosition(position);
        if (pieceAtPosition == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(pieceAtPosition.getPossibleMoves(position, chessboard.getBoard(), moveHistory));
    }

    private List<ChessboardPosition> getLegalMoves(ChessboardPosition currentPosition, List<ChessboardPosition> possiblyLegalMoves, ChessColor whoseTurn){
        if (chessboard.getPieceAtPosition(currentPosition).getColor() != whoseTurn) {
            throw new IllegalStateException("Attempting to find legal moves for a piece whose turn it is not.");
        }
        return possiblyLegalMoves.stream()
                .filter(possibleMove -> !wouldLeaveKingInCheck(currentPosition, possibleMove, whoseTurn))
                .toList();
    }

    private boolean wouldLeaveKingInCheck(ChessboardPosition from, ChessboardPosition to, ChessColor whoseTurn) {
        // Make the move, store if current player is in check.
        ChessPiece activePiece = chessboard.getPieceAtPosition(from);
        ChessPiece capturedPiece = chessboard.movePiece(from, to);
        boolean inCheck = currentPlayerInCheck(whoseTurn);

        // Undo the move
        chessboard.reverseMove(to, from, activePiece, capturedPiece);

        return inCheck;
    }

    private boolean currentPlayerInCheck(ChessColor whoseTurn) {
        // Find king location
        // Aggregate list of all positions the opponent color is currently attacking.
        // if king location in that list, return true, if not false.
        ChessboardPosition kingPosition = chessboard.getKingLocation(whoseTurn);
        List<ChessboardPosition> opponentsAttackableSquares = getAttackableSquares(oppositeColor(whoseTurn), moveHistory);
        return opponentsAttackableSquares.contains(kingPosition);
    }


    protected List<ChessboardPosition> getAttackableSquares(ChessColor color, List<MoveNew> moveHistory) {
        List<ChessboardPosition> attackableSquares = new ArrayList<>();
        for (PieceType piece : PieceType.values()){
            List<ChessboardPosition> locationsForPiece = chessboard.getLocationsOfPiece(piece, color);
            if (!locationsForPiece.isEmpty()) {
                for (ChessboardPosition position : locationsForPiece) {
                    attackableSquares.addAll(getPotentiallyLegalMoves(position));
                }
            }
        }
        return attackableSquares;
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }
}
