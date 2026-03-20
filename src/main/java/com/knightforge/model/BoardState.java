package com.knightforge.model;

import com.knightforge.view.ChessboardPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the domain state of a chess game independently from Swing components.
 */
public class BoardState {
    public static final int BOARD_SIZE = 8;

    private final ChessPiece[][] board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
    private final List<Move> moveHistory = new ArrayList<>();
    private ChessColor currentColor = ChessColor.BLACK;
    private ChessboardPoint enPassantTarget;

    public BoardState() {
        reset();
    }

    public void reset() {
        clearBoard();
        setupInitialPieces();
        moveHistory.clear();
        currentColor = ChessColor.BLACK;
        enPassantTarget = null;
    }

    public void loadPromotionTestPosition() {
        clearBoard();
        moveHistory.clear();
        enPassantTarget = null;
        currentColor = ChessColor.BLACK;

        board[0][4] = new ChessPiece(PieceType.KING, ChessColor.BLACK);
        board[7][4] = new ChessPiece(PieceType.KING, ChessColor.WHITE);
        board[6][0] = new ChessPiece(PieceType.PAWN, ChessColor.BLACK);
    }

    public ChessPiece getPieceAt(ChessboardPoint point) {
        return board[point.getX()][point.getY()];
    }

    public ChessPiece getPieceAt(int row, int col) {
        return board[row][col];
    }

