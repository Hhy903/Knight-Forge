package com.knightforge.view;

import com.knightforge.controller.ClickController;
import com.knightforge.model.BoardState;
import com.knightforge.model.ChessComponent;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.ObservableChessGame;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class ChessboardView extends JComponent {
    private static final int LABEL_MARGIN = 24;
    private final int CHESS_SIZE;

    private final ChessComponent[][] chessComponents = new ChessComponent[BoardState.BOARD_SIZE][BoardState.BOARD_SIZE];
    private final PieceComponentFactory componentFactory = new PieceComponentFactory();
    private final ClickController clickController = new ClickController(this);

    private ObservableChessGame chessGame;

    public ChessboardView(int width, int height, ObservableChessGame chessGame) {
        this.chessGame = chessGame;
        setLayout(null); // Use absolute layout.
        setSize(width + LABEL_MARGIN, height + LABEL_MARGIN);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

//        gameSession.addListener(this);
        refreshBoard();
    }

    // TODO: make this class implement observer add
//    private void update(){
//
//    }

    private void refreshBoard() {
        removeAll();
        // Pull Observer Pattern
        ChessPiece[][] board = chessGame.getBoardState();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                refreshSquare(row, col, board);
            }
        }
        revalidate();
        repaint();
    }

    private void refreshSquare(int row, int col, ChessPiece[][] board) {
        ChessboardPosition point = new ChessboardPosition(row, col);
        ChessPiece pieceAtPosition = board[row][col];

        ChessComponent component = componentFactory.createComponent(
                point,
                calculatePoint(row, col),
                pieceAtPosition,
                clickController,
                CHESS_SIZE
        );
        component.setSelected(point.equals(gameSession.getSelectedPoint()));
        component.setMoveHint(highlightedTargets.contains(point));
        chessComponents[row][col] = component;
        add(component);
    }
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + LABEL_MARGIN, row * CHESS_SIZE);
    }
}
