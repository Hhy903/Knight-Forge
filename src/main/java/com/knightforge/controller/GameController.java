package com.knightforge.controller;

import com.knightforge.view.Chessboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameController {
    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<String> loadGameFromFile(String path) {
        try {
            List<String> chessData = Files.readAllLines(Path.of(path));
            chessboard.loadGame(chessData);
            return chessData;
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveGameToFile(String path) {
        try {
            Files.write(Path.of(path), chessboard.saveGame());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void undo() {
        chessboard.undo();
    }
}
