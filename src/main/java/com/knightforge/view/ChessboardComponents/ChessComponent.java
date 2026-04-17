package com.knightforge.view.ChessboardComponents;

import com.knightforge.controller.ClickController;
import com.knightforge.model.ChessboardPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Abstract base class for every square on the 8x8 board.
 * Current implementations are {@link EmptySlotComponent} and piece-bearing square components.
 */
public abstract class ChessComponent extends JComponent {

    /**
     * Each piece is rendered from an image, so every occupied square uses a fixed square area.
     */

//    private static final Dimension CHESSGRID_SIZE = new Dimension(1080 / 4 * 3 / 8, 1080 / 4 * 3 / 8);
    private static final Color[] BACKGROUND_COLORS = {Color.WHITE, Color.BLACK};
    /**
     * Handles click events.
     */
    private final ClickController clickController;

    /**
     * `chessboardPoint` stores the square position.
     * `chessColor` stores the owner color.
     * `selected` marks whether the square is currently highlighted.
     */
    private ChessboardPosition chessboardPosition;
    private boolean selected;
    private boolean moveHint;

    protected ChessComponent(ChessboardPosition chessboardPosition, Point location, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPosition = chessboardPosition;
        this.selected = false;
        this.clickController = clickController;
    }

    public ChessboardPosition getChessboardPoint() {
        return chessboardPosition;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setMoveHint(boolean bool){
        moveHint = bool;
    }
    public boolean isMoveHint() {
        return moveHint;
    }

    /**
     * @param e the mouse event raised by Swing
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            System.out.printf("Click [%d,%d]\n", chessboardPosition.getX(), chessboardPosition.getY());
            clickController.onClick(this);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color squareColor = BACKGROUND_COLORS[(chessboardPosition.getX() + chessboardPosition.getY()) % 2];
        g.setColor(squareColor);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
