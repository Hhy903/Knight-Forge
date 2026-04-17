package com.knightforge.view;

import com.knightforge.controller.ChessGameController;
import com.knightforge.controller.ClickController;
import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.view.ChessboardComponents.ChessComponent;
import com.knightforge.view.ChessboardComponents.PieceComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessboardView extends JComponent implements ChessGameObserver{
    private static final int LABEL_MARGIN = 24;
    private final int CHESS_SIZE;
    private static final int DEFAULT_CHESSBOARD_SIZE = 8;

    private final ChessComponent[][] chessComponents = new ChessComponent[DEFAULT_CHESSBOARD_SIZE][DEFAULT_CHESSBOARD_SIZE];
    private final PieceComponentFactory componentFactory = new PieceComponentFactory();
    private final ClickController clickController = new ClickController(this);

    private final ChessGameController chessGameController;

    public ChessboardView(int chessboardWidth, int chessboardHeight, ChessGameController controller) {
        this.chessGameController = controller;
        setLayout(null); // Use absolute layout.
        setSize(chessboardWidth + LABEL_MARGIN, chessboardHeight + LABEL_MARGIN);
        CHESS_SIZE = chessboardWidth / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", chessboardWidth, CHESS_SIZE);
    }

    private void refreshBoard(ChessPiece[][] board, List<ChessboardPosition> currentLegalMoves) {
        removeAll();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                refreshSquare(row, col, board, currentLegalMoves);
            }
        }
        revalidate();
        repaint();
    }

    private void refreshSquare(int row, int col, ChessPiece[][] board, List<ChessboardPosition> currentLegalMoves) {
        ChessboardPosition point = new ChessboardPosition(row, col);
        ChessPiece pieceAtPosition = board[row][col];

        ChessComponent component = componentFactory.createComponent(
                point,
                calculatePoint(row, col),
                pieceAtPosition,
                clickController,
                CHESS_SIZE
        );

        if (currentLegalMoves.contains(point)){
            component.setMoveHint(true);
        }

        chessComponents[row][col] = component;
        add(component);
    }
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + LABEL_MARGIN, row * CHESS_SIZE);
    }

    public void handleSquareClick(ChessboardPosition selectedPosition) {
        chessGameController.handleSquareClick(selectedPosition);
    }

    @Override
    public void updateGameState(GameState gameState) {
        refreshBoard(gameState.board(), gameState.legalMoves());
    }
}
