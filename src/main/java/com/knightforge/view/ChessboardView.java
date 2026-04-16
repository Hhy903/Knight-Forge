package com.knightforge.view;

import com.knightforge.controller.ChessGameController;
import com.knightforge.controller.ClickController;
import com.knightforge.model.BoardState;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.MoveNew;
import com.knightforge.model.ObservableChessGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChessboardView extends JComponent {
    private static final int LABEL_MARGIN = 24;
    private final int CHESS_SIZE;

    private final ChessComponent[][] chessComponents = new ChessComponent[BoardState.BOARD_SIZE][BoardState.BOARD_SIZE];
    private final PieceComponentFactory componentFactory = new PieceComponentFactory();
    private final ClickController clickController = new ClickController(this);

    private ObservableChessGame chessGame;
    private ChessGameController chessGameController;
    private ChessboardPosition currentlySelectedSquare = null;
    private List<ChessboardPosition> currentlyPossibleMoves = new ArrayList<>();

    public ChessboardView(int width, int height, ObservableChessGame chessGame, ChessGameController controller) {
        this.chessGame = chessGame;
        this.chessGameController = controller;
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

        chessComponents[row][col] = component;
        add(component);
    }
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + LABEL_MARGIN, row * CHESS_SIZE);
    }

    public void handleSquareClick(ChessboardPosition selectedPosition) {
        if (currentlySelectedSquare == null) {
            // First click - select piece and show moves
            currentlySelectedSquare = selectedPosition;
            currentlyPossibleMoves = chessGameController.getPossibleMoves(selectedPosition);
            showPossibleMoves();
        }
        else if (currentlySelectedSquare.equals(selectedPosition)) {
            // Click same square - deselect
            clearSelection();
        }
        else if (currentlyPossibleMoves.contains(selectedPosition)) {
            // Click valid move destination - execute move
            chessGameController.executeMove(currentlySelectedSquare, selectedPosition);
            clearSelection();
        }
        else {
            // Click different piece - reselect
            clearSelection();
            currentlySelectedSquare = selectedPosition;
            currentlyPossibleMoves = chessGameController.getPossibleMoves(selectedPosition);
            showPossibleMoves();
        }
    }

    private void clearSelection() {
        unshowPossibleMoves();
        currentlySelectedSquare = null;
        currentlyPossibleMoves = new ArrayList<>();
    }

    private void unshowPossibleMoves() {
        for (int row = 0; row < chessComponents.length; row++) {
            for (int col = 0; col < chessComponents[0].length; col++) {
                chessComponents[row][col].setMoveHint(false);
            }
        }
        revalidate();
        repaint();
    }

    private void showPossibleMoves(){
        for (ChessboardPosition position : currentlyPossibleMoves) {
            chessComponents[position.getX()][position.getY()].setMoveHint(true);
        }
        revalidate();
        repaint();
    }
}
