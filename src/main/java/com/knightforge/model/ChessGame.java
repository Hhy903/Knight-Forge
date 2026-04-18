package com.knightforge.model;

import java.util.ArrayList;
import java.util.List;

import com.knightforge.view.ChessGameObserver;
import com.knightforge.model.ChessPieces.ChessPiece;

public class ChessGame implements ObservableChessGame {
    private Chessboard chessboard;
    private ChessColor whoseTurn;
    private MoveHandler moveHandler;
    private List<ChessGameObserver> observers = new ArrayList<>();

    // Interaction state
    private ChessboardPosition selectedSquare = null;
    private List<Move> currentLegalMoves = new ArrayList<>();
    private GameMode mode = GameMode.IDLE;
    private Move pendingPromotionMove = null;
    private String statusMessage;
    private int halfmoveClock = 0;
    private final List<Integer> halfmoveClockHistory = new ArrayList<>();

    public ChessGame(Chessboard chessboard) {
        this.chessboard = chessboard;
        this.moveHandler = new MoveHandler(chessboard);
        this.whoseTurn = ChessColor.WHITE;
        updateGameStatus();
    }

    public ChessGame() {
        this(new Chessboard());
    }

    // --- Public method for interacting with the board ---

    public void selectPosition(ChessboardPosition position) {
        switch (mode) {
            case IDLE -> handleIdleClick(position);
            case STAGING -> handleStagingClick(position);
            case AWAITING_PROMOTION -> {} // ignore board clicks until promotion is resolved
            case GAME_OVER -> {}
        }
        notifyObservers();
    }

    public void handlePromotionSelection(PieceType type) {
        if (mode != GameMode.AWAITING_PROMOTION || pendingPromotionMove == null) return;
        recordHalfmoveBeforeMove();
        moveHandler.executePromotionMove(pendingPromotionMove, type);
        updateHalfmoveClockAfterMove(pendingPromotionMove);
        pendingPromotionMove = null;
        switchTurns();
        clearSelection();
        updateGameStatus();
        notifyObservers();
    }

    public void undoLastMove() {
        if (!moveHandler.undoLastMove()) {
            return;
        }
        switchTurns();
        restoreHalfmoveClock();
        clearSelection();
        updateGameStatus();
        notifyObservers();
    }

    // --- Private methods to handle board interaction based on current gameMode ---

    private void handleIdleClick(ChessboardPosition position) {
        List<Move> moves = moveHandler.getValidMoves(whoseTurn, position);
        if (!moves.isEmpty()) {
            selectedSquare = position;
            currentLegalMoves = moves;
            mode = GameMode.STAGING;
        }
    }

    private void handleStagingClick(ChessboardPosition position) {
        if (selectedSquare.equals(position)) {
            clearSelection();
            return;
        }

        Move move = currentLegalMoves.stream()
                .filter(m -> m.getTo().equals(position))
                .findFirst()
                .orElse(null);

        if (move != null) {
            try {
                recordHalfmoveBeforeMove();
                moveHandler.executeMove(move);
                updateHalfmoveClockAfterMove(move);
                switchTurns();
                clearSelection();
                updateGameStatus();
            } catch (PromotionRequiredException e) {
                halfmoveClockHistory.remove(halfmoveClockHistory.size() - 1);
                pendingPromotionMove = move;
                mode = GameMode.AWAITING_PROMOTION;
                statusMessage = "Choose promotion piece.";
            }
        } else {
            // Only reselect if the clicked position belongs to a friendly piece with legal moves
            List<Move> moves = moveHandler.getValidMoves(whoseTurn, position);
            if (!moves.isEmpty()) {
                selectedSquare = position;
                currentLegalMoves = moves;
                mode = GameMode.STAGING;
            }
            // Otherwise — empty square or opponent piece outside legal moves — keep current selection
        }
    }

    private void clearSelection() {
        selectedSquare = null;
        currentLegalMoves = new ArrayList<>();
        mode = GameMode.IDLE;
    }

    private void updateGameStatus() {
        boolean inCheck = moveHandler.isInCheck(whoseTurn);
        boolean hasLegalMove = moveHandler.hasAnyValidMove(whoseTurn);

        if (!hasLegalMove) {
            mode = GameMode.GAME_OVER;
            if (inCheck) {
                statusMessage = "Checkmate. " + oppositeColor(whoseTurn).getName() + " wins.";
            } else {
                statusMessage = "Draw by stalemate.";
            }
            return;
        }

        if (isInsufficientMaterialDraw()) {
            mode = GameMode.GAME_OVER;
            statusMessage = "Draw by insufficient material.";
            return;
        }
        if (mode != GameMode.AWAITING_PROMOTION) {
            mode = GameMode.IDLE;
        }
        statusMessage = inCheck ? whoseTurn.getName() + " is in check." : whoseTurn.getName() + " to move.";
    }

