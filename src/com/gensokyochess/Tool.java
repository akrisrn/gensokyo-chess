package com.gensokyochess;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Tool {
  private static GuiFrame GuiFrame;
  private static boolean UseGui = false;
  private static StringBuffer Chessboard;
  private static ArrayList<Piece> Pieces;
  private static boolean CurrentCampIsRed = true;
  private final static String Arrows = "↙↓↘←→↖↑↗";
  private static Piece ActivatedPiece;
  private static int RoundCount = 0;
  private static boolean Lock = false;

  public static void locked() {
    Lock = true;
  }

  public static void unlock() {
    Lock = false;
  }

  public static boolean isLock() {
    return Lock;
  }

  public static int getRoundCount() {
    return RoundCount;
  }

  public static void CountRound() {
    RoundCount++;
  }

  public static void setActivatedPiece(Piece piece) {
    ActivatedPiece = piece;
    if (piece == null) {
      GuiFrame.setSpellButtonText(0, "无");
    } else {
      GuiFrame.setSpellButtonText(1, piece.getSpellCode(1));
      if (piece.getTotalSpellNumber() > 1) {
        GuiFrame.setSpellButtonText(2, piece.getSpellCode(2));
      }
    }
  }

  public static Piece getActivatedPiece() {
    return ActivatedPiece;
  }

  public static void setCurrentCampIsRed(boolean currentCampIsRed) {
    CurrentCampIsRed = currentCampIsRed;
  }

  public static boolean getCurrentCamp() {
    return CurrentCampIsRed;
  }

  public static GuiFrame getGuiFrame() {
    return GuiFrame;
  }

  public static StringBuffer getChessboard() {
    return Chessboard;
  }

  public static void setChessboard(StringBuffer chessboard) {
    Chessboard = chessboard;
  }

  public static void setGuiFrame(GuiFrame guiFrame) {
    GuiFrame = guiFrame;
    UseGui = true;
  }

  public static void setPieces(ArrayList<Piece> pieces) {
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
        GuiFrame.appendLog(msg, isALine);
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
      Scanner in = GuiFrame.getScanner();
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
        index = convert2Index(i, commonValue);
        aimChar = Chessboard.charAt(index);
      } else {
        index = convert2Index(commonValue, i);
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
    if (piece != null && (index == convert2Index(piece.getX(), piece.getY()))) {
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

  public static Piece findPiece(boolean camp, char code) {
    for (Piece piece : Pieces) {
      if (piece.getCamp() == camp) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }

  public static int convert2Index(int x, int y) {
    if (x < 1 || y < 1 || x > 9 || y > 9) {
      return 0;
    } else {
      return 648 - (y - 1) * 76 + (x - 1) * 4;
    }
  }

  public static int determineDirection(int aimX, int aimY, int curX, int curY) {
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
      GuiFrame.updateChessboard(String.valueOf(Chessboard));
    }
  }

  public static void updateRoundMsg() {
    String msg = "第 " + RoundCount + " 回合";
    if (!UseGui) {
      System.out.println(msg);
    } else {
      GuiFrame.updateRoundLabel(msg);
    }
  }

  public static void updateActionMsg(int count, int over) {
    String msg;
    if (count == 0) {
      switch (over) {
        case 1:
          msg = "红方胜利";
          break;
        case -1:
          msg = "黑方胜利";
          break;
        default:
          msg = "平局";
          break;
      }
    } else {
      String camp;
      if (CurrentCampIsRed) {
        camp = "红方";
      } else {
        camp = "黑方";
      }
      msg = camp + "第 " + count + " 次行动:";
    }
    if (!UseGui) {
      System.out.println(msg);
    } else {
      GuiFrame.updateActionLabel(msg);
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
              Tool.getChessboard().setCharAt(Tool.convert2Index(i, j), Arrows.charAt(k));
            }
          }
          k++;
        }
      }
    }
    if (!isAllArrows) {
      eraseArrows(false);
    }
    Tool.updateChessboard();
  }

  private static ArrayList<Integer> markDirection(int x, int y) {
    ArrayList<Integer> directions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (!(i == x && j == y)) {
          int index = Tool.convert2Index(i, j);
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

  public static void eraseArrows() {
    eraseArrows(true);
  }

  public static void eraseArrows(boolean isAll) {
    String arrows;
    int max;
    if (isAll) {
      arrows = Arrows;
      max = 8;
    } else {
      arrows = "↙↘↖↗";
      max = 4;
    }
    for (int i = 0; i < max; i++) {
      int index = Tool.getChessboard().indexOf(String.valueOf(arrows.charAt(i)));
      if (index != -1) {
        if (index == Tool.convert2Index(2, 5) || index == Tool.convert2Index(5, 5) ||
                index == Tool.convert2Index(8, 5)) {
          Tool.getChessboard().setCharAt(index, '|');
        } else if (index == Tool.convert2Index(1, 5) || index == Tool.convert2Index(3, 5) ||
                index == Tool.convert2Index(4, 5) || index == Tool.convert2Index(6, 5) ||
                index == Tool.convert2Index(7, 5) || index == Tool.convert2Index(9, 5)) {
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

  public static CsvReader getCsvReader(String name) {
    String path = System.getProperty("user.dir") + name;
    try {
      return new CsvReader(path, ',', Charset.forName("utf-8"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Piece createPiece(String place) {
    String tmp[] = place.split("");
    if (tmp.length != 4) {
      return null;
    }
    try {
      return new Piece(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),
              tmp[2].charAt(0), Integer.parseInt(tmp[3]));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
