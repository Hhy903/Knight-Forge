package com.knightforge.model;

import com.knightforge.controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

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

    public void setChessboardPoint(ChessboardPosition chessboardPosition) {
        this.chessboardPosition = chessboardPosition;
    }

    public abstract ChessColor getChessColor();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isMoveHint() {
        return moveHint;
    }

    public void setMoveHint(boolean moveHint) {
        this.moveHint = moveHint;
    }

    /**
     * @param another the other square to swap with during a move
     */
    public void swapLocation(ChessComponent another) {
        ChessboardPosition chessboardPosition1 = getChessboardPoint(), chessboardPosition2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();
        setChessboardPoint(chessboardPosition2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPosition1);
        another.setLocation(point1);
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

    /**
     * @param chessboard the full board state
     * @param destination the target square, such as (0, 0) or (0, 7)
     * @return whether this piece can legally move from its current position to the target
     */
    public abstract boolean canMoveTo(BoardState boardState, ChessboardPosition destination);

    /**
     * Loads static resources such as piece images.
     *
     * @throws IOException if a required resource cannot be found or loaded
     */
    public abstract void loadResource() throws IOException;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color squareColor = BACKGROUND_COLORS[(chessboardPosition.getX() + chessboardPosition.getY()) % 2];
        g.setColor(squareColor);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
