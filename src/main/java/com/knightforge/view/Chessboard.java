package com.knightforge.view;


import com.knightforge.controller.ClickController;
import com.knightforge.controller.GameSession;
import com.knightforge.model.BoardState;
import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessPiece;
import com.knightforge.model.ChessComponent;
import com.knightforge.model.EmptySlotComponent;
import com.knightforge.model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Board component displayed in the game window.
 */
public class Chessboard extends JComponent {
    private final ChessComponent[][] chessComponents = new ChessComponent[BoardState.BOARD_SIZE][BoardState.BOARD_SIZE];
    private final GameSession gameSession = new GameSession(new BoardState());
    // All board squares share a single controller instance.
    private final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;
    private Consumer<String> statusConsumer;
    private Function<ChessColor, PieceType> promotionHandler;


    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        refreshBoard();
    }

    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public BoardState getBoardState() {
        return gameSession.getBoardState();
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setStatusConsumer(Consumer<String> statusConsumer) {
        this.statusConsumer = statusConsumer;
        pushStatus();
    }

    public void setPromotionHandler(Function<ChessColor, PieceType> promotionHandler) {
        this.promotionHandler = promotionHandler;
    }

    public ChessColor getCurrentColor() {
        return gameSession.getCurrentColor();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        gameSession.loadGame(chessData);
        refreshBoard();
        pushStatus();
    }

    public void handleSquareClick(ChessboardPoint point) {
        if (gameSession.handleSquareClick(point)) {
            handlePromotionIfNeeded();
            refreshBoard();
        }
        pushStatus();
    }

    public void undo() {
        if (gameSession.undo()) {
            refreshBoard();
        }
        pushStatus();
    }

    public void loadPromotionTestPosition() {
        gameSession.loadPromotionTestPosition();
        refreshBoard();
        pushStatus();
    }

    private void refreshBoard() {
        removeAll();
        for (int row = 0; row < BoardState.BOARD_SIZE; row++) {
            for (int col = 0; col < BoardState.BOARD_SIZE; col++) {
                refreshSquare(row, col);
            }
        }
        revalidate();
        repaint();
    }

    private void refreshSquare(int row, int col) {
        ChessboardPoint point = new ChessboardPoint(row, col);
        ChessPiece piece = gameSession.getBoardState().getPieceAt(point);
        Set<ChessboardPoint> highlightedTargets = gameSession.getHighlightedTargets();
        ChessComponent currentComponent = chessComponents[row][col];
        if (currentComponent != null) {
            remove(currentComponent);
        }

        ChessComponent component = piece == null
                ? new EmptySlotComponent(point, calculatePoint(row, col), clickController, CHESS_SIZE)
                : new PieceChessComponent(point, calculatePoint(row, col), piece, clickController, CHESS_SIZE);
        component.setSelected(point.equals(gameSession.getSelectedPoint()));
        component.setMoveHint(highlightedTargets.contains(point));
        chessComponents[row][col] = component;
        add(component);
    }

    private void pushStatus() {
        if (statusConsumer != null) {
            statusConsumer.accept(gameSession.getStatusMessage());
        }
    }

    private void handlePromotionIfNeeded() {
        if (gameSession.getPhase() != com.knightforge.controller.GamePhase.PROMOTION_PENDING || promotionHandler == null) {
            return;
        }

        ChessColor promotingColor = gameSession.getLastMove() == null
                ? ChessColor.NONE
                : gameSession.getLastMove().getMovedPiece().getColor();
        PieceType selectedType = promotionHandler.apply(promotingColor);
        if (selectedType == null) {
            selectedType = PieceType.QUEEN;
        }
        gameSession.choosePromotion(selectedType);
    }
}
