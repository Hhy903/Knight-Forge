package com.knightforge.view;

import com.knightforge.controller.ClickController;
import com.knightforge.model.*;
import com.knightforge.model.ChessPiece;

import java.awt.*;

/**
 * Swing component for rendering a non-empty chess piece.
 */
public class PieceChessComponent extends ChessComponent {
    private final ChessPiece piece;
    private final SkinManager skinManager = SkinManager.getInstance();

    public PieceChessComponent(ChessboardPosition chessboardPosition, Point location, ChessPiece piece, ClickController listener, int size) {
        super(chessboardPosition, location, listener, size);
        this.piece = piece;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    @Override
    public ChessColor getChessColor() {
        return piece.getColor();
    }

    @Override
    public boolean canMoveTo(BoardState boardState, ChessboardPosition destination) {
        return boardState.isLegalMove(getChessboardPoint(), destination);
    }

    @Override
    public void loadResource() {
        skinManager.getPieceImage(piece.getType(), ChessColor.WHITE);
        skinManager.getPieceImage(piece.getType(), ChessColor.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(skinManager.getPieceImage(piece.getType(), piece.getColor()), 0, 0, getWidth(), getHeight(), this);
        if (isMoveHint()) {
            graphics.setColor(new Color(46, 204, 113, 170));
            graphics.fillOval(getWidth() / 3, getHeight() / 3, getWidth() / 3, getHeight() / 3);
        }
        if (isSelected()) {
            graphics.setColor(Color.RED);
            graphics.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
