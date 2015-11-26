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
      int index = Tool.convert2Index(X, Y);
      char aimChar = Tool.getChessboard().charAt(index);
      if (Tool.getActivatedPiece() != null) {
        int move = Tool.getMove(aimChar);
        if (move != 0) {
          Tool.getGuiFrame().sendCmd(Tool.getActivatedPiece().getCode() + "" + move);
        } else if (aimChar == Tool.getActivatedPiece().getCode()) {
          Tool.getGuiFrame().sendCmd(Tool.getActivatedPiece().getCode() + "5");
        } else if (aimChar == '*' && Tool.findSpecialPiece(index, '*') != null) {
          Tool.getGuiFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else if (aimChar == '|' && Tool.findSpecialPiece(index, '|') != null) {
          Tool.getGuiFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
          Tool.getGuiFrame().sendCmd(Tool.getActivatedPiece().getCode() + "+" + aimChar);
        } else {
          Tool.getGuiFrame().sendCmd(" ");
        }
        Tool.setActivatedPiece(null);
        Tool.eraseArrows();
      } else {
        Piece piece;
        if (aimChar != ' ') {
          piece = Tool.findPiece(aimChar);
          if (piece != null && X == piece.getX() && Y == piece.getY()) {
            Tool.print(piece.toString());
            if (piece.getCamp() == Tool.getCurrentCamp()) {
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
