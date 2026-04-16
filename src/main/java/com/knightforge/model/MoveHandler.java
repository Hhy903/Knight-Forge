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
    public List<MoveNew> getValidMoves(ChessColor whoseTurn, ChessboardPosition position) {
        List<MoveNew> possiblyLegalMoves = getPotentiallyLegalMoves(position);
        return getLegalMoves(position, possiblyLegalMoves, whoseTurn);
    }

    private List<MoveNew> getPotentiallyLegalMoves(ChessboardPosition position) {
        ChessPiece pieceAtPosition = chessboard.getPieceAtPosition(position);
        if (pieceAtPosition == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(pieceAtPosition.getPossibleMoves(position, chessboard, moveHistory));
    }

    private List<MoveNew> getLegalMoves(ChessboardPosition currentPosition, List<MoveNew> possiblyLegalMoves, ChessColor whoseTurn){
        if (chessboard.getPieceAtPosition(currentPosition).getColor() != whoseTurn) {
            throw new IllegalStateException("Attempting to find legal moves for a piece whose turn it is not.");
        }

        possiblyLegalMoves = filterOutInvalidCastlingMoves(possiblyLegalMoves, whoseTurn);

        return possiblyLegalMoves.stream()
                .filter(possibleMove -> !wouldLeaveKingInCheck(possibleMove, whoseTurn))
                .toList();
    }

    private List<MoveNew> filterOutInvalidCastlingMoves(List<MoveNew> possiblyLegalMoves, ChessColor whoseTurn){
        // Cannot castle while in check
        if (currentPlayerInCheck(whoseTurn)) {
            possiblyLegalMoves = possiblyLegalMoves.stream()
                    .filter(possibleMove -> !possibleMove.isCastleMove())
                    .toList();
        }

        // Cannot castle through attack.
        List<ChessboardPosition> opponentsAttackableSquares = getAttackableSquares(oppositeColor(whoseTurn));
        possiblyLegalMoves = possiblyLegalMoves.stream()
                .filter(move -> isNotCastleThroughAttackedSquareMove(move, opponentsAttackableSquares))
                .toList();

        return possiblyLegalMoves;
    }

    private boolean isNotCastleThroughAttackedSquareMove(MoveNew move, List<ChessboardPosition> opponentsAttackableSquares) {
        if (!move.isCastleMove()){
            return true;
        }
        ChessboardPosition squareOnCastlingKingsPath = new ChessboardPosition(move.getFrom().getX(), (move.getFrom().getY() + move.getTo().getY()) / 2 );
        if (opponentsAttackableSquares.contains(squareOnCastlingKingsPath)){
            return false;
        }
        return true;
    }

    // TODO:
    private boolean wouldLeaveKingInCheck(MoveNew move, ChessColor whoseTurn) {
        // Make the move, store if current player is in check.
        executeMove(move);
//        ChessPiece activePiece = chessboard.getPieceAtPosition(move.getFrom());
//        ChessPiece capturedPiece = chessboard.movePiece(move.getFrom(), move.getTo());
        boolean inCheck = currentPlayerInCheck(whoseTurn);

        // Undo the move
        undoLastMove();
//        chessboard.reverseMove(move.getTo(), move.getFrom(), activePiece, capturedPiece);

        return inCheck;
    }

    private boolean currentPlayerInCheck(ChessColor whoseTurn) {
        ChessboardPosition kingPosition = chessboard.getKingLocation(whoseTurn);
        List<ChessboardPosition> opponentsAttackableSquares = getAttackableSquares(oppositeColor(whoseTurn));
        return opponentsAttackableSquares.contains(kingPosition);
    }

    // TODO: Currently using the getPotentiallyLegalMoves to determine opponents attackable squares fails for pawn movement
    protected List<ChessboardPosition> getAttackableSquares(ChessColor color) {
        List<ChessboardPosition> attackableSquares = new ArrayList<>();
        for (PieceType piece : PieceType.values()){
            List<ChessboardPosition> locationsForPiece = chessboard.getLocationsOfPiece(piece, color);
            if (!locationsForPiece.isEmpty()) {
                for (ChessboardPosition position : locationsForPiece) {
                    List<ChessboardPosition> possibleResultingPositions = getPotentiallyLegalMoves(position).stream()
                            .map(move -> new ChessboardPosition(move.getTo().getX(), move.getTo().getY()))
                            .toList();;
                    attackableSquares.addAll(possibleResultingPositions);
                }
            }
        }
        return attackableSquares;
    }

    public boolean executeMove(MoveNew move) {
        chessboard.movePiece(move.getFrom(), move.getTo());
        moveHistory.add(move);
        if (move.isEnPassant()) {
            chessboard.capture(move.getEnPassantCaptureLocation());
        }
        if (move.isCastleMove()) {
            chessboard.movePiece(move.getRookPositionInvolvedInCastle(), new ChessboardPosition(move.getFrom().getX(), (move.getTo().getY() + move.getFrom().getY())/2));
        }
        return false;
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }
        MoveNew lastMove = moveHistory.get(moveHistory.size()-1);
        // En Passant
        if (lastMove.isEnPassant()){
            chessboard.movePiece(lastMove.getTo(), lastMove.getFrom());
            chessboard.placePiece(lastMove.getInvolvedPiece(), lastMove.getEnPassantCaptureLocation());
        }
        // Castle Move
        else if (lastMove.isCastleMove()) {
            chessboard.movePiece(lastMove.getTo(), lastMove.getFrom());
            chessboard.movePiece(new ChessboardPosition(lastMove.getFrom().getX(),(lastMove.getTo().getY() + lastMove.getFrom().getY())/2), lastMove.getRookPositionInvolvedInCastle());
        }
        // Standard Move
        else {
            chessboard.movePiece(lastMove.getTo(), lastMove.getFrom());
            chessboard.placePiece(lastMove.getInvolvedPiece(), lastMove.getTo());
        }
        // Remove the move from the moveHistory.
        moveHistory.remove(moveHistory.size() - 1);
        return true;
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }
}