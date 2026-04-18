package com.knightforge.view.ViewComponents.ChessboardComponents;

import com.knightforge.controller.ChessGameController;
import com.knightforge.controller.ClickController;
import com.knightforge.model.*;
import com.knightforge.view.ViewComponents.UpdatableUIComponent;

import javax.swing.*;
import java.awt.*;

public class ChessboardComponent extends JComponent implements UpdatableUIComponent {
    private static final int LABEL_MARGIN = 24;
    private static final int DEFAULT_CHESSBOARD_SIZE = 8;
    private final int CHESS_SIZE;

    private final ChessSquareComponent[][] chessSquareComponents = new ChessSquareComponent[DEFAULT_CHESSBOARD_SIZE][DEFAULT_CHESSBOARD_SIZE];
    private final ClickController clickController = new ClickController(this);
    private final ChessGameController chessGameController;

    public ChessboardComponent(int chessboardWidth, int chessboardHeight, ChessGameController controller) {
        this.chessGameController = controller;
        setLayout(null);
        setSize(chessboardWidth + LABEL_MARGIN, chessboardHeight + LABEL_MARGIN);
        CHESS_SIZE = chessboardWidth / 8;
        initializeSquares();
    }

    private void initializeSquares() {
        for (int row = 0; row < DEFAULT_CHESSBOARD_SIZE; row++) {
            for (int col = 0; col < DEFAULT_CHESSBOARD_SIZE; col++) {
                ChessSquareComponent square = new ChessSquareComponent(
                        new ChessboardPosition(row, col),
                        calculatePoint(row, col),
                        clickController,
                        CHESS_SIZE
                );
                chessSquareComponents[row][col] = square;
                add(square);
            }
        }
    }

    @Override
    public void updateGameState(GameState gameState) {
        for (ChessSquareComponent[] row : chessSquareComponents) {
            for (ChessSquareComponent square : row) {
                square.updateGameState(gameState);
            }
        }
        repaint();
    }

    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + LABEL_MARGIN, row * CHESS_SIZE);
    }

    public void handleSquareClick(ChessboardPosition selectedPosition) {
        chessGameController.handleSquareClick(selectedPosition);
    }
}