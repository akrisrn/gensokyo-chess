package com.gensokyochess;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 鼠标点击事件类
 */
public class MouseClick extends MouseAdapter {
  private int X;
  private int Y;

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    if (!Tool.isLock()) {
      convertXY2GridXY(e.getX(), e.getY());
      int index = Tool.convertXY2Index(X, Y);
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
            if (piece.getCamp()) {
              Tool.drawArrows(piece, true);
              Tool.setActivatedPiece(piece);
            }
          }
        }
      }
    }
  }

  /**
   * 将鼠标点击时的坐标转换为棋盘上的格子坐标
   *
   * @param x 鼠标点击时的 x 坐标
   * @param y 鼠标点击时的 y 坐标
   */
  private void convertXY2GridXY(int x, int y) {
    if (x <= 6 || x >= 366 || y <= 12 || y >= 390) {
      X = 0;
      Y = 0;
    } else {
      X = (x - 6) / 40 + 1;
      Y = 9 - (y - 12) / 42;
    }
  }
}
