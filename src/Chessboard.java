import java.util.ArrayList;

public class Chessboard {
  private StringBuffer Chessboard;
  private ArrayList<Piece> Pieces = new ArrayList<>();

  public Chessboard() {
    Chessboard = createChessboard();
  }

  public void show() {
    System.out.println(Chessboard);
  }

  public ArrayList<Piece> placePieces(String in) throws CanNotPlaceException {
    String cmd[] = in.split("\\s+");
    for (String aCmd : cmd) {
      String tmp[] = aCmd.split("");
      int x = Integer.parseInt(tmp[1]);
      int y = Integer.parseInt(tmp[3]);
      char p = tmp[4].charAt(0);
      Piece piece = new Piece(x, y, p);
      piece.placeTo(Chessboard);
      Pieces.add(piece);
    }
    return Pieces;
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
