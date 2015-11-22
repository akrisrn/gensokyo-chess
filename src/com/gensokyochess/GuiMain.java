package com.gensokyochess;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class GuiMain extends Main {
  private static GuiFrame GuiFrame = new GuiFrame();

  public static void main(String[] args) {
    GuiMain guiMain = new GuiMain();
    Battle.setGuiFrame(GuiFrame);
    guiMain.start();
  }

  @Override
  protected void print(String msg, boolean showMsg, boolean isALine) {
    if (showMsg) {
      GuiFrame.appendLog(msg, isALine);
    }
  }

  @Override
  protected String input() {
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

  private String uncheckedInput() {
    try {
      Scanner in = GuiFrame.getScanner();
      return in.nextLine();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  @Override
  protected void updateChessboard() {
    GuiFrame.updateChessboard(getChessboard());
  }
}