    public void setPieceAt(ChessboardPoint point, ChessPiece piece) {
        board[point.getX()][point.getY()] = piece;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public List<Move> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    public ChessboardPoint getEnPassantTarget() {
        return enPassantTarget;
    }

    public boolean isCurrentPlayerPiece(ChessboardPoint point) {
        ChessPiece piece = getPieceAt(point);
        return piece != null && piece.getColor() == currentColor;
    }

    public boolean isLegalMove(ChessboardPoint from, ChessboardPoint to) {
        if (!isPseudoLegalMove(from, to)) {
            return false;
        }
        return !wouldLeaveKingInCheck(from, to);
    }

    public List<ChessboardPoint> getLegalMovesFrom(ChessboardPoint from) {
        ChessPiece piece = getPieceAt(from);
        if (piece == null || piece.getColor() != currentColor) {
            return List.of();
        }

        List<ChessboardPoint> legalMoves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessboardPoint target = new ChessboardPoint(row, col);
                if (isLegalMove(from, target)) {
                    legalMoves.add(target);
                }
            }
        }
        return legalMoves;
    }

    public boolean isInCheck(ChessColor color) {
        ChessboardPoint kingPoint = findKing(color);
        return kingPoint != null && isSquareUnderAttack(kingPoint, oppositeColor(color));
    }

    public boolean hasAnyLegalMove(ChessColor color) {
        ChessColor originalColor = currentColor;
        currentColor = color;
        try {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    ChessPiece piece = board[row][col];
                    if (piece == null || piece.getColor() != color) {
                        continue;
                    }
                    if (!getLegalMovesFrom(new ChessboardPoint(row, col)).isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            currentColor = originalColor;
        }
    }

    public ChessboardPoint findKing(ChessColor color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                    return new ChessboardPoint(row, col);
                }
            }
        }
        return null;
    }

    public boolean isSquareUnderAttack(ChessboardPoint target, ChessColor attackerColor) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece == null || piece.getColor() != attackerColor) {
                    continue;
                }
                if (canAttack(new ChessboardPoint(row, col), target, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPseudoLegalMove(ChessboardPoint from, ChessboardPoint to) {
        if (!isInsideBoard(from) || !isInsideBoard(to) || from.equals(to)) {
            return false;
        }

        ChessPiece movingPiece = getPieceAt(from);
        if (movingPiece == null || movingPiece.getColor() != currentColor) {
            return false;
        }

        ChessPiece targetPiece = getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor() == movingPiece.getColor()) {
            return false;
        }

        return switch (movingPiece.getType()) {
            case ROOK -> canRookMove(from, to);
            case BISHOP -> canBishopMove(from, to);
            case QUEEN -> canRookMove(from, to) || canBishopMove(from, to);
            case KNIGHT -> canKnightMove(from, to);
            case KING -> canKingMove(from, to);
            case PAWN -> canPawnMove(from, to, movingPiece.getColor());
        };
    }

    public Move applyMove(ChessboardPoint from, ChessboardPoint to) {
        if (!isLegalMove(from, to)) {
            return null;
        }

        ChessPiece movedPiece = getPieceAt(from);
        ChessboardPoint previousEnPassantTarget = copyPoint(enPassantTarget);
        ChessPiece capturedPiece = getPieceAt(to);
        ChessboardPoint capturedPiecePoint = copyPoint(to);

        if (isEnPassantCapture(from, to, movedPiece)) {
            capturedPiecePoint = new ChessboardPoint(from.getX(), to.getY());
            capturedPiece = getPieceAt(capturedPiecePoint);
            board[capturedPiecePoint.getX()][capturedPiecePoint.getY()] = null;
        }

        setPieceAt(to, movedPiece);
        setPieceAt(from, null);

        Move move = new Move(from, to, movedPiece, capturedPiece, capturedPiecePoint, previousEnPassantTarget, currentColor);
        moveHistory.add(move);
        updateEnPassantTarget(from, to, movedPiece);
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        return move;
    }

    public boolean isPromotionRequired(Move move) {
        ChessPiece movedPiece = move.getMovedPiece();
        if (movedPiece.getType() != PieceType.PAWN) {
            return false;
        }
        int targetRow = move.getTo().getX();
        return targetRow == 0 || targetRow == BOARD_SIZE - 1;
    }

    public void promotePawn(ChessboardPoint point, PieceType targetType) {
        ChessPiece piece = getPieceAt(point);
        if (piece == null || piece.getType() != PieceType.PAWN) {
            return;
        }
        board[point.getX()][point.getY()] = new ChessPiece(targetType, piece.getColor());
        if (!moveHistory.isEmpty()) {
            moveHistory.get(moveHistory.size() - 1).setPromotionResult(targetType);
        }
    }

    public Move undoLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }

        Move move = moveHistory.remove(moveHistory.size() - 1);
        currentColor = move.getPreviousCurrentColor();
        enPassantTarget = copyPoint(move.getPreviousEnPassantTarget());

        board[move.getFrom().getX()][move.getFrom().getY()] = move.getMovedPiece();
        board[move.getTo().getX()][move.getTo().getY()] = null;
        if (move.getCapturedPiece() != null && move.getCapturedPiecePoint() != null) {
            board[move.getCapturedPiecePoint().getX()][move.getCapturedPiecePoint().getY()] = move.getCapturedPiece();
        }
        return move;
    }

    private boolean wouldLeaveKingInCheck(ChessboardPoint from, ChessboardPoint to) {
        ChessPiece movingPiece = getPieceAt(from);
        ChessPiece capturedPiece = getPieceAt(to);
        ChessboardPoint capturedPoint = copyPoint(to);
        boolean enPassantCapture = isEnPassantCapture(from, to, movingPiece);
        if (enPassantCapture) {
            capturedPoint = new ChessboardPoint(from.getX(), to.getY());
            capturedPiece = getPieceAt(capturedPoint);
            board[capturedPoint.getX()][capturedPoint.getY()] = null;
        }
        board[to.getX()][to.getY()] = movingPiece;
        board[from.getX()][from.getY()] = null;

        ChessboardPoint kingPoint = movingPiece.getType() == PieceType.KING ? to : findKing(movingPiece.getColor());
        boolean inCheck = kingPoint == null || isSquareUnderAttack(kingPoint, oppositeColor(movingPiece.getColor()));

        board[from.getX()][from.getY()] = movingPiece;
        board[to.getX()][to.getY()] = capturedPoint.equals(to) ? capturedPiece : null;
        if (enPassantCapture && capturedPoint != null) {
            board[capturedPoint.getX()][capturedPoint.getY()] = capturedPiece;
        }
        return inCheck;
    }

    private boolean canAttack(ChessboardPoint from, ChessboardPoint to, ChessPiece piece) {
        if (from.equals(to)) {
            return false;
        }
        ChessPiece targetPiece = getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return false;
        }

        return switch (piece.getType()) {
            case ROOK -> canRookMove(from, to);
            case BISHOP -> canBishopMove(from, to);
            case QUEEN -> canRookMove(from, to) || canBishopMove(from, to);
            case KNIGHT -> canKnightMove(from, to);
            case KING -> canKingMove(from, to);
            case PAWN -> canPawnAttack(from, to, piece.getColor());
        };
    }

    private boolean canPawnAttack(ChessboardPoint from, ChessboardPoint to, ChessColor color) {
        int direction = color == ChessColor.BLACK ? 1 : -1;
        int dx = to.getX() - from.getX();
        int dy = Math.abs(to.getY() - from.getY());
        return dx == direction && dy == 1;
    }

    private boolean canRookMove(ChessboardPoint from, ChessboardPoint to) {
        return (from.getX() == to.getX() || from.getY() == to.getY()) && isPathClear(from, to);
    }

    private boolean canBishopMove(ChessboardPoint from, ChessboardPoint to) {
        return Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY()) && isPathClear(from, to);
    }

    private boolean canKnightMove(ChessboardPoint from, ChessboardPoint to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx * dy == 2;
    }

    private boolean canKingMove(ChessboardPoint from, ChessboardPoint to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx <= 1 && dy <= 1;
    }

    private boolean canPawnMove(ChessboardPoint from, ChessboardPoint to, ChessColor color) {
        int direction = color == ChessColor.BLACK ? 1 : -1;
        int startRow = color == ChessColor.BLACK ? 1 : 6;
        int dx = to.getX() - from.getX();
        int dy = Math.abs(to.getY() - from.getY());
        ChessPiece targetPiece = getPieceAt(to);

        if (dy == 0) {
            if (targetPiece != null) {
                return false;
            }
            if (dx == direction) {
                return true;
            }
            if (from.getX() == startRow && dx == 2 * direction) {
                int middleRow = from.getX() + direction;
                return getPieceAt(middleRow, from.getY()) == null;
            }
            return false;
        }

        if (dy == 1 && dx == direction && targetPiece != null && targetPiece.getColor() != color) {
            return true;
        }

        return dy == 1 && dx == direction && targetPiece == null && enPassantTarget != null && enPassantTarget.equals(to);
    }

    private boolean isPathClear(ChessboardPoint from, ChessboardPoint to) {
        int rowStep = Integer.compare(to.getX(), from.getX());
        int colStep = Integer.compare(to.getY(), from.getY());
        int row = from.getX() + rowStep;
        int col = from.getY() + colStep;

        while (row != to.getX() || col != to.getY()) {
            if (board[row][col] != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    private boolean isInsideBoard(ChessboardPoint point) {
        return point.getX() >= 0 && point.getX() < BOARD_SIZE && point.getY() >= 0 && point.getY() < BOARD_SIZE;
    }

    private ChessColor oppositeColor(ChessColor color) {
        return color == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    private boolean isEnPassantCapture(ChessboardPoint from, ChessboardPoint to, ChessPiece movingPiece) {
        return movingPiece != null
                && movingPiece.getType() == PieceType.PAWN
                && enPassantTarget != null
                && enPassantTarget.equals(to)
                && getPieceAt(to) == null
                && from.getY() != to.getY();
    }

    private void updateEnPassantTarget(ChessboardPoint from, ChessboardPoint to, ChessPiece movedPiece) {
        enPassantTarget = null;
        if (movedPiece.getType() == PieceType.PAWN && Math.abs(from.getX() - to.getX()) == 2) {
            enPassantTarget = new ChessboardPoint((from.getX() + to.getX()) / 2, from.getY());
        }
    }

    private ChessboardPoint copyPoint(ChessboardPoint point) {
        return point == null ? null : new ChessboardPoint(point.getX(), point.getY());
    }

    private void clearBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
    }

    private void setupInitialPieces() {
        PieceType[] backRank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            board[0][col] = new ChessPiece(backRank[col], ChessColor.BLACK);
            board[1][col] = new ChessPiece(PieceType.PAWN, ChessColor.BLACK);
            board[6][col] = new ChessPiece(PieceType.PAWN, ChessColor.WHITE);
            board[7][col] = new ChessPiece(backRank[col], ChessColor.WHITE);
        }
    }
}
