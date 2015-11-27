package com.gensokyochess;

/**
 * Gui 主类
 */
public class GuiMain extends Main {
  /**
   * 主方法，构建图形框架，实例化一个主类开始启动游戏
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    GuiFrame guiFrame = new GuiFrame();
    Tool.setGuiFrame(guiFrame);

    ConfirmDialog dialog = new ConfirmDialog(guiFrame);
    dialog.setVisible(true);

    GuiMain guiMain = new GuiMain();
    guiMain.start();
  }
}
