package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPieceFactory;
import com.knightforge.model.ChessPieces.ChessPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chessboard {
    public static final int BOARD_SIZE = 8;
    public static final ChessPieceFactory chessPieceFactory = new ChessPieceFactory();
    private final ChessPiece[][] board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];

    public Chessboard() {
        setupInitialPieces();
    }

    public int getLength() { return board.length; }
    public int getHeight() { return board[0].length; }

    public void capture(ChessboardPosition capturePosition) {
        board[capturePosition.getX()][capturePosition.getY()] = null;
    }

    public List<ChessboardPosition> getLocationsOfPiece(PieceType type, ChessColor color) {
        List<ChessboardPosition> positions = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null && board[row][col].getColor() == color && board[row][col].getType().equals(type.name())){
                    positions.add(new ChessboardPosition(row, col));
                }
            }
        }
        return positions;
    }

    public ChessPiece movePiece(ChessboardPosition from, ChessboardPosition to){
        // TODO: ensure En passant, and castling functionality are properly handled here.
        ChessPiece capturedPiece = getPieceAtPosition(to);
        board[to.getX()][to.getY()] = board[from.getX()][from.getY()];
        board[from.getX()][from.getY()] = null;
        return capturedPiece;
    }

    public void reverseMove(ChessboardPosition currentLocation, ChessboardPosition reversedLocation, ChessPiece movedPiece, ChessPiece capturedPiece){
        // Move piece back to previous location
        board[reversedLocation.getX()][reversedLocation.getY()] = movedPiece;
        // Restore captured piece
        board[currentLocation.getX()][currentLocation.getY()] = capturedPiece;
    }

    public ChessboardPosition getKingLocation(ChessColor color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null && board[row][col].getColor() == color && Objects.equals(board[row][col].getType(), "KING")){
                    return new ChessboardPosition(row, col);
                }
            }
        }
        return null;
    }

    public ChessPiece getPieceAtPosition(ChessboardPosition position){
        return board[position.getX()][position.getY()];
    }
    public ChessPiece getPieceAtPosition(int row, int height){
        return board[row][height];
    }

    private void setupInitialPieces() {
        PieceType[] backRank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            board[0][col] = chessPieceFactory.createPiece(backRank[col], ChessColor.BLACK);
            board[1][col] = chessPieceFactory.createPiece(PieceType.PAWN, ChessColor.BLACK);
            board[6][col] = chessPieceFactory.createPiece(PieceType.PAWN, ChessColor.WHITE);
            board[7][col] = chessPieceFactory.createPiece(backRank[col], ChessColor.WHITE);
        }
    }

    public void loadFromLines(List<String> lines) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            String[] tokens = lines.get(row).trim().split("\\s+");
            if (tokens.length != BOARD_SIZE) {
                throw new IllegalArgumentException("Invalid board row at line " + (row));
            }
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = codeToPiece(tokens[col]);
            }
        }
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
        return chessPieceFactory.createPiece(type, color);
    }

    public ChessPiece[][] getBoard() {
        return board;
    }
}
