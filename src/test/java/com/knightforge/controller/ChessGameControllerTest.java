package com.knightforge.controller;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessGame;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.GameMode;
import com.knightforge.model.PieceType;
import com.knightforge.view.ChessGameView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessGameControllerTest {
    @TempDir
    Path tempDir;

    @Test
    void saveGameToFileWritesSerializedGameState() throws Exception {
        ChessGame chessGame = new ChessGame();
        ChessGameController controller = createController(chessGame, false, null);
        Path saveFile = tempDir.resolve("saved-game.txt");

        boolean saved = controller.saveGameToFile(saveFile.toString());

        assertTrue(saved);
        assertEquals(chessGame.serializeGameState(), Files.readAllLines(saveFile));
    }

    @Test
    void loadGameFromFileRestoresBoardAndMetadata() throws Exception {
        ChessGameController controller = createController(new ChessGame(), false, null);
        Path saveFile = tempDir.resolve("loadable-game.txt");
        List<String> saveData = List.of(
                "CURRENT:BLACK",
                "CASTLE:Kq",
                "EN_PASSANT:2,3",
                "HALFMOVE:12",
                "bR -- -- -- bK -- -- bR",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- wP bP -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "wR -- -- -- wK -- -- wR");
        Files.write(saveFile, saveData);

        controller.loadGameFromFile(saveFile.toString());

        assertEquals(saveData, controller.chessGameModel.serializeGameState());
    }

    @Test
    void loadGameFromFileThrowsWhenHeadersAreInvalid() throws Exception {
        ChessGameController controller = createController(new ChessGame(), false, null);
        Path invalidSave = tempDir.resolve("invalid-game.txt");
        Files.write(invalidSave, List.of(
                "CURRENT:WHITE",
                "BAD_HEADER:KQkq",
                "EN_PASSANT:-",
                "HALFMOVE:0",
                "-- -- -- -- bK -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> controller.loadGameFromFile(invalidSave.toString()));

        assertTrue(exception.getMessage().contains("Failed to load game from"));
    }

    @Test
    void handleSquareClickCompletesPromotionUsingViewSelection() throws Exception {
        ChessGame chessGame = new ChessGame();
        chessGame.loadGameState(loadPromotionBoard(), ChessColor.WHITE, "-", null, 0);
        TestChessGameView view = allocatePromotionView("Knight");
        ChessGameController controller = createController(chessGame, false, view);

        controller.handleSquareClick(new ChessboardPosition(1, 0));
        controller.handleSquareClick(new ChessboardPosition(0, 0));

        assertEquals(GameMode.GAME_OVER, chessGame.getState().mode());
        assertEquals("Draw by insufficient material.", chessGame.getState().statusMessage());
        assertEquals(ChessColor.BLACK, chessGame.getState().currentTurn());
        assertNull(chessGame.getState().board()[1][0]);
        assertNotNull(chessGame.getState().board()[0][0]);
        assertEquals(PieceType.KNIGHT, chessGame.getState().board()[0][0].getType());
    }

    @Test
    void undoLastMoveWithAiEnabledRewindsBothSidesTurns() throws Exception {
        ChessGame chessGame = new ChessGame();
        ChessGameController controller = createController(chessGame, true, null);

        chessGame.selectPosition(new ChessboardPosition(6, 4));
        chessGame.selectPosition(new ChessboardPosition(4, 4));
        chessGame.selectPosition(new ChessboardPosition(1, 4));
        chessGame.selectPosition(new ChessboardPosition(3, 4));

        controller.undoLastMove();

        assertEquals(ChessColor.WHITE, chessGame.getCurrentTurn());
        assertEquals(PieceType.PAWN, chessGame.getState().board()[6][4].getType());
        assertEquals(PieceType.PAWN, chessGame.getState().board()[1][4].getType());
        assertNull(chessGame.getState().board()[4][4]);
        assertNull(chessGame.getState().board()[3][4]);
    }

    private static ChessGameController createController(ChessGame chessGame, boolean aiEnabled, ChessGameView view) throws Exception {
        ChessGameController controller = (ChessGameController) getUnsafe().allocateInstance(ChessGameController.class);
        setField(controller, "chessGameModel", chessGame);
        setField(controller, "chessGameView", view);
        setField(controller, "aiEnabled", aiEnabled);
        return controller;
    }

    private static TestChessGameView allocatePromotionView(String selection) throws Exception {
        TestChessGameView view = (TestChessGameView) getUnsafe().allocateInstance(TestChessGameView.class);
        view.selection = selection;
        return view;
    }

    private static Unsafe getUnsafe() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ChessGameController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static com.knightforge.model.Chessboard loadPromotionBoard() {
        com.knightforge.model.Chessboard chessboard = new com.knightforge.model.Chessboard();
        chessboard.loadFromLines(List.of(
                "-- -- -- -- bK -- -- --",
                "wP -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- -- -- -- --",
                "-- -- -- -- wK -- -- --"));
        return chessboard;
    }

    private static class TestChessGameView extends ChessGameView {
        private String selection;

        private TestChessGameView() {
            super(null, null);
        }

        @Override
        public String getDesiredPromotion(String[] promotionOptions) {
            return selection;
        }
    }
}
