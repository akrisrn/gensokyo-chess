package com.gensokyochess;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Tool {
  private static GuiFrame Frame;
  private static boolean UseGui = false;
  private static StringBuffer Chessboard;
  private static ArrayList<Piece> Pieces;
  private static boolean CurCampIsRed = true;
  private final static String Arrows = "↙↓↘←→↖↑↗";

  public static void setCurCampIsRed(boolean curCampIsRed) {
    CurCampIsRed = curCampIsRed;
  }

  public static String getCurCamp() {
    if (CurCampIsRed) {
      return "red";
    } else {
      return "black";
    }
  }

  public static GuiFrame getFrame() {
    return Frame;
  }

  public static StringBuffer getChessboard() {
    return Chessboard;
  }

  public static void setChessboard(StringBuffer chessboard) {
    Chessboard = chessboard;
  }

  public static void setFrame(GuiFrame frame) {
    Frame = frame;
    UseGui = true;
  }

  public static void setPiece(ArrayList<Piece> pieces) {
    Pieces = pieces;
  }

  public static void print(String msg) {
    print(msg, true, true, 0);
  }

  public static void print(String msg, int delay) {
    print(msg, true, true, delay);
  }

  public static void print(String msg, boolean isALine) {
    print(msg, isALine, true, 0);
  }

  public static void print(String msg, boolean isALine, boolean showMsg) {
    print(msg, isALine, showMsg, 0);
  }

  public static void print(String msg, boolean notShowOnGui, int delay) {
    if (!UseGui) {
      print(msg);
    } else if (!notShowOnGui) {
      print(msg, true, false, delay);
    }
  }

  public static void print(String msg, boolean isALine, boolean showMsg, int delay) {
    if (!UseGui) {
      if (isALine && showMsg) {
        System.out.println(msg);
      } else if (showMsg) {
        System.out.print(msg);
      }
    } else {
      if (showMsg) {
        Frame.appendLog(msg, isALine);
      }
    }
    if (delay != 0) {
      try {
        Thread.sleep(1200);
      } catch (InterruptedException ignored) {
      }
    }
  }

  public static String input() {
    if (!UseGui) {
      Scanner in = new Scanner(System.in);
      return in.nextLine();
    } else {
      String input;
      while (true) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        input = uncheckedInput();
        if (input != null) {
          break;
        }
      }
      return input;
    }
  }

  private static String uncheckedInput() {
    try {
      Scanner in = Frame.getScanner();
      return in.nextLine();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  public static ArrayList<Piece> findPieces(boolean isXAxis, int commonValue, int startXOrY, int overXOrY) {
    ArrayList<Piece> pieces = new ArrayList<>();
    int reversal = 1;
    if (startXOrY > overXOrY) {
      reversal = -1;
    }

    char aimChar;
    int index;
    for (int i = startXOrY + reversal; i * reversal < overXOrY * reversal; i += reversal) {
      if (isXAxis) {
        index = convertToIndex(i, commonValue);
        aimChar = Chessboard.charAt(index);
      } else {
        index = convertToIndex(commonValue, i);
        aimChar = Chessboard.charAt(index);
      }

      Piece piece;
      if (aimChar == '*') {
        piece = findSpecialPiece(index, '*');
      } else if (aimChar == '|') {
        piece = findSpecialPiece(index, '|');
      } else {
        piece = findPiece(aimChar);
      }
      if (piece != null) {
        pieces.add(piece);
      }
    }
    return pieces;
  }

  public static Piece findSpecialPiece(int index, char aimChar) {
    Piece piece = findPiece(aimChar);
    if (piece != null && (index == convertToIndex(piece.getX(), piece.getY()))) {
      return piece;
    }
    else {
      return null;
    }
  }

  public static Piece findPiece(char code) {
    for (Piece piece : Pieces) {
      if (piece.getCode() == code) {
        return piece;
      }
    }
    return null;
  }

  public static Piece findPiece(String camp, char code) {
    for (Piece piece : Pieces) {
      if (piece.getCamp().equals(camp)) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }

  public static int convertToIndex(int x, int y) {
    if (x < 1 || y < 1 || x > 9 || y > 9) {
      return 0;
    } else {
      return 648 - (y - 1) * 76 + (x - 1) * 4;
    }
  }

  public static int determineDirection(int aimX, int aimY, int curX, int curY) {
    /*
       789
       456
       123
     */
    if (aimX > curX) {
      if (aimY > curY) {
        return 9;
      } else if (aimY < curY) {
        return 3;
      } else {
        return 6;
      }
    } else if (aimX < curX) {
      if (aimY > curY) {
        return 7;
      } else if (aimY < curY) {
        return 1;
      } else {
        return 4;
      }
    } else {
      if (aimY > curY) {
        return 8;
      } else if (aimY < curY) {
        return 2;
      } else {
        return 5;
      }
    }
  }

  public static void updateChessboard() {
    if (!UseGui) {
      System.out.println(Chessboard);
    } else {
      Frame.updateChessboard(String.valueOf(Chessboard));
    }
  }

  public static void updateState(String msg, boolean isRound) {
    if (!UseGui) {
      System.out.println(msg);
    } else {
      if (isRound) {
        Frame.updateRoundLabel(msg);
      } else {
        Frame.updateActionLabel(msg);
      }
    }
  }

  public static void drawArrows(Piece piece, boolean isAllArrows) {
    int x = piece.getX();
    int y = piece.getY();

    ArrayList<Integer> directions = markDirection(x, y);
    int k = 0;
    for (int j = y - 1; j <= y + 1; j++) {
      for (int i = x - 1; i <= x + 1; i++) {
        if (!(i == x && j == y)) {
          for (int direction : directions) {
            if (Tool.determineDirection(i, j, x, y) == direction) {
              if (!isAllArrows) {
                if (k == 1 || k == 3 || k == 4 || k == 6) {
                  Tool.getChessboard().setCharAt(Tool.convertToIndex(i, j), Arrows.charAt(k));
                }
              } else {
                Tool.getChessboard().setCharAt(Tool.convertToIndex(i, j), Arrows.charAt(k));
              }
            }
          }
          k++;
        }
      }
    }
    Tool.updateChessboard();
  }

  private static ArrayList<Integer> markDirection(int x, int y) {
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
            } else if (aimChar == '*' && Tool.findSpecialPiece(index, '*') == null) {
              directions.add(direction);
            } else if (aimChar == '|' && Tool.findSpecialPiece(index, '|') == null) {
              directions.add(direction);
            }
          }
        }
      }
    }
    return directions;
  }

  public static void removeArrows() {
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

  public static int getMove(char aimChar) {
    int arrowIndex = Arrows.indexOf(aimChar);
    if (arrowIndex != -1) {
      return 5 * arrowIndex / 4 + 1;
    } else {
      return 0;
    }
  }
}
