package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoveHandler implements IMoveHandler{
    Chessboard chessboard;
    private final List<Move> moveHistory = new ArrayList<>();
    private final List<Move> stateHistory = new ArrayList<>();

    public MoveHandler(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    @Override
    public List<Move> getValidMoves(ChessColor whoseTurn, ChessboardPosition position) {
        List<Move> possiblyLegalMoves = getPotentiallyLegalMoves(position);
        return getLegalMoves(position, possiblyLegalMoves, whoseTurn);
    }

    // Retrieves piece at location, get possible unvalidated moves
    private List<Move> getPotentiallyLegalMoves(ChessboardPosition position) {
        ChessPiece pieceAtPosition = chessboard.getPieceAtPosition(position);
        if (pieceAtPosition == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(pieceAtPosition.getPossibleMoves(position, chessboard, getEffectiveMoveHistory()));
    }

    // Filter out invalid moves (invalid castling moves/would leave king in check moves)
    private List<Move> getLegalMoves(ChessboardPosition currentPosition, List<Move> possiblyLegalMoves, ChessColor whoseTurn){
        if (chessboard.getPieceAtPosition(currentPosition) == null || chessboard.getPieceAtPosition(currentPosition).getColor() != whoseTurn) {
            return new ArrayList<>();
        }

        possiblyLegalMoves = filterOutInvalidCastlingMoves(possiblyLegalMoves, whoseTurn);

        return possiblyLegalMoves.stream()
                .filter(possibleMove -> !wouldLeaveKingInCheck(possibleMove, whoseTurn))
                .toList();
    }

    private List<Move> filterOutInvalidCastlingMoves(List<Move> possiblyLegalMoves, ChessColor whoseTurn){
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

    private boolean isNotCastleThroughAttackedSquareMove(Move move, List<ChessboardPosition> opponentsAttackableSquares) {
        if (!move.isCastleMove()){
            return true;
        }
        ChessboardPosition squareOnCastlingKingsPath = new ChessboardPosition(move.getFrom().getX(), (move.getFrom().getY() + move.getTo().getY()) / 2 );

        return !opponentsAttackableSquares.contains(squareOnCastlingKingsPath);
    }

    // TODO:
    private boolean wouldLeaveKingInCheck(Move move, ChessColor whoseTurn) {
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


    protected List<ChessboardPosition> getAttackableSquares(ChessColor color) {
        List<ChessboardPosition> attackableSquares = new ArrayList<>();
        for (PieceType piece : PieceType.values()){
            List<ChessboardPosition> locationsForPiece = chessboard.getLocationsOfPiece(piece, color);
            if (!locationsForPiece.isEmpty()) {
                for (ChessboardPosition position : locationsForPiece) {
                    List<ChessboardPosition> possibleResultingPositions = getPotentiallyLegalMoves(position).stream()
                            .filter(this::moveIsDiagonalIfPawnMove)
                            .map(move -> new ChessboardPosition(move.getTo().getX(), move.getTo().getY()))
                            .toList();
                    attackableSquares.addAll(possibleResultingPositions);
                }
            }
        }
        return attackableSquares;
    }

    private boolean moveIsDiagonalIfPawnMove(Move move) {
        if (!Objects.equals(chessboard.getPieceAtPosition(move.getFrom()).getType(), PieceType.PAWN)){
            return true;
        }
        return move.getTo().getY() != move.getFrom().getY();
    }

    public void executeMove(Move move) throws PromotionRequiredException{
        if (isPawnPromotion(move)) {
            throw new PromotionRequiredException();
        }
        executeInternal(move, null);
    }

    public void executePromotionMove(Move move, PieceType desiredPiece){
        executeInternal(move, desiredPiece);
    }

    public boolean isInCheck(ChessColor whoseTurn) {
        return currentPlayerInCheck(whoseTurn);
    }

    public boolean hasAnyValidMove(ChessColor whoseTurn) {
        for (int row = 0; row < Chessboard.BOARD_SIZE; row++) {
            for (int col = 0; col < Chessboard.BOARD_SIZE; col++) {
                ChessboardPosition position = new ChessboardPosition(row, col);
                if (!getValidMoves(whoseTurn, position).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void executeInternal(Move move, PieceType promotionPiece){
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
        Move lastMove = moveHistory.get(moveHistory.size()-1);
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

    private boolean isPawnPromotion(Move move) {
        return move.getActivePiece().getType().equals(PieceType.PAWN) && (move.getTo().getX() == 0 || move.getTo().getX() == 7);
    }

    public void loadMetadata(ChessColor whoseTurn, String castleRights, ChessboardPosition enPassantTarget) {
        stateHistory.clear();

        if (!castleRights.contains("K")) {
            stateHistory.add(createStateMove(PieceType.ROOK, ChessColor.WHITE, new ChessboardPosition(7, 7)));
        }
        if (!castleRights.contains("Q")) {
            stateHistory.add(createStateMove(PieceType.ROOK, ChessColor.WHITE, new ChessboardPosition(7, 0)));
        }
        if (!castleRights.contains("k")) {
            stateHistory.add(createStateMove(PieceType.ROOK, ChessColor.BLACK, new ChessboardPosition(0, 7)));
        }
        if (!castleRights.contains("q")) {
            stateHistory.add(createStateMove(PieceType.ROOK, ChessColor.BLACK, new ChessboardPosition(0, 0)));
        }
        if (!castleRights.contains("K") && !castleRights.contains("Q")) {
            stateHistory.add(createStateMove(PieceType.KING, ChessColor.WHITE, new ChessboardPosition(7, 4)));
        }
        if (!castleRights.contains("k") && !castleRights.contains("q")) {
            stateHistory.add(createStateMove(PieceType.KING, ChessColor.BLACK, new ChessboardPosition(0, 4)));
        }

        if (enPassantTarget != null) {
            stateHistory.add(createEnPassantStateMove(whoseTurn, enPassantTarget));
        }
    }

    public String getCastleRightsToken() {
        StringBuilder rights = new StringBuilder();
        if (hasCastlingRight(ChessColor.WHITE, true)) {
            rights.append('K');
        }
        if (hasCastlingRight(ChessColor.WHITE, false)) {
            rights.append('Q');
        }
        if (hasCastlingRight(ChessColor.BLACK, true)) {
            rights.append('k');
        }
        if (hasCastlingRight(ChessColor.BLACK, false)) {
            rights.append('q');
        }
        return rights.isEmpty() ? "-" : rights.toString();
    }

    public ChessboardPosition getEnPassantTarget() {
        List<Move> effectiveHistory = getEffectiveMoveHistory();
        if (effectiveHistory.isEmpty()) {
            return null;
        }

        Move lastMove = effectiveHistory.get(effectiveHistory.size() - 1);
        if (!lastMove.wasTwoSquarePawnMove()) {
            return null;
        }

        int targetRow = (lastMove.getFrom().getX() + lastMove.getTo().getX()) / 2;
        return new ChessboardPosition(targetRow, lastMove.getTo().getY());
    }

    private List<Move> getEffectiveMoveHistory() {
        List<Move> effectiveHistory = new ArrayList<>(stateHistory);
        effectiveHistory.addAll(moveHistory);
        return effectiveHistory;
    }

    private boolean hasCastlingRight(ChessColor color, boolean kingside) {
        int row = color == ChessColor.WHITE ? 7 : 0;
        int rookColumn = kingside ? 7 : 0;
        ChessPiece king = chessboard.getPieceAtPosition(row, 4);
        ChessPiece rook = chessboard.getPieceAtPosition(row, rookColumn);
        if (king == null || rook == null) {
            return false;
        }
        if (king.getType() != PieceType.KING || king.getColor() != color) {
            return false;
        }
        if (rook.getType() != PieceType.ROOK || rook.getColor() != color) {
            return false;
        }

        for (Move move : getEffectiveMoveHistory()) {
            ChessPiece movedPiece = move.getActivePiece();
            if (movedPiece == null || movedPiece.getColor() != color) {
                continue;
            }
            if (movedPiece.getType() == PieceType.KING && move.getFrom().equals(new ChessboardPosition(row, 4))) {
                return false;
            }
            if (movedPiece.getType() == PieceType.ROOK && move.getFrom().equals(new ChessboardPosition(row, rookColumn))) {
                return false;
            }
        }
        return true;
    }

    private Move createStateMove(PieceType pieceType, ChessColor color, ChessboardPosition origin) {
        ChessPiece piece = Chessboard.chessPieceFactory.createPiece(pieceType, color);
        return new Move(origin, origin, piece, null);
    }

    private Move createEnPassantStateMove(ChessColor whoseTurn, ChessboardPosition enPassantTarget) {
        ChessColor pawnColor = oppositeColor(whoseTurn);
        ChessPiece pawn = Chessboard.chessPieceFactory.createPiece(PieceType.PAWN, pawnColor);
        int destinationRow = pawnColor == ChessColor.WHITE ? enPassantTarget.getX() - 1 : enPassantTarget.getX() + 1;
        int originRow = pawnColor == ChessColor.WHITE ? destinationRow + 2 : destinationRow - 2;
        ChessboardPosition from = new ChessboardPosition(originRow, enPassantTarget.getY());
        ChessboardPosition to = new ChessboardPosition(destinationRow, enPassantTarget.getY());
        return new Move(from, to, pawn, null);
    }
}