    private void recordHalfmoveBeforeMove() {
        halfmoveClockHistory.add(halfmoveClock);
    }

    private void restoreHalfmoveClock() {
        if (halfmoveClockHistory.isEmpty()) {
            halfmoveClock = 0;
            return;
        }
        halfmoveClock = halfmoveClockHistory.remove(halfmoveClockHistory.size() - 1);
    }

    private void updateHalfmoveClockAfterMove(Move move) {
        if (move.getActivePiece().getType() == PieceType.PAWN || move.getInvolvedPiece() != null || move.isEnPassant()) {
            halfmoveClock = 0;
            return;
        }
        halfmoveClock++;
    }

    private boolean isInsufficientMaterialDraw() {
        List<PieceOnBoard> nonKingPieces = getNonKingPieces();
        if (nonKingPieces.isEmpty()) {
            return true;
        }

        if (nonKingPieces.size() == 1) {
            PieceType pieceType = nonKingPieces.get(0).piece().getType();
            return pieceType == PieceType.BISHOP || pieceType == PieceType.KNIGHT;
        }

        if (nonKingPieces.size() == 2 &&
                nonKingPieces.stream().allMatch(piece -> piece.piece().getType() == PieceType.BISHOP)) {
            return bishopsStayOnSameColorSquares(nonKingPieces.get(0).position(), nonKingPieces.get(1).position());
        }

        return false;
    }

    private List<PieceOnBoard> getNonKingPieces() {
        List<PieceOnBoard> pieces = new ArrayList<>();
        ChessPiece[][] board = chessboard.getBoard();
        for (int row = 0; row < Chessboard.BOARD_SIZE; row++) {
            for (int col = 0; col < Chessboard.BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.getType() != PieceType.KING) {
                    pieces.add(new PieceOnBoard(piece, new ChessboardPosition(row, col)));
                }
            }
        }
        return pieces;
    }

    private boolean bishopsStayOnSameColorSquares(ChessboardPosition first, ChessboardPosition second) {
        return squareColor(first) == squareColor(second);
    }

    private int squareColor(ChessboardPosition position) {
        return (position.getX() + position.getY()) % 2;
    }


    // --- Game flow/helper methods ---

    private void switchTurns() {
        whoseTurn = oppositeColor(whoseTurn);
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    // --- Observer specific method ---

    public void setup() {
        notifyObservers();
    }

    @Override
    public void addObserver(ChessGameObserver observer) {
        observers.add(observer);
    }

    public GameState getState() {
        return new GameState(
                chessboard.getBoard(),
                selectedSquare,
                currentLegalMoves.stream().map(m -> new ChessboardPosition(m.getTo().getX(), m.getTo().getY())).toList(),
                whoseTurn,
                mode,
                statusMessage
        );
    }

    public void loadGameState(Chessboard chessboard, ChessColor whoseTurn, String castleRights, ChessboardPosition enPassantTarget, int halfmoveClock) {
        this.chessboard = chessboard;
        this.moveHandler = new MoveHandler(chessboard);
        this.moveHandler.loadMetadata(whoseTurn, castleRights, enPassantTarget);
        this.whoseTurn = whoseTurn;
        this.halfmoveClock = halfmoveClock;
        this.halfmoveClockHistory.clear();
        this.pendingPromotionMove = null;
        clearSelection();
        updateGameStatus();
        notifyObservers();
    }

    public List<String> serializeGameState() {
        List<String> lines = new ArrayList<>();
        lines.add("CURRENT:" + whoseTurn.name());
        lines.add("CASTLE:" + moveHandler.getCastleRightsToken());
        lines.add("EN_PASSANT:" + formatEnPassant(moveHandler.getEnPassantTarget()));
        lines.add("HALFMOVE:" + halfmoveClock);

        ChessPiece[][] board = chessboard.getBoard();
        for (int row = 0; row < Chessboard.BOARD_SIZE; row++) {
            List<String> cells = new ArrayList<>();
            for (int col = 0; col < Chessboard.BOARD_SIZE; col++) {
                cells.add(pieceToCode(board[row][col]));
            }
            lines.add(String.join(" ", cells));
        }
        return lines;
    }

    private String formatEnPassant(ChessboardPosition position) {
        if (position == null) {
            return "-";
        }
        return position.getX() + "," + position.getY();
    }

    private String pieceToCode(ChessPiece piece) {
        if (piece == null) {
            return "--";
        }
        char colorCode = piece.getColor() == ChessColor.WHITE ? 'w' : 'b';
        char pieceCode = switch (piece.getType()) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
        return "" + colorCode + pieceCode;
    }

    private void notifyObservers() {
        GameState state = getState();
        observers.forEach(o -> o.updateGameState(state));
    }

    private record PieceOnBoard(ChessPiece piece, ChessboardPosition position) {}
}
