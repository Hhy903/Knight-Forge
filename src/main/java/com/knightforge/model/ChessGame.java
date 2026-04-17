package com.knightforge.model;

import java.util.ArrayList;
import java.util.List;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.view.ChessGameObserver;

public class ChessGame implements ObservableChessGame{
    Chessboard chessboard;
    ChessColor whoseTurn;
    IMoveHandler moveHandler;
    List<ChessGameObserver> observers = new ArrayList<>();

    public ChessGame(Chessboard chessboard) {
        this.chessboard = chessboard;
        this.moveHandler = new MoveHandler(chessboard);
        this.whoseTurn = ChessColor.WHITE;
    }
    public ChessGame() {
        this.chessboard = new Chessboard();
        this.moveHandler = new MoveHandler(chessboard);
        this.whoseTurn = ChessColor.WHITE;
    }

    public void switchTurns() { whoseTurn = oppositeColor(whoseTurn); }

    public List<MoveNew> getAllPossibleMoves(ChessboardPosition currentPosition) {
        return moveHandler.getValidMoves(whoseTurn, currentPosition);
    }

    public void executeMove(MoveNew move) throws PromotionRequiredException {
        moveHandler.executeMove(move);
        switchTurns();
        notifyObservers();
    }

    public void executePromotionMove(MoveNew move, PieceType type) {
        moveHandler.executePromotionMove(move, type);
        switchTurns();
        notifyObservers();
    }

    public void undoLastMove() {
        moveHandler.undoLastMove();
        switchTurns();
        notifyObservers();
    }

    public List<ChessboardPosition> getLocationsOfPiece(PieceType type, ChessColor color) {
        return chessboard.getLocationsOfPiece(type, color);
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    public ChessPiece[][] getBoardState() {
        return chessboard.getBoard();
    }

    // TODO: add additional status information?
    @Override
    public String getGameStatus() {
        return whoseTurn.getName() + " to Move";
    }

    @Override
    public void addObserver(ChessGameObserver observer) {
        observers.add(observer);
    }
    private void notifyObservers() {
        observers.forEach(ChessGameObserver::updateGameState);
    }
}
