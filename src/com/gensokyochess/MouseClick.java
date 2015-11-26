package com.gensokyochess;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseClick extends MouseAdapter {
  private int X;
  private int Y;

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    if (!Tool.isLock()) {
      convert2XY(e.getX(), e.getY());
      int index = Tool.convertToIndex(X, Y);
      char aimChar = Tool.getChessboard().charAt(index);
      if (Tool.getActivatedPiece() != null) {
        int move = Tool.getMove(aimChar);
        if (move != 0) {
          Tool.getFrame().sendCmd(Tool.getActivatedPiece().getCode() + "" + move);
        } else if (aimChar == Tool.getActivatedPiece().getCode()) {
          Tool.getFrame().sendCmd(Tool.getActivatedPiece().getCode() + "5");
        } else if (aimChar == '*' && Tool.findSpecialPiece(index, '*') != null) {
          Tool.getFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else if (aimChar == '|' && Tool.findSpecialPiece(index, '|') != null) {
          Tool.getFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
          Tool.getFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else {
          Tool.getFrame().sendCmd(" ");
        }
        Tool.setActivatedPiece(null);
        Tool.removeArrows();
      } else {
        Piece piece;
        if (aimChar != ' ') {
          piece = Tool.findPiece(aimChar);
          if (piece != null && X == piece.getX() && Y == piece.getY()) {
            Tool.print(piece.toString());
            if (piece.getCamp().equals(Tool.getCurCamp())) {
              Tool.drawArrows(piece, true);
              Tool.setActivatedPiece(piece);
            }
          }
        }
      }
    }
  }

  private void convert2XY(int x, int y) {
    if (x <= 6 || x >= 366 || y <= 12 || y >= 390) {
      X = 0;
      Y = 0;
    } else {
      X = (x - 6) / 40 + 1;
      Y = 9 - (y - 12) / 42;
    }
  }
}
