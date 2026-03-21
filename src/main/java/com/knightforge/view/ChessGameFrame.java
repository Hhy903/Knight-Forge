package com.knightforge.view;

import com.knightforge.controller.GameController;
import com.knightforge.model.ChessColor;
import com.knightforge.model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main game window that hosts the entire UI.
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    private JLabel statusLabel;
    private static final int BUTTON_X_OFFSET = 120;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    private final File defaultResourceDirectory = new File("src/main/resources/resource");

    public ChessGameFrame(int width, int height) {
        setTitle("KnightForge"); // Set the window title.
        this.WIDTH = width;
        this.HEIGTH = height;
        this.CHESSBOARD_SIZE = HEIGTH * 4 / 5;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Close the game when the window is closed.
        setLayout(null);


        addChessboard();
        addLabel();
        addHelloButton();
        addLoadButton();
        addSaveButton();
    }


    /**
     * Adds the chessboard to the game panel.
     */
    private void addChessboard() {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        chessboard.setStatusConsumer(this::updateStatus);
        chessboard.setPromotionHandler(this::showPromotionDialog);
        chessboard.setGameOverConsumer(this::showGameOverDialog);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGTH / 10, HEIGTH / 10);
        add(chessboard);
    }

    /**
     * Adds the status label to the game panel.
     */
    private void addLabel() {
        statusLabel = new JLabel("Black to move.");
        statusLabel.setLocation(HEIGTH, HEIGTH / 10);
        statusLabel.setSize(220, 100);
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 18));
        add(statusLabel);
    }

    /**
     * Adds a sample button that shows a greeting dialog.
     */

    private void addHelloButton() {
        JButton button = new JButton("Undo");
        button.addActionListener((e) -> gameController.undo());
        button.setLocation(HEIGTH, HEIGTH / 10 + BUTTON_X_OFFSET);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(HEIGTH, HEIGTH / 10 + 210);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            JFileChooser chooser = createFileChooser("Load Game");
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            gameController.loadGameFromFile(chooser.getSelectedFile().getPath());
        });
    }

    private void addSaveButton() {
        JButton button = new JButton("Save");
        button.setLocation(HEIGTH, HEIGTH / 10 + 290);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.addActionListener(e -> {
            JFileChooser chooser = createFileChooser("Save Game");
            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            boolean success = gameController.saveGameToFile(chooser.getSelectedFile().getPath());
            updateStatus(success ? "Game saved." : "Save failed.");
        });
        add(button);
    }

    private void updateStatus(String text) {
        if (statusLabel == null) {
            return;
        }
        statusLabel.setText("<html>" + (text == null ? "Black to move." : text) + "</html>");
    }

    private PieceType showPromotionDialog(ChessColor color) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String selection = (String) JOptionPane.showInputDialog(
                this,
                color.getName() + " promotion: choose a piece",
                "Promotion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selection == null) {
            return PieceType.QUEEN;
        }

        return switch (selection) {
            case "Rook" -> PieceType.ROOK;
            case "Bishop" -> PieceType.BISHOP;
            case "Knight" -> PieceType.KNIGHT;
            default -> PieceType.QUEEN;
        };
    }

    private void showGameOverDialog(String result) {
        JOptionPane.showMessageDialog(this, result, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private JFileChooser createFileChooser(String dialogTitle) {
        JFileChooser chooser = new JFileChooser(defaultResourceDirectory);
        chooser.setDialogTitle(dialogTitle);
        return chooser;
    }

}
