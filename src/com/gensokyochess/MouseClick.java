package com.gensokyochess;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseClick extends MouseAdapter {
  private int X;
  private int Y;
  private Piece ActivatedPiece;

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    convert2XY(e.getX(), e.getY());
    int index = Tool.convertToIndex(X, Y);
    char aimChar = Tool.getChessboard().charAt(index);
    if (ActivatedPiece != null) {
      int move = Tool.getMove(aimChar);
      if (move != 0) {
        Tool.getFrame().sendCmd(String.valueOf(ActivatedPiece.getCode()) + move);
      } else if (aimChar == ActivatedPiece.getCode()) {
        Tool.getFrame().sendCmd(String.valueOf(ActivatedPiece.getCode()) + 5);
      } else if (aimChar == '*') {
        if (Tool.findSpecialPiece(index, '*') != null) {
          Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
        }
      } else if (aimChar == '|') {
        if (Tool.findSpecialPiece(index, '|') != null) {
          Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
        }
      } else if (aimChar == ' '){
        Tool.getFrame().sendCmd(" ");
      } else {
        Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
      }
      ActivatedPiece = null;
      Tool.removeArrows();
    } else {
      Piece piece;
      if (aimChar != ' ') {
        piece = Tool.findPiece(aimChar);
        if (piece != null && X == piece.getX() && Y == piece.getY()) {
          Tool.print(piece.toString());
          if (piece.getCamp().equals(Tool.getCurCamp())) {
            ActivatedPiece = piece;
            Tool.drawArrows(piece, true);
            Tool.getFrame().setSpellButton1Text(ActivatedPiece.getSpellCode());
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
