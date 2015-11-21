package com.gensokyochess;

public class Chessboard {
  private StringBuffer Chessboard;

  public Chessboard() {
    Chessboard = createChessboard();
  }

  public Piece createPiece(String place) {
    int par[] = new int[4];
    String tmp[] = place.split("");
    if (tmp.length != 6) {
      return null;
    }
    try {
      par[0] = Integer.parseInt(tmp[1]);
      par[1] = Integer.parseInt(tmp[3]);
      par[2] = (int) tmp[4].charAt(0);
      par[3] = Integer.parseInt(tmp[5]);
      return new Piece(par[0], par[1], (char) par[2], par[3]);
    } catch (NumberFormatException e) {
      return null;
    }
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
