package com.knightforge.model;

import com.knightforge.controller.ClickController;
import com.knightforge.view.ChessboardPoint;

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
    private ChessboardPoint chessboardPoint;
    private boolean selected;
    private boolean moveHint;

    protected ChessComponent(ChessboardPoint chessboardPoint, Point location, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPoint = chessboardPoint;
        this.selected = false;
        this.clickController = clickController;
    }

    public ChessboardPoint getChessboardPoint() {
        return chessboardPoint;
    }

    public void setChessboardPoint(ChessboardPoint chessboardPoint) {
        this.chessboardPoint = chessboardPoint;
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
        ChessboardPoint chessboardPoint1 = getChessboardPoint(), chessboardPoint2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();
        setChessboardPoint(chessboardPoint2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPoint1);
        another.setLocation(point1);
    }

    /**
     * @param e the mouse event raised by Swing
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            System.out.printf("Click [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            clickController.onClick(this);
        }
    }

    /**
     * @param chessboard the full board state
     * @param destination the target square, such as (0, 0) or (0, 7)
     * @return whether this piece can legally move from its current position to the target
     */
    public abstract boolean canMoveTo(BoardState boardState, ChessboardPoint destination);

    /**
     * Loads static resources such as piece images.
     *
     * @throws IOException if a required resource cannot be found or loaded
     */
    public abstract void loadResource() throws IOException;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color squareColor = BACKGROUND_COLORS[(chessboardPoint.getX() + chessboardPoint.getY()) % 2];
        g.setColor(squareColor);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
