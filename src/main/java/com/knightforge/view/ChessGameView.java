package com.knightforge.view;

import com.knightforge.controller.ChessGameController;
import com.knightforge.model.GameState;
import com.knightforge.model.ObservableChessGame;
import com.knightforge.view.ViewComponents.ChessboardComponents.ChessboardComponent;
import com.knightforge.view.ViewComponents.StatusLabelComponent;
import com.knightforge.view.ViewComponents.UpdatableUIComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private List<UpdatableUIComponent> components = new ArrayList<>();

    public ChessGameView (ChessGameController chessGameController, ObservableChessGame chessGameModel){
        this.chessGameController = chessGameController;
        this.chessGameModel = chessGameModel;

        setTitle("KnightForge"); // Set the window title.

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Close the game when the window is closed.
        setLayout(null);

        addComponents();
    }

    public void addComponents(){
        // Create Swing Components
        addChessboard();
        addLabel();
        addUndoButton();
        addLoadButton();
        addSaveButton();
    }

private <T extends Component & UpdatableUIComponent> void registerComponent(T component) {
        components.add(component);
        add(component); // Swing add, satisfied because T extends Component
    }

    private void addChessboard() {
        ChessboardComponent chessboard = new ChessboardComponent(DEFAULT_CHESSBOARD_SIZE, DEFAULT_CHESSBOARD_SIZE, chessGameController);
        chessboard.setLocation(DEFAULT_HEIGHT / 10, DEFAULT_HEIGHT / 10);
        registerComponent(chessboard);
    }

    private void addLabel() {
        StatusLabelComponent statusLabel = new StatusLabelComponent(DEFAULT_HEIGHT);
        registerComponent(statusLabel);
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
            try {
                chessGameController.loadGameFromFile(chooser.getSelectedFile().getPath());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Load Failed", JOptionPane.ERROR_MESSAGE);
            }
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
            if (!success) {
                JOptionPane.showMessageDialog(this, "Failed to save game.", "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(button);
    }

    private JFileChooser createFileChooser(String dialogTitle) {
        JFileChooser chooser = new JFileChooser(defaultResourceDirectory);
        chooser.setDialogTitle(dialogTitle);
        return chooser;
    }

    @Override
    public void updateGameState(GameState gameState) {
        for (UpdatableUIComponent component : components) {
            component.updateGameState(gameState);
        }
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
