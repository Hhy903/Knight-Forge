package com.knightforge.view;


import com.knightforge.controller.ClickController;
import com.knightforge.controller.GameSessionEvent;
import com.knightforge.controller.GameSessionListener;
import com.knightforge.controller.GameSession;
import com.knightforge.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Board component displayed in the game window.
 */
public class Chessboard extends JComponent implements GameSessionListener {
    private static final int LABEL_MARGIN = 24;
    private final ChessComponent[][] chessComponents = new ChessComponent[BoardState.BOARD_SIZE][BoardState.BOARD_SIZE];
    private final GameSession gameSession = new GameSession(new BoardState());
    private final PieceComponentFactory componentFactory = new PieceComponentFactory();
    // All board squares share a single controller instance.
    private final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;
    private Consumer<String> statusConsumer;
    private Consumer<String> gameOverConsumer;
    private Function<ChessColor, PieceType> promotionHandler;
    private String lastPublishedGameResult;


    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width + LABEL_MARGIN, height + LABEL_MARGIN);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        gameSession.addListener(this);
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

    public void setGameOverConsumer(Consumer<String> gameOverConsumer) {
        this.gameOverConsumer = gameOverConsumer;
    }

    public ChessColor getCurrentColor() {
        return gameSession.getCurrentColor();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBoardLabels(graphics2D);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + LABEL_MARGIN, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        gameSession.loadGame(chessData);
    }

    public List<String> saveGame() {
        return gameSession.saveGame();
    }

    public void handleSquareClick(ChessboardPosition point) {
        if (gameSession.handleSquareClick(point)) {
            handlePromotionIfNeeded();
        }
    }

    public void undo() {
        gameSession.undo();
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
        ChessboardPosition point = new ChessboardPosition(row, col);
        ChessPiece piece = gameSession.getBoardState().getPieceAt(point);
        Set<ChessboardPosition> highlightedTargets = gameSession.getHighlightedTargets();
        ChessComponent currentComponent = chessComponents[row][col];
        if (currentComponent != null) {
            remove(currentComponent);
        }

        ChessComponent component = componentFactory.createComponent(
                point,
                calculatePoint(row, col),
                piece,
                clickController,
                CHESS_SIZE
        );
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

    private void publishGameOverIfNeeded() {
        String currentResult = gameSession.getGameResult();
        if (currentResult == null) {
            lastPublishedGameResult = null;
            return;
        }
        if (gameOverConsumer != null && !currentResult.equals(lastPublishedGameResult)) {
            lastPublishedGameResult = currentResult;
            gameOverConsumer.accept(currentResult);
        }
    }

    @Override
    public void onSessionChanged(GameSessionEvent event) {
        refreshBoard();
        pushStatus();
        publishGameOverIfNeeded();
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

    private void drawBoardLabels(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(70, 70, 70));
        graphics2D.setFont(new Font("Rockwell", Font.BOLD, 14));
        FontMetrics metrics = graphics2D.getFontMetrics();

        for (int col = 0; col < BoardState.BOARD_SIZE; col++) {
            String file = String.valueOf((char) ('a' + col));
            int x = LABEL_MARGIN + col * CHESS_SIZE + (CHESS_SIZE - metrics.stringWidth(file)) / 2;
            int y = BoardState.BOARD_SIZE * CHESS_SIZE + metrics.getAscent() + 2;
            graphics2D.drawString(file, x, y);
        }

        for (int row = 0; row < BoardState.BOARD_SIZE; row++) {
            String rank = String.valueOf(8 - row);
            int x = (LABEL_MARGIN - metrics.stringWidth(rank)) / 2;
            int y = row * CHESS_SIZE + (CHESS_SIZE + metrics.getAscent()) / 2 - 2;
            graphics2D.drawString(rank, x, y);
        }
    }
}
