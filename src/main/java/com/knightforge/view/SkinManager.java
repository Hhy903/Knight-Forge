package com.knightforge.view;

import com.knightforge.model.ChessColor;
import com.knightforge.model.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton manager for shared chess piece images.
 */
public final class SkinManager {
    private static final SkinManager INSTANCE = new SkinManager();

    private final Map<ChessColor, Map<PieceType, Image>> imageCache = new EnumMap<>(ChessColor.class);

    private SkinManager() {
        imageCache.put(ChessColor.WHITE, new EnumMap<>(PieceType.class));
        imageCache.put(ChessColor.BLACK, new EnumMap<>(PieceType.class));
    }

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    public Image getPieceImage(PieceType type, ChessColor color) {
        if (color == ChessColor.NONE) {
            throw new IllegalArgumentException("No image exists for ChessColor.NONE");
        }

        Map<PieceType, Image> colorCache = imageCache.get(color);
        return colorCache.computeIfAbsent(type, key -> loadImage(key, color));
    }

    private Image loadImage(PieceType type, ChessColor color) {
        String resourcePath = "/images/" + type.getResourceName() + "-" + color.getName().toLowerCase() + ".png";
        try (InputStream stream = Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Missing resource: " + resourcePath)) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load image: " + resourcePath, e);
        }
    }
}
