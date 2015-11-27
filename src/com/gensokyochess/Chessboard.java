package com.gensokyochess;

/**
 * 棋盘类
 */
public class Chessboard {
  private StringBuffer Chessboard;

  /**
   * 实例化一个棋盘
   */
  public Chessboard() {
    Chessboard = createChessboard();
  }

  /**
   * 创建一个空白的棋盘字符串
   *
   * @return 棋盘字符串的可变对象
   */
  private StringBuffer createChessboard() {
    String chessboard = "";

    chessboard += "┌";
    for (int j = 1; j <= 8; j++) {
      chessboard += "───┬";
    }
    chessboard += "───┐\n";

    for (int i = 2; i <= 18; i++) {
      if (i % 2 == 1) {
        chessboard += "├";
        for (int j = 1; j <= 8; j++) {
          chessboard += "───┼";
        }
        chessboard += "───┤";
      }
      if (i % 2 == 0) {
        chessboard += "│";
        if (i == 10) {
          for (int j = 1; j <= 3; j++) {
            chessboard += " * │ | │ * │";
          }
        } else {
          for (int j = 1; j <= 9; j++) {
            chessboard += "   │";
          }
        }
      }
      chessboard += '\n';
    }

    chessboard += "└";
    for (int j = 1; j <= 8; j++) {
      chessboard += "───┴";
    }
    chessboard += "───┘";
    return new StringBuffer(chessboard);
  }

  /**
   * 获取棋盘
   *
   * @return 棋盘字符串的可变对象
   */
  public StringBuffer getChessboard() {
    return Chessboard;
  }
}
