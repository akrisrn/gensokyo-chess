package com.gensokyochess;

public class GuiMain extends Main {
  public static void main(String[] args) {
    GuiFrame Frame = new GuiFrame();
    Tool.setFrame(Frame);

    ConfirmDialog dialog = new ConfirmDialog(Frame);
    dialog.setVisible(true);

    GuiMain guiMain = new GuiMain();
    Frame.setChessboard(guiMain.getChessboard());
    Frame.setPieces(guiMain.getPieces());
    guiMain.start();
  }
}
