package com.knightforge.controller;

import com.knightforge.model.BoardState;
import com.knightforge.model.ChessColor;
import com.knightforge.model.Move;
import com.knightforge.model.PieceType;
import com.knightforge.view.ChessboardPoint;

import java.util.List;
import java.util.Set;

import java.util.HashSet;

/**
 * Coordinates board state and game phase transitions.
 */
public class GameSession {
    private final BoardState boardState;
    private GamePhase phase = GamePhase.SELECTING_PIECE;
    private ChessboardPoint selectedPoint;
    private ChessboardPoint promotionPoint;
    private Move lastMove;
    private String gameResult;
    private String statusMessage;

    public GameSession(BoardState boardState) {
        this.boardState = boardState;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public ChessboardPoint getSelectedPoint() {
        return selectedPoint;
    }

    public ChessboardPoint getPromotionPoint() {
        return promotionPoint;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public String getGameResult() {
        return gameResult;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public ChessColor getCurrentColor() {
        return boardState.getCurrentColor();
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public Set<ChessboardPoint> getHighlightedTargets() {
        if (phase != GamePhase.SELECTING_TARGET || selectedPoint == null) {
            return Set.of();
        }
        return new HashSet<>(boardState.getLegalMovesFrom(selectedPoint));
    }

    public boolean handleSquareClick(ChessboardPoint point) {
        return switch (phase) {
            case SELECTING_PIECE -> handleSelectingPiece(point);
            case SELECTING_TARGET -> handleSelectingTarget(point);
            case PROMOTION_PENDING, GAME_OVER, LOADING_GAME -> false;
        };
    }

    public boolean choosePromotion(PieceType pieceType) {
        if (phase != GamePhase.PROMOTION_PENDING || promotionPoint == null) {
            return false;
        }

        if (pieceType == PieceType.KING || pieceType == PieceType.PAWN) {
            return false;
        }

        boardState.promotePawn(promotionPoint, pieceType);
        promotionPoint = null;
        phase = GamePhase.SELECTING_PIECE;
        statusMessage = String.format("%s promoted to %s.", oppositeColor(boardState.getCurrentColor()).getName(), pieceType.name());
        return true;
    }

    public void enterLoadingPhase() {
        clearTransientState();
        phase = GamePhase.LOADING_GAME;
    }

    public void finishLoading() {
        clearTransientState();
        gameResult = null;
        phase = GamePhase.SELECTING_PIECE;
        statusMessage = "Game loaded.";
    }

    public void finishGame(String result) {
        clearTransientState();
        gameResult = result;
        phase = GamePhase.GAME_OVER;
        statusMessage = result;
    }

    public void reset() {
        boardState.reset();
        clearTransientState();
        gameResult = null;
        phase = GamePhase.SELECTING_PIECE;
        statusMessage = "New game started.";
    }

    public void loadGame(List<String> chessData) {
        enterLoadingPhase();
        boardState.reset();
        finishLoading();
    }

    private boolean handleSelectingPiece(ChessboardPoint point) {
        if (!boardState.isCurrentPlayerPiece(point)) {
            statusMessage = "Select one of the current player's pieces.";
            return false;
        }
        selectedPoint = point;
        phase = GamePhase.SELECTING_TARGET;
        statusMessage = String.format("%s to move. Choose a target square.", boardState.getCurrentColor().getName());
        return true;
    }

    private boolean handleSelectingTarget(ChessboardPoint point) {
        if (selectedPoint == null) {
            phase = GamePhase.SELECTING_PIECE;
            statusMessage = "Selection was cleared.";
            return false;
        }

        if (selectedPoint.equals(point)) {
            clearSelection();
            statusMessage = "Selection canceled.";
            return true;
        }

        if (boardState.isCurrentPlayerPiece(point)) {
            selectedPoint = point;
            statusMessage = "Piece changed. Choose a target square.";
            return true;
        }

        lastMove = boardState.applyMove(selectedPoint, point);
        if (lastMove == null) {
            statusMessage = "Illegal move. Your king must remain safe.";
            return false;
        }

        ChessColor sideToMove = boardState.getCurrentColor();
        selectedPoint = null;
        if (boardState.isPromotionRequired(lastMove)) {
            promotionPoint = point;
            phase = GamePhase.PROMOTION_PENDING;
            statusMessage = "Promotion pending. Choose a piece type.";
        } else {
            promotionPoint = null;
            phase = GamePhase.SELECTING_PIECE;
            updateGameStatusAfterMove(sideToMove);
        }
        return true;
    }

    private void clearSelection() {
        selectedPoint = null;
        phase = GamePhase.SELECTING_PIECE;
    }

    private void clearTransientState() {
        selectedPoint = null;
        promotionPoint = null;
        lastMove = null;
    }

    private void updateGameStatusAfterMove(ChessColor sideToMove) {
        boolean inCheck = boardState.isInCheck(sideToMove);
        boolean hasLegalMove = boardState.hasAnyLegalMove(sideToMove);
        if (!hasLegalMove) {
            if (inCheck) {
                finishGame(oppositeColor(sideToMove).getName() + " wins by checkmate.");
            } else {
                finishGame("Draw by stalemate.");
            }
            return;
        }

        if (inCheck) {
            statusMessage = sideToMove.getName() + " is in check.";
        } else {
            statusMessage = sideToMove.getName() + " to move.";
        }
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }
}
