package com.knightforge.model;

import java.util.List;
import com.knightforge.model.ChessPieces.ChessPiece;

public class ChessGame implements ObservableChessGame{
    Chessboard chessboard;
    ChessColor whoseTurn;
    IMoveHandler moveHandler;

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

    public boolean executeMove(MoveNew move) {
        boolean moveExecutionSuccess = moveHandler.executeMove(move);
        switchTurns();
        return moveExecutionSuccess;
    }

    public void undoLastMove() {
        moveHandler.undoLastMove();
        switchTurns();
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
}
