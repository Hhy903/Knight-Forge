package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.model.ChessPieces.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            return new ArrayList<>();
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

        return !opponentsAttackableSquares.contains(squareOnCastlingKingsPath);
    }

    // TODO:
    private boolean wouldLeaveKingInCheck(MoveNew move, ChessColor whoseTurn) {
        // Make the move, store if current player is in check.
        if (isPawnPromotion(move)) {
            executeInternal(move, PieceType.QUEEN);
        }
        else {
            executeInternal(move, null);
        }
        boolean inCheck = currentPlayerInCheck(whoseTurn);

        // Undo the move
        undoLastMove();

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
                    List<MoveNew> possiblyLegalMoves = getPotentiallyLegalMoves(position);
                    List<MoveNew> tmp = getPotentiallyLegalMoves(position).stream()
                            .filter(this::moveIsDiagonalIfPawnMove)
                            .toList();

                    List<ChessboardPosition> possibleResultingPositions = tmp.stream()
                            .map(move -> new ChessboardPosition(move.getTo().getX(), move.getTo().getY()))
                            .toList();
                    attackableSquares.addAll(possibleResultingPositions);
                }
            }
        }
        return attackableSquares;
    }

    private boolean moveIsDiagonalIfPawnMove(MoveNew move) {
        if (!Objects.equals(chessboard.getPieceAtPosition(move.getFrom()).getType(), PieceType.PAWN)){
            return true;
        }
        return move.getTo().getY() != move.getFrom().getY();
    }

    public void executeMove(MoveNew move) throws PromotionRequiredException{
        if (isPawnPromotion(move)) {
            throw new PromotionRequiredException();
        }
        executeInternal(move, null);
    }

    public void executePromotionMove(MoveNew move, PieceType desiredPiece){
        executeInternal(move, desiredPiece);
    }

    private void executeInternal(MoveNew move, PieceType promotionPiece){
        chessboard.movePiece(move.getFrom(), move.getTo());
        if (promotionPiece != null) {
            chessboard.createAndPlacePiece(promotionPiece, move.getActivePiece().getColor(), move.getTo());
        }
        else if (move.isEnPassant()) {
            chessboard.capture(move.getEnPassantCaptureLocation());
        }
        else if (move.isCastleMove()) {
            chessboard.movePiece(move.getRookPositionInvolvedInCastle(), new ChessboardPosition(move.getFrom().getX(), (move.getTo().getY() + move.getFrom().getY())/2));
        }
        moveHistory.add(move);
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
            // Ensure piece that was moved in the first place is moves, not current piece (needed for promotion undo)
            chessboard.placePiece(lastMove.getActivePiece(), lastMove.getFrom());
            chessboard.placePiece(lastMove.getInvolvedPiece(), lastMove.getTo());
        }
        // Remove the move from the moveHistory.
        moveHistory.remove(moveHistory.size() - 1);
        return true;
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    private boolean isPawnPromotion(MoveNew move) {
        return move.getActivePiece().getType().equals(PieceType.PAWN) && (move.getTo().getX() == 0 || move.getTo().getX() == 7);
    }
}