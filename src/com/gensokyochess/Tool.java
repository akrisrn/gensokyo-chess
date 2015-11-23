package com.gensokyochess;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Tool {
  private static GuiFrame GuiFrame;
  private static boolean UseGui = false;

  public static void setGuiFrame(GuiFrame guiFrame) {
    GuiFrame = guiFrame;
    UseGui = true;
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

  public static Piece findPiece(char code, ArrayList<Piece> pieces) {
    for (Piece piece : pieces) {
      if (piece.getCode() == code) {
        return piece;
      }
    }
    return null;
  }

  public static Piece findPiece(String camp, char code, ArrayList<Piece> pieces) {
    for (Piece piece : pieces) {
      if (piece.getCamp().equals(camp)) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }
}
