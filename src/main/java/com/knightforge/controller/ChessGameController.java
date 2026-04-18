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
}
