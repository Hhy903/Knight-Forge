package com.knightforge.view;

import com.knightforge.controller.GameController;

import javax.swing.*;
import java.awt.*;

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
    }


    /**
     * Adds the chessboard to the game panel.
     */
    private void addChessboard() {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        chessboard.setStatusConsumer(this::updateStatus);
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
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Hello, world!"));
        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(HEIGTH, HEIGTH / 10 + 240);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    private void updateStatus(String text) {
        if (statusLabel == null) {
            return;
        }
        statusLabel.setText("<html>" + (text == null ? "Black to move." : text) + "</html>");
    }

}
