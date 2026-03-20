package com.knightforge.view;

import com.knightforge.controller.ClickController;
import com.knightforge.model.BoardState;
import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessPiece;
import com.knightforge.model.ChessComponent;
import com.knightforge.model.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Swing component for rendering a non-empty chess piece.
 */
public class PieceChessComponent extends ChessComponent {
    private static final Map<PieceType, Image> WHITE_IMAGES = new EnumMap<>(PieceType.class);
    private static final Map<PieceType, Image> BLACK_IMAGES = new EnumMap<>(PieceType.class);

    private final ChessPiece piece;

    public PieceChessComponent(ChessboardPoint chessboardPoint, Point location, ChessPiece piece, ClickController listener, int size) {
        super(chessboardPoint, location, listener, size);
        this.piece = piece;
        try {
            loadResource();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load piece image: " + piece.getType(), e);
        }
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
    public void loadResource() throws IOException {
        loadImage(piece.getType(), ChessColor.WHITE, WHITE_IMAGES);
        loadImage(piece.getType(), ChessColor.BLACK, BLACK_IMAGES);
    }

    private void loadImage(PieceType type, ChessColor color, Map<PieceType, Image> cache) throws IOException {
        if (cache.containsKey(type)) {
            return;
        }

        String resourcePath = "/images/" + type.getResourceName() + "-" + color.getName().toLowerCase() + ".png";
        try (InputStream stream = Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Missing resource: " + resourcePath)) {
            cache.put(type, ImageIO.read(stream));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Map<PieceType, Image> cache = piece.getColor() == ChessColor.WHITE ? WHITE_IMAGES : BLACK_IMAGES;
        g.drawImage(cache.get(piece.getType()), 0, 0, getWidth(), getHeight(), this);
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
