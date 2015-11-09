public class Piece extends Role {
  private int X;
  private int Y;
  private char P;
  private String Camp;
  private int SpecialMoveCheck = 0;

  public Piece(int x, int y, char p) {
    super(p);
    X = x;
    Y = y;
    P = p;

    if (y > 5 && y < 10) {
      Camp = "black";
    } else if (y > 0 && y < 5) {
      Camp = "red";
    }
  }

  public void battleWith(Piece piece, StringBuffer chessboard) throws ExceedAttackRangeException, SameCampException {
    if (Math.abs(X - piece.getX()) > 1 || Math.abs(Y - piece.getY()) > 1) {
      throw new ExceedAttackRangeException();
    }
    if (Camp.equals(piece.getCamp())) {
      throw new SameCampException();
    }

    Battle.start(this, piece);
    if (P == Battle.Loser) {
      removeFrom(chessboard);
    } else {
      piece.removeFrom(chessboard);
    }
  }

  public void moveTo(int x, int y, StringBuffer chessboard, int count) throws CanNotPlaceException,
          CanNotMoveException {
    char aimChar = chessboard.charAt(convert(x, y));
    int index = chessboard.indexOf(String.valueOf(P));

    if (Math.abs(x - X) > 1 || Math.abs(y - Y) > 1) {
      throw new CanNotMoveException();
    }
    if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
      throw new CanNotPlaceException();
    }

    if (index == 344 || index == 352 || index == 356 || index == 364 || index == 368 || index == 376) {
      if (aimChar != '*') {
        speciallyMoveTo(x, y, chessboard, count);
      } else {
        normallyMoveTo(x, y, chessboard);
      }
    } else if (aimChar == '*') {
      speciallyMoveTo(x, y, chessboard, count);
    } else {
      normallyMoveTo(x, y, chessboard);
    }
  }

  public void normallyMoveTo(int x, int y, StringBuffer chessboard) {
    SpecialMoveCheck = 0;
    removeFrom(chessboard);
    chessboard.setCharAt(convert(x, y), P);
    setX(x);
    setY(y);
  }

  public void speciallyMoveTo(int x, int y, StringBuffer chessboard, int count) {
    SpecialMoveCheck++;
    if (SpecialMoveCheck == 2) {
      normallyMoveTo(x, y, chessboard);
    } else if (count == 2) {
      SpecialMoveCheck = 0;
    }
  }

  public void removeFrom(StringBuffer chessboard) {
    int index = chessboard.indexOf(String.valueOf(P));
    if (index == 344 || index == 352 || index == 356 || index == 364 || index == 368 || index == 376) {
      chessboard.setCharAt(index, '*');
    } else if (index == 348 || index == 360 || index == 372) {
      chessboard.setCharAt(index, '|');
    } else {
      chessboard.setCharAt(index, ' ');
    }
    setX(-1);
    setY(-1);
  }

  public void placeTo(StringBuffer chessboard) throws CanNotPlaceException {
    if (chessboard.charAt(convert(X, Y)) != ' ') {
      throw new CanNotPlaceException();
    }
    chessboard.setCharAt(convert(X, Y), P);
  }

  public int convert(int x, int y) {
    return 648 - (y - 1) * 76 + (x - 1) * 4;
  }

  public String getCamp() {
    return Camp;
  }

  public char getP() {
    return P;
  }

  public int getX() {
    return X;
  }

  public void setX(int x) {
    X = x;
  }

  public int getY() {
    return Y;
  }

  public void setY(int y) {
    Y = y;
  }

  public void setSpecialMoveCheck(int specialMoveCheck) {
    SpecialMoveCheck = specialMoveCheck;
  }
}