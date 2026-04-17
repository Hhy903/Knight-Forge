package com.knightforge.view.ChessboardComponents;

import com.knightforge.controller.ClickController;
import com.knightforge.model.*;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.view.SkinManager;

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
