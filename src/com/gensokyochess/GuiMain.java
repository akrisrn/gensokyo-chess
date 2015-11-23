package com.gensokyochess;

public class GuiMain extends Main {
  private static GuiFrame GuiFrame = new GuiFrame();

  public static void main(String[] args) {
    GuiMain guiMain = new GuiMain();
    Tool.setGuiFrame(GuiFrame);
    guiMain.start();
  }

  @Override
  protected void updateChessboard() {
    GuiFrame.updateChessboard(getChessboard());
  }
}
