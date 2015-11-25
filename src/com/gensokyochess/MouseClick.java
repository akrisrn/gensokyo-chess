package com.gensokyochess;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MouseClick extends MouseAdapter {
  private int X;
  private int Y;
  private final String Arrows = "↙↓↘←→↖↑↗";
  private Piece ActivatedPiece;

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    convert2XY(e.getX(), e.getY());
    int index = Tool.convertToIndex(X, Y);
    char aimChar = Tool.getChessboard().charAt(index);
    int arrowIndex = Arrows.indexOf(aimChar);
    if (ActivatedPiece != null) {
      if (arrowIndex != -1){
        Tool.getFrame().sendCmd(String.valueOf(ActivatedPiece.getCode()) + (5 * arrowIndex / 4 + 1));
      } else if (aimChar == ActivatedPiece.getCode()) {
        Tool.getFrame().sendCmd(String.valueOf(ActivatedPiece.getCode()) + 5);
      } else if (aimChar == '*') {
        if (Tool.findSpecialPiece(index, '*')) {
          Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
        }
      } else if (aimChar == '|') {
        if (Tool.findSpecialPiece(index, '|')) {
          Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
        }
      } else if (aimChar != ' '){
        Tool.getFrame().sendCmd(ActivatedPiece.getCode() + "+" + aimChar);
      }
      removeArrows();
    } else {
      Piece piece;
      if (aimChar != ' ') {
        piece = Tool.findPiece(aimChar);
        if (piece != null && X == piece.getX() && Y == piece.getY()) {
          Tool.print(piece.toString());
          if (piece.getCamp().equals(Tool.getCurCamp())) {
            drawArrows(piece);
            Tool.getFrame().setSpellButton1Text(ActivatedPiece.getSpellCode());
          }
        }
      }
    }
  }

  private void removeArrows() {
    ActivatedPiece = null;

    for (int i = 0; i < 8; i++) {
      int index = Tool.getChessboard().indexOf(String.valueOf(Arrows.charAt(i)));
      if (index != -1) {
        if (index == Tool.convertToIndex(2, 5) || index == Tool.convertToIndex(5, 5) ||
                index == Tool.convertToIndex(8, 5)) {
          Tool.getChessboard().setCharAt(index, '|');
        } else if (index == Tool.convertToIndex(1, 5) || index == Tool.convertToIndex(3, 5) ||
                index == Tool.convertToIndex(4, 5) || index == Tool.convertToIndex(6, 5) ||
                index == Tool.convertToIndex(7, 5) || index == Tool.convertToIndex(9, 5)) {
          Tool.getChessboard().setCharAt(index, '*');
        } else {
          Tool.getChessboard().setCharAt(index, ' ');
        }
      }
    }
    Tool.updateChessboard();
  }

  private void drawArrows(Piece piece) {
    ActivatedPiece = piece;

    int x = piece.getX();
    int y = piece.getY();

    ArrayList<Integer> directions = markDirection(x, y);
    int k = 0;
    for (int j = y - 1; j <= y + 1; j++) {
      for (int i = x - 1; i <= x + 1; i++) {
        if (!(i == x && j == y)) {
          for (int direction : directions) {
            if (Tool.determineDirection(i, j, x, y) == direction) {
              Tool.getChessboard().setCharAt(Tool.convertToIndex(i, j), Arrows.charAt(k));
            }
          }
          k++;
        }
      }
    }
    Tool.updateChessboard();
  }

  private ArrayList<Integer> markDirection(int x, int y) {
    ArrayList<Integer> directions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (!(i == x && j == y)) {
          int index = Tool.convertToIndex(i, j);
          if (index != 0) {
            int direction = Tool.determineDirection(i, j, x, y);
            char aimChar = Tool.getChessboard().charAt(index);
            if (aimChar == ' ') {
              directions.add(direction);
            } else if (aimChar == '*' && !Tool.findSpecialPiece(index, '*')) {
              directions.add(direction);
            } else if (aimChar == '|' && !Tool.findSpecialPiece(index, '|')) {
              directions.add(direction);
            }
          }
        }
      }
    }
    return directions;
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
