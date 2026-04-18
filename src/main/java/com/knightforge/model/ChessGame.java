package com.knightforge.model;

import java.util.ArrayList;
import java.util.List;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.view.ChessGameObserver;

public class ChessGame implements ObservableChessGame {
    private Chessboard chessboard;
    private ChessColor whoseTurn;
    private IMoveHandler moveHandler;
    private List<ChessGameObserver> observers = new ArrayList<>();

    // Interaction state
    private ChessboardPosition selectedSquare = null;
    private List<MoveNew> currentLegalMoves = new ArrayList<>();
    private GameMode mode = GameMode.IDLE;
    private MoveNew pendingPromotionMove = null;
//
//    public ChessGame(Chessboard chessboard) {
//        this.chessboard = chessboard;
//        this.moveHandler = new MoveHandler(chessboard);
//        this.whoseTurn = ChessColor.WHITE;
//    }

    public ChessGame() {
        this.chessboard = new Chessboard();
        this.moveHandler = new MoveHandler(chessboard);
        this.whoseTurn = ChessColor.WHITE;
    }

    // --- Public method for interacting with the board ---

    public void selectPosition(ChessboardPosition position) {
        switch (mode) {
            case IDLE -> handleIdleClick(position);
            case STAGING -> handleStagingClick(position);
            case AWAITING_PROMOTION -> {} // ignore board clicks until promotion is resolved
        }
        notifyObservers();
    }

    public void handlePromotionSelection(PieceType type) {
        if (mode != GameMode.AWAITING_PROMOTION || pendingPromotionMove == null) return;
        moveHandler.executePromotionMove(pendingPromotionMove, type);
        pendingPromotionMove = null;
        switchTurns();
        clearSelection();
        notifyObservers();
    }

    public void undoLastMove() {
        moveHandler.undoLastMove();
        switchTurns();
        clearSelection();
        notifyObservers();
    }

    // --- Private methods to handle board interaction based on current gameMode ---

    private void handleIdleClick(ChessboardPosition position) {
        List<MoveNew> moves = moveHandler.getValidMoves(whoseTurn, position);
        if (!moves.isEmpty()) {
            selectedSquare = position;
            currentLegalMoves = moves;
            mode = GameMode.STAGING;
        }
    }

    private void handleStagingClick(ChessboardPosition position) {
        if (selectedSquare.equals(position)) {
            clearSelection();
            return;
        }

        MoveNew move = currentLegalMoves.stream()
                .filter(m -> m.getTo().equals(position))
                .findFirst()
                .orElse(null);

        if (move != null) {
            try {
                moveHandler.executeMove(move);
                switchTurns();
                clearSelection();
            } catch (PromotionRequiredException e) {
                pendingPromotionMove = move;
                mode = GameMode.AWAITING_PROMOTION;
            }
        } else {
            // Only reselect if the clicked position belongs to a friendly piece with legal moves
            List<MoveNew> moves = moveHandler.getValidMoves(whoseTurn, position);
            if (!moves.isEmpty()) {
                selectedSquare = position;
                currentLegalMoves = moves;
                mode = GameMode.STAGING;
            }
            // Otherwise — empty square or opponent piece outside legal moves — keep current selection
        }
    }

    private void clearSelection() {
        selectedSquare = null;
        currentLegalMoves = new ArrayList<>();
        mode = GameMode.IDLE;
    }


    // --- Game flow/helper methods ---

    private void switchTurns() {
        whoseTurn = oppositeColor(whoseTurn);
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    // --- Observer specific method ---

    public void setup() {
        notifyObservers();
    }

    @Override
    public void addObserver(ChessGameObserver observer) {
        observers.add(observer);
    }

    public GameState getState() {
        return new GameState(
                chessboard.getBoard(),
                selectedSquare,
                currentLegalMoves.stream().map(m -> new ChessboardPosition(m.getTo().getX(), m.getTo().getY())).toList(),
                whoseTurn,
                mode
        );
    }

    private void notifyObservers() {
        GameState state = getState();
        observers.forEach(o -> o.updateGameState(state));
    }
}