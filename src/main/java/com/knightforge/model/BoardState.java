package com.knightforge.model;

import com.knightforge.view.ChessboardPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Stores the domain state of a chess game independently from Swing components.
 */
public class BoardState {
    public static final int BOARD_SIZE = 8;

    private final ChessPiece[][] board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
    private final List<Move> moveHistory = new ArrayList<>();
    private ChessColor currentColor = ChessColor.BLACK;
    private ChessboardPoint enPassantTarget;
    private boolean whiteKingSideCastleAvailable;
    private boolean whiteQueenSideCastleAvailable;
    private boolean blackKingSideCastleAvailable;
    private boolean blackQueenSideCastleAvailable;

    public BoardState() {
        reset();
    }

    public void reset() {
        clearBoard();
        setupInitialPieces();
        moveHistory.clear();
        currentColor = ChessColor.BLACK;
        enPassantTarget = null;
        whiteKingSideCastleAvailable = true;
        whiteQueenSideCastleAvailable = true;
        blackKingSideCastleAvailable = true;
        blackQueenSideCastleAvailable = true;
    }

    public void loadPromotionTestPosition() {
        clearBoard();
        moveHistory.clear();
        enPassantTarget = null;
        currentColor = ChessColor.BLACK;
        whiteKingSideCastleAvailable = false;
        whiteQueenSideCastleAvailable = false;
        blackKingSideCastleAvailable = false;
        blackQueenSideCastleAvailable = false;

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

    public boolean isInsufficientMaterial() {
        List<ChessPiece> activePieces = new ArrayList<>();
        List<ChessboardPoint> bishopSquares = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board[row][col];
                if (piece == null) {
                    continue;
                }
                activePieces.add(piece);
                if (piece.getType() == PieceType.BISHOP) {
                    bishopSquares.add(new ChessboardPoint(row, col));
                }
            }
        }

        long nonKingCount = activePieces.stream().filter(piece -> piece.getType() != PieceType.KING).count();
        if (nonKingCount == 0) {
            return true;
        }

        if (nonKingCount == 1) {
            return activePieces.stream()
                    .filter(piece -> piece.getType() != PieceType.KING)
                    .allMatch(piece -> piece.getType() == PieceType.BISHOP || piece.getType() == PieceType.KNIGHT);
        }

        if (nonKingCount == 2) {
            List<ChessPiece> nonKings = activePieces.stream().filter(piece -> piece.getType() != PieceType.KING).toList();
            boolean bishopsOnly = nonKings.stream().allMatch(piece -> piece.getType() == PieceType.BISHOP);
            if (!bishopsOnly || bishopSquares.size() != 2) {
                return false;
            }
            return bishopSquares.stream()
                    .map(point -> (point.getX() + point.getY()) % 2)
                    .distinct()
                    .count() == 1;
        }

        return false;
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
        ChessboardPoint rookFrom = null;
        ChessboardPoint rookTo = null;

        if (isEnPassantCapture(from, to, movedPiece)) {
            capturedPiecePoint = new ChessboardPoint(from.getX(), to.getY());
            capturedPiece = getPieceAt(capturedPiecePoint);
            board[capturedPiecePoint.getX()][capturedPiecePoint.getY()] = null;
        }

        setPieceAt(to, movedPiece);
        setPieceAt(from, null);
        if (isCastlingMove(from, to, movedPiece)) {
            rookFrom = new ChessboardPoint(from.getX(), to.getY() > from.getY() ? 7 : 0);
            rookTo = new ChessboardPoint(from.getX(), to.getY() > from.getY() ? 5 : 3);
            board[rookTo.getX()][rookTo.getY()] = board[rookFrom.getX()][rookFrom.getY()];
            board[rookFrom.getX()][rookFrom.getY()] = null;
        }

        Move move = new Move(
                from,
                to,
                movedPiece,
                capturedPiece,
                capturedPiecePoint,
                previousEnPassantTarget,
                currentColor,
                whiteKingSideCastleAvailable,
                whiteQueenSideCastleAvailable,
                blackKingSideCastleAvailable,
                blackQueenSideCastleAvailable,
                rookFrom,
                rookTo
        );
        moveHistory.add(move);
        updateEnPassantTarget(from, to, movedPiece);
        updateCastleAvailability(from, to, movedPiece, capturedPiece, capturedPiecePoint);
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
        whiteKingSideCastleAvailable = move.isPreviousWhiteKingSideCastleAvailable();
        whiteQueenSideCastleAvailable = move.isPreviousWhiteQueenSideCastleAvailable();
        blackKingSideCastleAvailable = move.isPreviousBlackKingSideCastleAvailable();
        blackQueenSideCastleAvailable = move.isPreviousBlackQueenSideCastleAvailable();

