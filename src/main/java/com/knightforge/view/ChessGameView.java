package com.knightforge.view;

import com.knightforge.controller.ChessGameController;
import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessGame;
import com.knightforge.model.GameState;
import com.knightforge.model.ObservableChessGame;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ChessGameView extends JFrame implements ChessGameObserver{
    private final int DEFAULT_WIDTH = 1000;
    private final int DEFAULT_HEIGHT = 760;
    public final int DEFAULT_CHESSBOARD_SIZE = DEFAULT_HEIGHT * 4 / 5;
    private static final int BUTTON_X_OFFSET = 120;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    private final File defaultResourceDirectory = new File("src/main/resources/resource");

    ObservableChessGame chessGameModel;
    ChessGameController chessGameController;

    private JLabel statusLabel;

    public ChessGameView (ChessGameController chessGameController, ObservableChessGame chessGameModel){
        this.chessGameController = chessGameController;
        this.chessGameModel = chessGameModel;

        setTitle("KnightForge"); // Set the window title.

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Close the game when the window is closed.
        setLayout(null);

        chessGameModel.addObserver(this);
    }

    public void createView(){
        // Create Swing Components
        addChessboard();
        addLabel();
        addUndoButton();
        addLoadButton();
        addSaveButton();
    }

    private void addChessboard() {
        ChessboardView chessboard = new ChessboardView(DEFAULT_CHESSBOARD_SIZE, DEFAULT_CHESSBOARD_SIZE, chessGameModel, chessGameController);
        chessboard.setLocation(DEFAULT_HEIGHT / 10, DEFAULT_HEIGHT / 10);
        add(chessboard);
    }

    private void addLabel() {
        statusLabel = new JLabel();
        statusLabel.setLocation(DEFAULT_HEIGHT, DEFAULT_HEIGHT / 10);
        statusLabel.setSize(220, 100);
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 18));
        add(statusLabel);
        updateStatus("White to Move");
    }

    private void addUndoButton() {
        JButton button = new JButton("Undo");
        button.addActionListener((e) -> chessGameController.undoLastMove());
        button.setLocation(DEFAULT_HEIGHT, DEFAULT_HEIGHT / 10 + BUTTON_X_OFFSET);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(DEFAULT_HEIGHT, DEFAULT_HEIGHT / 10 + 210);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            JFileChooser chooser = createFileChooser("Load Game");
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            chessGameController.loadGameFromFile(chooser.getSelectedFile().getPath());
        });
    }

    private void addSaveButton() {
        JButton button = new JButton("Save");
        button.setLocation(DEFAULT_HEIGHT, DEFAULT_HEIGHT / 10 + 290);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.addActionListener(e -> {
            JFileChooser chooser = createFileChooser("Save Game");
            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            boolean success = chessGameController.saveGameToFile(chooser.getSelectedFile().getPath());
            updateStatus(success ? "Game saved." : "Save failed.");
        });
        add(button);
    }

    private JFileChooser createFileChooser(String dialogTitle) {
        JFileChooser chooser = new JFileChooser(defaultResourceDirectory);
        chooser.setDialogTitle(dialogTitle);
        return chooser;
    }

    private void updateStatus(String whoseTurn) {
        if (statusLabel == null) {
            return;
        }
        statusLabel.setText("<html>" + (whoseTurn == null ? "White to move." : whoseTurn + " to move.") + "</html>");
    }

    public void createControls(){

    }

    @Override
    public void updateGameState(GameState gameState) {
        updateStatus(gameState.currentTurn().getName());
    }

    public String getDesiredPromotion(String[] promotionOptions) {
        String selection = (String) JOptionPane.showInputDialog(
                this,
                "Promotion: choose a piece",
                "Promotion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                promotionOptions,
                promotionOptions[0]
        );

        if (selection == null) {
            return "Queen";
        }

        return selection;
    }

}
