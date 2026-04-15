package com.knightforge.controller;

import com.knightforge.model.BoardState;
import com.knightforge.model.ChessColor;
import com.knightforge.model.Move;
import com.knightforge.model.PieceType;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.HashSet;

/**
 * Coordinates board state and game phase transitions.
 */
public class GameSession {
    private final BoardState boardState;
    private GamePhase phase = GamePhase.SELECTING_PIECE;
    private ChessboardPosition selectedPoint;
    private ChessboardPosition promotionPoint;
    private Move lastMove;
    private String gameResult;
    private String statusMessage;
    private final List<GameSessionListener> listeners = new ArrayList<>();

    public GameSession(BoardState boardState) {
        this.boardState = boardState;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public ChessboardPosition getSelectedPoint() {
        return selectedPoint;
    }

    public ChessboardPosition getPromotionPoint() {
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

    public Set<ChessboardPosition> getHighlightedTargets() {
        if (phase != GamePhase.SELECTING_TARGET || selectedPoint == null) {
            return Set.of();
        }
        return new HashSet<>(boardState.getLegalMovesFrom(selectedPoint));
    }

    public void addListener(GameSessionListener listener) {
        listeners.add(listener);
    }

    public boolean handleSquareClick(ChessboardPosition point) {
        boolean changed = switch (phase) {
            case SELECTING_PIECE -> handleSelectingPiece(point);
            case SELECTING_TARGET -> handleSelectingTarget(point);
            case PROMOTION_PENDING, GAME_OVER, LOADING_GAME -> false;
        };
        if (changed) {
            notifyListeners();
        }
        return changed;
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
        ChessColor sideToMove = boardState.getCurrentColor();
        updateGameStatusAfterMove(sideToMove);
        if (phase != GamePhase.GAME_OVER) {
            statusMessage = String.format("%s promoted to %s. %s",
                    oppositeColor(boardState.getCurrentColor()).getName(),
                    pieceType.name(),
                    statusMessage == null ? "" : statusMessage);
        }
        notifyListeners();
        return true;
    }

    public boolean undo() {
        Move undoneMove = boardState.undoLastMove();
        if (undoneMove == null) {
            statusMessage = "No move to undo.";
            notifyListeners();
            return false;
        }

        clearTransientState();
        gameResult = null;
        phase = GamePhase.SELECTING_PIECE;
        statusMessage = boardState.getCurrentColor().getName() + " to move. Last move undone.";
        notifyListeners();
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
        notifyListeners();
    }

    public void loadGame(List<String> chessData) {
        enterLoadingPhase();
        boardState.loadFromLines(chessData);
        finishLoading();
        notifyListeners();
    }

    public List<String> saveGame() {
        return boardState.serialize();
    }

    private boolean handleSelectingPiece(ChessboardPosition point) {
        if (!boardState.isCurrentPlayerPiece(point)) {
            statusMessage = "Select one of the current player's pieces.";
            return false;
        }
        selectedPoint = point;
        phase = GamePhase.SELECTING_TARGET;
        statusMessage = String.format("%s to move. Choose a target square.", boardState.getCurrentColor().getName());
        return true;
    }

    private boolean handleSelectingTarget(ChessboardPosition point) {
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

        if (boardState.isInsufficientMaterial()) {
            finishGame("Draw by insufficient material.");
            return;
        }

        if (boardState.isThreefoldRepetition()) {
            finishGame("Draw by threefold repetition.");
            return;
        }

        if (boardState.getHalfmoveClock() >= 100) {
            finishGame("Draw by 50-move rule.");
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

    private void notifyListeners() {
        GameSessionEvent event = new GameSessionEvent(phase, statusMessage, gameResult);
        for (GameSessionListener listener : listeners) {
            listener.onSessionChanged(event);
        }
    }
}
