package com.knightforge.model;

import com.knightforge.view.ChessboardPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int halfmoveClock;
    private final Map<String, Integer> positionCounts = new HashMap<>();

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
        halfmoveClock = 0;
        resetPositionTracking();
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
        halfmoveClock = 0;
        resetPositionTracking();

        board[0][4] = new ChessPiece(PieceType.KING, ChessColor.BLACK);
        board[7][4] = new ChessPiece(PieceType.KING, ChessColor.WHITE);
        board[6][0] = new ChessPiece(PieceType.PAWN, ChessColor.BLACK);
    }

    public void loadCheckmateTestPosition() {
        reset();
        moveHistory.clear();
        enPassantTarget = null;
        currentColor = ChessColor.BLACK;
        halfmoveClock = 0;
        resetPositionTracking();

        board[6][5] = null;
        board[5][5] = new ChessPiece(PieceType.PAWN, ChessColor.WHITE);
        board[6][6] = null;
        board[4][6] = new ChessPiece(PieceType.PAWN, ChessColor.WHITE);

        board[1][4] = null;
        board[3][4] = new ChessPiece(PieceType.PAWN, ChessColor.BLACK);
    }

    public void loadDrawTestPosition() {
        clearBoard();
        moveHistory.clear();
        enPassantTarget = null;
        currentColor = ChessColor.BLACK;
        whiteKingSideCastleAvailable = false;
        whiteQueenSideCastleAvailable = false;
        blackKingSideCastleAvailable = false;
        blackQueenSideCastleAvailable = false;
        halfmoveClock = 0;

        board[0][4] = new ChessPiece(PieceType.KING, ChessColor.BLACK);
        board[7][4] = new ChessPiece(PieceType.KING, ChessColor.WHITE);
        resetPositionTracking();
    }

    public List<String> serialize() {
        List<String> lines = new ArrayList<>();
        lines.add("CURRENT:" + currentColor.name());
        lines.add("CASTLE:" + castleRightsString());
        lines.add("EN_PASSANT:" + pointToString(enPassantTarget));
        lines.add("HALFMOVE:" + halfmoveClock);
        for (int row = 0; row < BOARD_SIZE; row++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (col > 0) {
                    rowBuilder.append(' ');
                }
                rowBuilder.append(pieceToCode(board[row][col]));
            }
            lines.add(rowBuilder.toString());
        }
        return lines;
    }

    public void loadFromLines(List<String> lines) {
        if (lines == null || lines.size() < 12) {
            throw new IllegalArgumentException("Save file is incomplete.");
        }

        clearBoard();
        moveHistory.clear();
        currentColor = ChessColor.valueOf(readValue(lines.get(0), "CURRENT"));
        applyCastleRights(readValue(lines.get(1), "CASTLE"));
        enPassantTarget = parsePoint(readValue(lines.get(2), "EN_PASSANT"));
        halfmoveClock = Integer.parseInt(readValue(lines.get(3), "HALFMOVE"));

        for (int row = 0; row < BOARD_SIZE; row++) {
            String[] tokens = lines.get(row + 4).trim().split("\\s+");
            if (tokens.length != BOARD_SIZE) {
                throw new IllegalArgumentException("Invalid board row at line " + (row + 5));
            }
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = codeToPiece(tokens[col]);
            }
        }

        resetPositionTracking();
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

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public boolean isThreefoldRepetition() {
        return positionCounts.getOrDefault(buildPositionKey(), 0) >= 3;
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
                halfmoveClock,
                rookFrom,
                rookTo
        );
        moveHistory.add(move);
        updateEnPassantTarget(from, to, movedPiece);
        updateCastleAvailability(from, to, movedPiece, capturedPiece, capturedPiecePoint);
        updateHalfmoveClock(movedPiece, capturedPiece);
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        recordCurrentPosition();
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
        halfmoveClock = move.getPreviousHalfmoveClock();

        board[move.getFrom().getX()][move.getFrom().getY()] = move.getMovedPiece();
        board[move.getTo().getX()][move.getTo().getY()] = null;
        if (move.getCapturedPiece() != null && move.getCapturedPiecePoint() != null) {
            board[move.getCapturedPiecePoint().getX()][move.getCapturedPiecePoint().getY()] = move.getCapturedPiece();
        }
        if (move.getRookFrom() != null && move.getRookTo() != null) {
            board[move.getRookFrom().getX()][move.getRookFrom().getY()] = board[move.getRookTo().getX()][move.getRookTo().getY()];
            board[move.getRookTo().getX()][move.getRookTo().getY()] = null;
        }
        rebuildPositionTracking();
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

    private void updateHalfmoveClock(ChessPiece movedPiece, ChessPiece capturedPiece) {
        if (movedPiece.getType() == PieceType.PAWN || capturedPiece != null) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
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

    private void resetPositionTracking() {
        positionCounts.clear();
        recordCurrentPosition();
    }

    private void rebuildPositionTracking() {
        positionCounts.clear();
        recordCurrentPosition();

        ChessColor replayColor = ChessColor.BLACK;
        ChessPiece[][] replayBoard = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                replayBoard[row][col] = null;
            }
        }
        initializeReplayBoard(replayBoard);
        ChessboardPoint replayEnPassant = null;
        boolean replayWhiteKingSide = true;
        boolean replayWhiteQueenSide = true;
        boolean replayBlackKingSide = true;
        boolean replayBlackQueenSide = true;

        for (Move move : moveHistory) {
            ChessPiece movedPiece = replayBoard[move.getFrom().getX()][move.getFrom().getY()];
            ChessPiece capturedPiece = replayBoard[move.getTo().getX()][move.getTo().getY()];
            if (movedPiece != null && movedPiece.getType() == PieceType.PAWN && replayEnPassant != null
                    && move.getTo().equals(replayEnPassant) && replayBoard[move.getTo().getX()][move.getTo().getY()] == null
                    && move.getFrom().getY() != move.getTo().getY()) {
                replayBoard[move.getFrom().getX()][move.getTo().getY()] = null;
                capturedPiece = move.getCapturedPiece();
            }

            replayBoard[move.getTo().getX()][move.getTo().getY()] = movedPiece;
            replayBoard[move.getFrom().getX()][move.getFrom().getY()] = null;
            if (move.getRookFrom() != null && move.getRookTo() != null) {
                replayBoard[move.getRookTo().getX()][move.getRookTo().getY()] = replayBoard[move.getRookFrom().getX()][move.getRookFrom().getY()];
                replayBoard[move.getRookFrom().getX()][move.getRookFrom().getY()] = null;
            }
            if (move.getPromotionResult() != null) {
                replayBoard[move.getTo().getX()][move.getTo().getY()] = new ChessPiece(move.getPromotionResult(), movedPiece.getColor());
            }

            replayEnPassant = null;
            if (movedPiece != null && movedPiece.getType() == PieceType.PAWN
                    && Math.abs(move.getFrom().getX() - move.getTo().getX()) == 2) {
                replayEnPassant = new ChessboardPoint((move.getFrom().getX() + move.getTo().getX()) / 2, move.getFrom().getY());
            }

            if (movedPiece != null) {
                if (movedPiece.getType() == PieceType.KING) {
                    if (movedPiece.getColor() == ChessColor.WHITE) {
                        replayWhiteKingSide = false;
                        replayWhiteQueenSide = false;
                    } else {
                        replayBlackKingSide = false;
                        replayBlackQueenSide = false;
                    }
                }
                if (movedPiece.getType() == PieceType.ROOK) {
                    if (movedPiece.getColor() == ChessColor.WHITE) {
                        if (move.getFrom().getX() == 7 && move.getFrom().getY() == 0) {
                            replayWhiteQueenSide = false;
                        } else if (move.getFrom().getX() == 7 && move.getFrom().getY() == 7) {
                            replayWhiteKingSide = false;
                        }
                    } else {
                        if (move.getFrom().getX() == 0 && move.getFrom().getY() == 0) {
                            replayBlackQueenSide = false;
                        } else if (move.getFrom().getX() == 0 && move.getFrom().getY() == 7) {
                            replayBlackKingSide = false;
                        }
                    }
                }
            }
            if (capturedPiece != null && move.getCapturedPiecePoint() != null && capturedPiece.getType() == PieceType.ROOK) {
                if (capturedPiece.getColor() == ChessColor.WHITE) {
                    if (move.getCapturedPiecePoint().getX() == 7 && move.getCapturedPiecePoint().getY() == 0) {
                        replayWhiteQueenSide = false;
                    } else if (move.getCapturedPiecePoint().getX() == 7 && move.getCapturedPiecePoint().getY() == 7) {
                        replayWhiteKingSide = false;
                    }
                } else {
                    if (move.getCapturedPiecePoint().getX() == 0 && move.getCapturedPiecePoint().getY() == 0) {
                        replayBlackQueenSide = false;
                    } else if (move.getCapturedPiecePoint().getX() == 0 && move.getCapturedPiecePoint().getY() == 7) {
                        replayBlackKingSide = false;
                    }
                }
            }

            replayColor = replayColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
            positionCounts.merge(buildPositionKey(replayBoard, replayColor, replayEnPassant,
                    replayWhiteKingSide, replayWhiteQueenSide, replayBlackKingSide, replayBlackQueenSide), 1, Integer::sum);
        }
    }

    private void initializeReplayBoard(ChessPiece[][] replayBoard) {
        PieceType[] backRank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            replayBoard[0][col] = new ChessPiece(backRank[col], ChessColor.BLACK);
            replayBoard[1][col] = new ChessPiece(PieceType.PAWN, ChessColor.BLACK);
            replayBoard[6][col] = new ChessPiece(PieceType.PAWN, ChessColor.WHITE);
            replayBoard[7][col] = new ChessPiece(backRank[col], ChessColor.WHITE);
        }
    }

    private void recordCurrentPosition() {
        positionCounts.merge(buildPositionKey(), 1, Integer::sum);
    }

    private String buildPositionKey() {
        return buildPositionKey(board, currentColor, enPassantTarget,
                whiteKingSideCastleAvailable, whiteQueenSideCastleAvailable,
                blackKingSideCastleAvailable, blackQueenSideCastleAvailable);
    }

    private String buildPositionKey(
            ChessPiece[][] boardState,
            ChessColor colorToMove,
            ChessboardPoint enPassant,
            boolean whiteKingSide,
            boolean whiteQueenSide,
            boolean blackKingSide,
            boolean blackQueenSide
    ) {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = boardState[row][col];
                if (piece == null) {
                    builder.append('.');
                } else {
                    char colorPrefix = piece.getColor() == ChessColor.WHITE ? 'w' : 'b';
                    char typeCode = switch (piece.getType()) {
                        case KING -> 'k';
                        case QUEEN -> 'q';
                        case ROOK -> 'r';
                        case BISHOP -> 'b';
                        case KNIGHT -> 'n';
                        case PAWN -> 'p';
                    };
                    builder.append(colorPrefix).append(typeCode);
                }
                builder.append(',');
            }
        }
        builder.append('|').append(colorToMove.name());
        builder.append('|').append(whiteKingSide ? 'K' : '-').append(whiteQueenSide ? 'Q' : '-')
                .append(blackKingSide ? 'k' : '-').append(blackQueenSide ? 'q' : '-');
        builder.append('|');
        if (enPassant == null) {
            builder.append('-');
        } else {
            builder.append(enPassant.getX()).append(':').append(enPassant.getY());
        }
        return builder.toString();
    }

    private String castleRightsString() {
        StringBuilder builder = new StringBuilder();
        if (whiteKingSideCastleAvailable) {
            builder.append('K');
        }
        if (whiteQueenSideCastleAvailable) {
            builder.append('Q');
        }
        if (blackKingSideCastleAvailable) {
            builder.append('k');
        }
        if (blackQueenSideCastleAvailable) {
            builder.append('q');
        }
        return builder.isEmpty() ? "-" : builder.toString();
    }

    private void applyCastleRights(String rights) {
        whiteKingSideCastleAvailable = rights.contains("K");
        whiteQueenSideCastleAvailable = rights.contains("Q");
        blackKingSideCastleAvailable = rights.contains("k");
        blackQueenSideCastleAvailable = rights.contains("q");
    }

    private String pointToString(ChessboardPoint point) {
        return point == null ? "-" : point.getX() + "," + point.getY();
    }

    private ChessboardPoint parsePoint(String value) {
        if ("-".equals(value)) {
            return null;
        }
        String[] parts = value.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid point: " + value);
        }
        return new ChessboardPoint(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private String pieceToCode(ChessPiece piece) {
        if (piece == null) {
            return "--";
        }
        char colorCode = piece.getColor() == ChessColor.WHITE ? 'w' : 'b';
        char typeCode = switch (piece.getType()) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
        return "" + colorCode + typeCode;
    }

    private ChessPiece codeToPiece(String code) {
        if ("--".equals(code)) {
            return null;
        }
        if (code.length() != 2) {
            throw new IllegalArgumentException("Invalid piece code: " + code);
        }
        ChessColor color = switch (code.charAt(0)) {
            case 'w' -> ChessColor.WHITE;
            case 'b' -> ChessColor.BLACK;
            default -> throw new IllegalArgumentException("Invalid piece color code: " + code);
        };
        PieceType type = switch (code.charAt(1)) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'P' -> PieceType.PAWN;
            default -> throw new IllegalArgumentException("Invalid piece type code: " + code);
        };
        return new ChessPiece(type, color);
    }

    private String readValue(String line, String key) {
        String prefix = key + ":";
        if (!line.startsWith(prefix)) {
            throw new IllegalArgumentException("Expected " + key + " line.");
        }
        return line.substring(prefix.length()).trim();
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
