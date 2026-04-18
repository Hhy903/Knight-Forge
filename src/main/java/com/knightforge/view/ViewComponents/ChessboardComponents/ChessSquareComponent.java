package com.knightforge.view.ViewComponents.ChessboardComponents;

import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.GameState;
import com.knightforge.view.ViewComponents.UpdatableUIComponent;
import com.knightforge.view.SkinManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ChessSquareComponent extends JComponent implements UpdatableUIComponent {
    private static final Color[] BACKGROUND_COLORS = {Color.WHITE, Color.BLACK};
    private final ClickController clickController;
    private final ChessboardPosition chessboardPosition;
    private final SkinManager skinManager = SkinManager.getInstance();

    private ChessPiece piece;
    private boolean selected;
    private boolean moveHint;

    public ChessSquareComponent(ChessboardPosition chessboardPosition, Point location, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPosition = chessboardPosition;
        this.clickController = clickController;
    }

    @Override
    public void updateGameState(GameState state) {
        piece = state.board()[chessboardPosition.getX()][chessboardPosition.getY()];
        moveHint = state.legalMoves().contains(chessboardPosition);
        selected = chessboardPosition.equals(state.selectedPosition());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(BACKGROUND_COLORS[(chessboardPosition.getX() + chessboardPosition.getY()) % 2]);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Piece
        if (piece != null) {
            g.drawImage(skinManager.getPieceImage(piece.getType(), piece.getColor()), 0, 0, getWidth(), getHeight(), this);
        }

        // Move hint
        if (moveHint) {
            g.setColor(new Color(46, 204, 113, 170));
            g.fillOval(getWidth() / 3, getHeight() / 3, getWidth() / 3, getHeight() / 3);
        }

        // Selected
        if (selected) {
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            clickController.onClick(this);
        }
    }

    public ChessboardPosition getChessboardPoint() {
        return chessboardPosition;
    }
}