        board[move.getFrom().getX()][move.getFrom().getY()] = move.getMovedPiece();
        board[move.getTo().getX()][move.getTo().getY()] = null;
        if (move.getCapturedPiece() != null && move.getCapturedPiecePoint() != null) {
            board[move.getCapturedPiecePoint().getX()][move.getCapturedPiecePoint().getY()] = move.getCapturedPiece();
        }
        if (move.getRookFrom() != null && move.getRookTo() != null) {
            board[move.getRookFrom().getX()][move.getRookFrom().getY()] = board[move.getRookTo().getX()][move.getRookTo().getY()];
            board[move.getRookTo().getX()][move.getRookTo().getY()] = null;
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
        return (dx <= 1 && dy <= 1) || canCastle(from, to);
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

    private void updateCastleAvailability(
            ChessboardPoint from,
            ChessboardPoint to,
            ChessPiece movedPiece,
            ChessPiece capturedPiece,
            ChessboardPoint capturedPiecePoint
    ) {
        if (movedPiece.getType() == PieceType.KING) {
            if (movedPiece.getColor() == ChessColor.WHITE) {
                whiteKingSideCastleAvailable = false;
                whiteQueenSideCastleAvailable = false;
            } else {
                blackKingSideCastleAvailable = false;
                blackQueenSideCastleAvailable = false;
            }
        }

        if (movedPiece.getType() == PieceType.ROOK) {
            disableCastleByRookSquare(from, movedPiece.getColor());
        }

        if (capturedPiece != null && capturedPiece.getType() == PieceType.ROOK && capturedPiecePoint != null) {
            disableCastleByRookSquare(capturedPiecePoint, capturedPiece.getColor());
        }
    }

    private void disableCastleByRookSquare(ChessboardPoint point, ChessColor color) {
        if (color == ChessColor.WHITE) {
            if (point.getX() == 7 && point.getY() == 0) {
                whiteQueenSideCastleAvailable = false;
            } else if (point.getX() == 7 && point.getY() == 7) {
                whiteKingSideCastleAvailable = false;
            }
        } else if (color == ChessColor.BLACK) {
            if (point.getX() == 0 && point.getY() == 0) {
                blackQueenSideCastleAvailable = false;
            } else if (point.getX() == 0 && point.getY() == 7) {
                blackKingSideCastleAvailable = false;
            }
        }
    }

    private boolean canCastle(ChessboardPoint from, ChessboardPoint to) {
        ChessPiece king = getPieceAt(from);
        if (king == null || king.getType() != PieceType.KING || from.getX() != to.getX()) {
            return false;
        }

        int row = from.getX();
        int colDiff = to.getY() - from.getY();
        if (Math.abs(colDiff) != 2) {
            return false;
        }

        boolean kingSide = colDiff > 0;
        if (!isCastleAvailable(king.getColor(), kingSide)) {
            return false;
        }

        ChessboardPoint rookPoint = new ChessboardPoint(row, kingSide ? 7 : 0);
        ChessPiece rook = getPieceAt(rookPoint);
        if (rook == null || rook.getType() != PieceType.ROOK || rook.getColor() != king.getColor()) {
            return false;
        }

        int startCol = Math.min(from.getY(), rookPoint.getY()) + 1;
        int endCol = Math.max(from.getY(), rookPoint.getY()) - 1;
        for (int col = startCol; col <= endCol; col++) {
            if (col == from.getY()) {
                continue;
            }
            if (board[row][col] != null) {
                return false;
            }
        }

        if (isInCheck(king.getColor())) {
            return false;
        }

        int step = kingSide ? 1 : -1;
        for (int offset = 1; offset <= 2; offset++) {
            ChessboardPoint pathPoint = new ChessboardPoint(row, from.getY() + offset * step);
            if (isSquareUnderAttack(pathPoint, oppositeColor(king.getColor()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isCastlingMove(ChessboardPoint from, ChessboardPoint to, ChessPiece movedPiece) {
        return movedPiece != null
                && movedPiece.getType() == PieceType.KING
                && from.getX() == to.getX()
                && Math.abs(from.getY() - to.getY()) == 2;
    }

    private boolean isCastleAvailable(ChessColor color, boolean kingSide) {
        return switch (color) {
            case WHITE -> kingSide ? whiteKingSideCastleAvailable : whiteQueenSideCastleAvailable;
            case BLACK -> kingSide ? blackKingSideCastleAvailable : blackQueenSideCastleAvailable;
            case NONE -> false;
        };
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
