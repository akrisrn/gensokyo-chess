package com.gensokyochess;

public class GuiMain extends Main {
  public static void main(String[] args) {
    GuiFrame guiFrame = new GuiFrame();
    Tool.setGuiFrame(guiFrame);

    ConfirmDialog dialog = new ConfirmDialog(guiFrame);
    dialog.setVisible(true);

    GuiMain guiMain = new GuiMain();
    guiMain.start();
  }
}
