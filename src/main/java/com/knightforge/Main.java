package com.knightforge;

import com.knightforge.controller.ChessGameController;
import com.knightforge.model.ChessGame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");

            Object[] modeOptions = {"Local PvP", "Play vs AI"};
            int selectedMode = JOptionPane.showOptionDialog(
                    null,
                    "Choose game mode",
                    "KnightForge",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    modeOptions,
                    modeOptions[0]
            );

            ChessGame chessGame = new ChessGame();
            boolean aiEnabled = selectedMode == 1;
            ChessGameController chessGameController = new ChessGameController(chessGame, aiEnabled);
            chessGameController.showView();
        });
    }
}
