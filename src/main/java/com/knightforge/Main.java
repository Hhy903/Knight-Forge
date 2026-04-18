package com.knightforge;

import com.knightforge.controller.ChessGameController;
import com.knightforge.model.ChessGame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            ChessGame chessGame = new ChessGame();
            ChessGameController chessGameController = new ChessGameController(chessGame);
            chessGameController.showView();
        });
    }
}
