//package com.knightforge.view.ViewComponents.ChessboardComponents;
//
//import com.knightforge.controller.ClickController;
//import com.knightforge.model.ChessboardPosition;
//
//import java.awt.*;
//
///**
// * Represents an empty square on the board.
// */
//public class EmptySlotComponent extends ChessComponent {
//
//    public EmptySlotComponent(ChessboardPosition chessboardPosition, Point location, ClickController listener, int size) {
//        super(chessboardPosition, location, listener, size);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (isMoveHint()) {
//            g.setColor(new Color(46, 204, 113, 170));
//            g.fillOval(getWidth() / 3, getHeight() / 3, getWidth() / 3, getHeight() / 3);
//        }
//        if (isSelected()) {
//            g.setColor(Color.RED);
//            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
//        }
//    }
//}
