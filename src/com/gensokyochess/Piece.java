package com.gensokyochess;

public class Piece extends Role {
  private int X;
  private int Y;
  private String Camp;
  private boolean IsKing;
  private boolean InRiver = false;
  private int SpecialMoveCheck = 0;

  public Piece(int x, int y, char code, int level) {
    super(code, level);
    X = x;
    Y = y;

    if (y > 5 && y < 10) {
      Camp = "black";
    } else if (y > 0 && y < 5) {
      Camp = "red";
    } else {
      Camp = "";
    }

    if (IsKing = x == 5 && (y == 1 || y == 9)) {
      setRoleLevel(code, 0);
    }
  }

  private void removeLoser(Piece piece1, Piece piece2, StringBuffer chessboard) {
    if (!piece1.isAlive()) {
      piece1.removeFrom(chessboard);
    } else if (!piece2.isAlive()) {
      piece2.removeFrom(chessboard);
    }
  }

  private boolean isHaveObstacleBetween(Piece piece, boolean isXAxis, StringBuffer chessboard) {
    int min;
    int max;
    if (isXAxis) {
      if (piece.getX() - X > 0) {
        min = X + 1;
        max = piece.getX();
      } else {
        min = piece.getX() + 1;
        max = X;
      }
    } else {
      if (piece.getY() - Y > 0) {
        min = Y + 1;
        max = piece.getY();
      } else {
        min = piece.getY() + 1;
        max = Y;
      }
    }

    for (int i = min; i < max; i++) {
      char aimChar;
      if (isXAxis) {
        aimChar = chessboard.charAt(convert(i, Y));
      } else {
        aimChar = chessboard.charAt(convert(X, i));
      }
      if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
        return true;
      }
    }
    return false;
  }

  public void opportunityBattleWith(Piece piece, StringBuffer chessboard) {
    if (getAttackType() == 0 && !isInRiver() && !piece.isInRiver()) {
      Battle.opportunityBattle(this, piece);
      removeLoser(this, piece, chessboard);
    }
  }

  public void remoteBattleWith(Piece piece, StringBuffer chessboard) throws ExceedAttackRangeException,
          SameCampException, HaveObstacleException, InRiverException, KingSpellException {
    if (Camp.equals(piece.getCamp())) {
      throw new SameCampException();
    }
    if (isInRiver()) {
      throw new InRiverException();
    }
    if (piece.isKing()) {
      if ((piece.getY() < 5 && Y > 5) || (piece.getY() > 5 && Y < 5)) {
        throw new KingSpellException();
      }
    }

    if (X == piece.getX()) {
      if (isHaveObstacleBetween(piece, false, chessboard)) {
        throw new HaveObstacleException();
      }
      int distance = Math.abs(Y - piece.getY()) - 1;
      Battle.remoteBattle(this, piece, distance);
    } else if (Y == piece.getY()) {
      if (isHaveObstacleBetween(piece, true, chessboard)) {
        throw new HaveObstacleException();
      }
      int distance = Math.abs(X - piece.getX()) - 1;
      Battle.remoteBattle(this, piece, distance);
    } else {
      throw new ExceedAttackRangeException();
    }
    removeLoser(this, piece, chessboard);
  }

  public void frontalBattleWith(Piece piece, StringBuffer chessboard) throws ExceedAttackRangeException,
          SameCampException, InRiverException {
    if (Math.abs(X - piece.getX()) > 1 || Math.abs(Y - piece.getY()) > 1) {
      throw new ExceedAttackRangeException();
    }

    if (Camp.equals(piece.getCamp())) {
      throw new SameCampException();
    }

    if (isInRiver() || piece.isInRiver()) {
      throw new InRiverException();
    }

    Battle.frontalBattle(this, piece);
    removeLoser(this, piece, chessboard);
  }

  public char[] findHaveChanceChar(int x, int y, StringBuffer chessboard) {
    char haveChanceChar[] = new char[5];
    char nearbyChar[] = findNearbyChar(chessboard);
    int direction = determineDirection(x, y);

    switch (direction) {
      case 0:
        System.arraycopy(nearbyChar, 3, haveChanceChar, 0, 3);
        break;
      case 1:
        System.arraycopy(nearbyChar, 3, haveChanceChar, 0, 5);
        break;
      case 2:
        System.arraycopy(nearbyChar, 5, haveChanceChar, 0, 3);
        break;
      case 3:
        System.arraycopy(nearbyChar, 5, haveChanceChar, 0, 3);
        System.arraycopy(nearbyChar, 0, haveChanceChar, 3, 2);
        break;
      case 4:
        System.arraycopy(nearbyChar, 0, haveChanceChar, 0, 2);
        haveChanceChar[2] = nearbyChar[7];
        break;
      case 5:
        System.arraycopy(nearbyChar, 0, haveChanceChar, 0, 4);
        haveChanceChar[4] = nearbyChar[7];
        break;
      case 6:
        System.arraycopy(nearbyChar, 1, haveChanceChar, 0, 3);
        break;
      case 7:
        System.arraycopy(nearbyChar, 1, haveChanceChar, 0, 5);
        break;
      case 8:
        haveChanceChar = nearbyChar;
        break;
    }
    return haveChanceChar;
  }

  private char[] findNearbyChar(StringBuffer chessboard) {
    char nearbyChar[] = new char[8];
    int count = 0;

    for (int i = X + 1; i >= X - 1; i--) {
      for (int j = Y + 1; j >= Y - 1; j--) {
        if (!(i == X && j == Y)) {
          int index = convert(i, j);
          if (index == 0) {
            nearbyChar[count] = ' ';
          } else {
            char aimChar = chessboard.charAt(index);
            if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
              nearbyChar[count] = aimChar;
            } else {
              nearbyChar[count] = ' ';
            }
          }
          count++;
        }
      }
    }

    char tmp = nearbyChar[3];
    System.arraycopy(nearbyChar, 0, nearbyChar, 1, 3);
    nearbyChar[0] = tmp;
    tmp = nearbyChar[7];
    nearbyChar[7] = nearbyChar[5];
    nearbyChar[5] = tmp;
    return nearbyChar;
  }

  private int determineDirection(int x, int y) {
    /*
       701
       682
       543
     */
    if (x > X) {
      if (y > Y) {
        return 1;
      } else if (y < Y) {
        return 3;
      } else {
        return 2;
      }
    } else if (x < X) {
      if (y > Y) {
        return 7;
      } else if (y < Y) {
        return 5;
      } else {
        return 6;
      }
    } else {
      if (y > Y) {
        return 0;
      } else if (y < Y) {
        return 4;
      } else {
        return 8;
      }
    }
  }

  public char[] moveTo(int x, int y, StringBuffer chessboard, int count, boolean noChance) throws CanNotPlaceException,
          CanNotMoveException, KingMoveException {
    char aimChar = chessboard.charAt(convert(x, y));

    if (Math.abs(x - X) > 1 || Math.abs(y - Y) > 1) {
      throw new CanNotMoveException();
    }
    if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
      throw new CanNotPlaceException();
    }
    if (isKing() && y == 5) {
      throw new KingMoveException();
    }

    if (Y == 5 && X != 2 && X != 5 && X != 8) {
      if (aimChar != '*') {
        return speciallyMoveTo(x, y, chessboard, false, count);
      } else {
        return normallyMoveTo(x, y, chessboard, noChance);
      }
    } else if (aimChar == '*') {
      return speciallyMoveTo(x, y, chessboard, true, count);
    } else {
      return normallyMoveTo(x, y, chessboard, noChance);
    }
  }

  private char[] normallyMoveTo(int x, int y, StringBuffer chessboard, boolean noChance) {
    SpecialMoveCheck = 0;
    char haveChanceChars[] = null;

    if (!noChance) {
      haveChanceChars = findHaveChanceChar(x, y, chessboard);
    }

    removeFrom(chessboard);
    chessboard.setCharAt(convert(x, y), getCode());
    setXY(x, y);
    return haveChanceChars;
  }

  private char[] speciallyMoveTo(int x, int y, StringBuffer chessboard, boolean into, int count) {
    char haveChanceChars[] = null;
    SpecialMoveCheck++;

    if (SpecialMoveCheck == 2) {
      if (into) {
        addRiverBonus();
        InRiver = true;
      } else {
        subRiverBonus();
        InRiver = false;
      }

      SpecialMoveCheck = 0;
      removeFrom(chessboard);
      chessboard.setCharAt(convert(x, y), getCode());
      setXY(x, y);
    } else if (count == 2) {
      if (into) {
        haveChanceChars = findHaveChanceChar(x, y, chessboard);
      }
      SpecialMoveCheck = 0;
    } else {
      if (into) {
        haveChanceChars = findHaveChanceChar(x, y, chessboard);
      }
    }
    return haveChanceChars;
  }

  private void removeFrom(StringBuffer chessboard) {
    if (Y == 5) {
      if (X == 2 || X == 5 || X == 8) {
        chessboard.setCharAt(convert(X, Y), '|');
      } else {
        chessboard.setCharAt(convert(X, Y), '*');
      }
    } else {
      chessboard.setCharAt(convert(X, Y), ' ');
    }
    setXY(-1, -1);
  }

  public void placeTo(StringBuffer chessboard) throws CanNotPlaceException {
    if (chessboard.charAt(convert(X, Y)) != ' ') {
      throw new CanNotPlaceException();
    }
    chessboard.setCharAt(convert(X, Y), getCode());
  }

  private int convert(int x, int y) {
    if (x < 1 || y < 1 || x > 9 || y > 9) {
      return 0;
    } else {
      return 648 - (y - 1) * 76 + (x - 1) * 4;
    }
  }

  public boolean isInRiver() {
    return InRiver;
  }

  public boolean isKing() {
    return IsKing;
  }

  public String getCamp() {
    return Camp;
  }

  public int getX() {
    return X;
  }

  public int getY() {
    return Y;
  }

  private void setXY(int x, int y) {
    X = x;
    Y = y;
  }

  @Override
  public String toString() {
    String camp;
    String state;

    if (getCamp().equals("red")) {
      camp = "(红)";
    } else {
      camp = "(黑)";
    }

    if (isKing()) {
      state = "(国王)";
    } else {
      if (isAlive()) {
        state = "(存活)";
      } else {
        state = "(阵亡)";
      }
    }

    return "姓名: " + getNameAndLV() + state + camp + "\n" +
            "生命: " + getCurrentHP() + "(" + getHitPoint() + ")" + "\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n";
  }
}
