package com.knightforge.view;

import com.knightforge.controller.ClickController;
import com.knightforge.model.BoardState;
import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessPiece;
import com.knightforge.model.ChessComponent;
import com.knightforge.model.PieceType;

import java.awt.*;

/**
 * Swing component for rendering a non-empty chess piece.
 */
public class PieceChessComponent extends ChessComponent {
    private final ChessPiece piece;
    private final SkinManager skinManager = SkinManager.getInstance();

    public PieceChessComponent(ChessboardPoint chessboardPoint, Point location, ChessPiece piece, ClickController listener, int size) {
        super(chessboardPoint, location, listener, size);
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
    public boolean canMoveTo(BoardState boardState, ChessboardPoint destination) {
        return boardState.isLegalMove(getChessboardPoint(), destination);
    }

    @Override
    public void loadResource() {
        skinManager.getPieceImage(piece.getType(), ChessColor.WHITE);
        skinManager.getPieceImage(piece.getType(), ChessColor.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(skinManager.getPieceImage(piece.getType(), piece.getColor()), 0, 0, getWidth(), getHeight(), this);
        if (isMoveHint()) {
            g.setColor(new Color(46, 204, 113, 170));
            g.fillOval(getWidth() / 3, getHeight() / 3, getWidth() / 3, getHeight() / 3);
        }
        if (isSelected()) {
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
