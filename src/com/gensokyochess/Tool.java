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

      if (aimChar == '*') {
        if (findSpecialPiece(index, '*')) {
          pieces.add(findPiece(aimChar));
        }
      } else if (aimChar == '|') {
        if (findSpecialPiece(index, '|')) {
          pieces.add(findPiece(aimChar));
        }
      } else if (aimChar != ' ') {
        pieces.add(findPiece(aimChar));
      }
    }
    return pieces;
  }

  public static boolean findSpecialPiece(int index, char aimChar) {
    Piece piece = findPiece(aimChar);
    return piece != null && (index == convertToIndex(piece.getX(), piece.getY()));
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
}
