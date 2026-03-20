package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Represents a rook in chess.
 */
public class RookChessComponent extends ChessComponent {
    /**
     * Shared rook images for both colors.
     * FIXME: The loaded images must use transparent backgrounds.
     */
    private static Image ROOK_WHITE;
    private static Image ROOK_BLACK;

    /**
     * The image used by this rook instance.
     */
    private Image rookImage;

    /**
     * Loads the rook images from the classpath.
     *
     * @throws IOException if the image files cannot be loaded
     */
    public void loadResource() throws IOException {
        if (ROOK_WHITE == null) {
            try (InputStream stream = Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/rook-white.png"),
                    "Missing resource: /images/rook-white.png")) {
                ROOK_WHITE = ImageIO.read(stream);
            }
        }

        if (ROOK_BLACK == null) {
            try (InputStream stream = Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/rook-black.png"),
                    "Missing resource: /images/rook-black.png")) {
                ROOK_BLACK = ImageIO.read(stream);
            }
        }
    }


    /**
     * Chooses the correct rook image for the given color during construction.
     *
     * @param color the piece color
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                rookImage = ROOK_WHITE;
            } else if (color == ChessColor.BLACK) {
                rookImage = ROOK_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RookChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateRookImage(color);
    }

    /**
     * Rook movement rules.
     *
     * @param chessComponents the board state
     * @param destination the target square, such as (0, 0) or (0, 7)
     * @return whether the move is legal for a rook
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        if (source.getX() == destination.getX()) {
            int row = source.getX();
            for (int col = Math.min(source.getY(), destination.getY()) + 1;
                 col < Math.max(source.getY(), destination.getY()); col++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (source.getY() == destination.getY()) {
            int col = source.getY();
            for (int row = Math.min(source.getX(), destination.getX()) + 1;
                 row < Math.max(source.getX(), destination.getX()); row++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else { // Not on the same row or column.
            return false;
        }
        return true;
    }

    /**
     * Repaints the rook whenever Swing requests a redraw.
     *
     * @param g the drawing context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(rookImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth(), getHeight());
        }
    }
}
