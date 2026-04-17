package com.knightforge.controller;

import com.knightforge.model.*;
import com.knightforge.view.ChessGameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessGameController {
    ChessGame chessGameModel;
    ChessGameView chessGameView;

    List<MoveNew> possibleMoves = new ArrayList<>();

    public ChessGameController (ChessGame chessGameModel) {
        this.chessGameModel = chessGameModel;
        chessGameView = new ChessGameView(this, chessGameModel);
        chessGameView.createView();
        chessGameView.createControls();
    }

    public void showView(){
        chessGameView.setVisible(true);
    }

    public void undoLastMove() {
        chessGameModel.undoLastMove();
    }

    public void loadGameFromFile(String filepath) {
        throw new Error("Implement me!");
    }

    public boolean saveGameToFile(String filepath) {
        throw new Error("Implement me!");
    }

    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition position){
        possibleMoves = chessGameModel.getAllPossibleMoves(position);
        return possibleMoves.stream().map(move -> new ChessboardPosition(move.getTo().getX(), move.getTo().getY())).toList();
    }

    public void executeMove(ChessboardPosition from, ChessboardPosition to) {
        Optional<MoveNew> moveToMake = possibleMoves.stream().filter(move -> move.getTo().equals(to) && move.getFrom().equals(from)).findFirst();
        if (moveToMake.isEmpty()) {
            throw new IllegalStateException();
        }
        try {
            chessGameModel.executeMove(moveToMake.get());
        } catch (PromotionRequiredException e){
            String[] options = {"Queen", "Rook", "Bishop", "Knight"};
            String selection = chessGameView.getDesiredPromotion(options, moveToMake.get().getActivePiece().getColor());

            PieceType promotionPiece = switch (selection) {
                case "Rook" -> PieceType.ROOK;
                case "Bishop" -> PieceType.BISHOP;
                case "Knight" -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            };

            chessGameModel.executePromotionMove(moveToMake.get(), promotionPiece);
        }
    }
}
