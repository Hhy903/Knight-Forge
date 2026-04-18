package com.knightforge.controller;

import com.knightforge.model.*;
import com.knightforge.view.ChessGameView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChessGameController {
    ChessGame chessGameModel;
    ChessGameView chessGameView;

    List<Move> possibleMoves = new ArrayList<>();

    public ChessGameController (ChessGame chessGameModel) {
        this.chessGameModel = chessGameModel;

        chessGameView = new ChessGameView(this, chessGameModel);

        chessGameModel.addObserver(chessGameView);
        chessGameModel.setup();
    }

    public void handleSquareClick(ChessboardPosition position) {
        chessGameModel.selectPosition(position);

        if (chessGameModel.getState().mode() == GameMode.AWAITING_PROMOTION) {
            String[] options = {"Queen", "Rook", "Bishop", "Knight"};
            String selection = chessGameView.getDesiredPromotion(options);
            PieceType promotionPiece = switch (selection) {
                case "Rook" -> PieceType.ROOK;
                case "Bishop" -> PieceType.BISHOP;
                case "Knight" -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            };
            chessGameModel.handlePromotionSelection(promotionPiece);
        }
    }

    public void showView(){
        chessGameView.setVisible(true);
    }

    public void undoLastMove() {
        chessGameModel.undoLastMove();
    }

    // TODO
    public void loadGameFromFile(String filepath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filepath));
            SaveData saveData = parseSaveData(lines);

            Chessboard loadedBoard = new Chessboard();
            loadedBoard.loadFromLines(saveData.boardLines());
            chessGameModel.loadGameState(
                    loadedBoard,
                    saveData.currentTurn(),
                    saveData.castleRights(),
                    saveData.enPassantTarget(),
                    saveData.halfmoveClock()
            );
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to load game from " + filepath, e);
        }
    }

    // TODO
    public boolean saveGameToFile(String filepath) {
        try {
            Files.write(Path.of(filepath), chessGameModel.serializeGameState());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private SaveData parseSaveData(List<String> lines) {
        if (lines.size() < 12) {
            throw new IllegalArgumentException("Save file must include 4 metadata lines and 8 board rows.");
        }

        String currentLine = lines.get(0).trim();
        String castleLine = lines.get(1).trim();
        String enPassantLine = lines.get(2).trim();
        String halfmoveLine = lines.get(3).trim();

        if (!currentLine.startsWith("CURRENT:")) {
            throw new IllegalArgumentException("Missing CURRENT header.");
        }
        if (!castleLine.startsWith("CASTLE:")) {
            throw new IllegalArgumentException("Missing CASTLE header.");
        }
        if (!enPassantLine.startsWith("EN_PASSANT:")) {
            throw new IllegalArgumentException("Missing EN_PASSANT header.");
        }
        if (!halfmoveLine.startsWith("HALFMOVE:")) {
            throw new IllegalArgumentException("Missing HALFMOVE header.");
        }

        ChessColor currentTurn = ChessColor.valueOf(currentLine.substring("CURRENT:".length()).trim());
        String castleRights = normalizeCastleRights(castleLine.substring("CASTLE:".length()).trim());
        ChessboardPosition enPassantTarget = parseEnPassant(enPassantLine.substring("EN_PASSANT:".length()).trim());
        int halfmoveClock = Integer.parseInt(halfmoveLine.substring("HALFMOVE:".length()).trim());

        List<String> boardLines = new ArrayList<>();
        for (int index = 4; index < 12; index++) {
            boardLines.add(lines.get(index).trim());
        }

        return new SaveData(currentTurn, castleRights, enPassantTarget, halfmoveClock, boardLines);
    }

    private String normalizeCastleRights(String rawCastleRights) {
        return rawCastleRights.equals("-") ? "" : rawCastleRights;
    }

    private ChessboardPosition parseEnPassant(String rawEnPassant) {
        if (rawEnPassant.equals("-")) {
            return null;
        }
        String[] coordinates = rawEnPassant.split(",");
        if (coordinates.length != 2) {
            throw new IllegalArgumentException("Invalid EN_PASSANT value.");
        }
        return new ChessboardPosition(Integer.parseInt(coordinates[0].trim()), Integer.parseInt(coordinates[1].trim()));
    }

    private record SaveData(
            ChessColor currentTurn,
            String castleRights,
            ChessboardPosition enPassantTarget,
            int halfmoveClock,
            List<String> boardLines
    ) {}

//    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition position){
//        possibleMoves = chessGameModel.getAllPossibleMoves(position);
//        return possibleMoves.stream().map(move -> new ChessboardPosition(move.getTo().getX(), move.getTo().getY())).toList();
//    }
}
