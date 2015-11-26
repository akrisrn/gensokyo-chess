package com.gensokyochess;

public class Chessboard {
  private StringBuffer Chessboard;

  public Chessboard() {
    Chessboard = createChessboard();
  }

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

  public StringBuffer getChessboard() {
    return Chessboard;
  }
}
