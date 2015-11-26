package com.gensokyochess;

import com.gensokyochess.exception.*;

public class Piece extends Role {
  private int X;
  private int Y;
  private String Camp;
  private boolean IsKing;
  private int SpecialMoveCheck = 0;
  private StringBuffer chessboard = Tool.getChessboard();
  private boolean IsCanNotMove = false;

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

  public void checkAlive(Piece piece2) {
    if (!this.isAlive()) {
      this.remove();
    } else if (!piece2.isAlive()) {
      piece2.remove();
    }
  }

  private boolean isHaveObstacleBetween(Piece piece, boolean isXAxis) {
    int commonValue;
    int startXOrY;
    int overXOrY;
    if (isXAxis) {
      commonValue = Y;
      startXOrY = X;
      overXOrY = piece.getX();
    } else {
      commonValue = X;
      startXOrY = Y;
      overXOrY = piece.getY();
    }
    return !Tool.findPieces(isXAxis, commonValue, startXOrY, overXOrY).isEmpty();
  }

  public void opportunityBattleWith(Piece piece) {
    if (getAttackType() == 0 && !isInRiver() && !piece.isInRiver()) {
      Battle.opportunityBattle(this, piece);
      checkAlive(piece);
    }
  }

  public void remoteBattleWith(Piece piece) throws ExceedAttackRangeException,
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
      if (isHaveObstacleBetween(piece, false)) {
        throw new HaveObstacleException();
      }
      int distance = Math.abs(Y - piece.getY()) - 1;
      Battle.remoteBattle(this, piece, distance);
    } else if (Y == piece.getY()) {
      if (isHaveObstacleBetween(piece, true)) {
        throw new HaveObstacleException();
      }
      int distance = Math.abs(X - piece.getX()) - 1;
      Battle.remoteBattle(this, piece, distance);
    } else if (Math.abs(X - piece.getX()) == 1 && Math.abs(Y - piece.getY()) == 1) {
      Battle.remoteBattle(this, piece, 0);
    } else {
      throw new ExceedAttackRangeException();
    }
    checkAlive(piece);
  }

  public void frontalBattleWith(Piece piece) throws ExceedAttackRangeException,
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
    checkAlive(piece);
  }

  public char[] findHaveChanceChar(int x, int y) {
    char haveChanceChar[] = new char[5];
    char nearbyChar[] = findNearbyChar();
    int direction = Tool.determineDirection(x, y, X, Y);

    switch (direction) {
      case 8:
        System.arraycopy(nearbyChar, 3, haveChanceChar, 0, 3);
        break;
      case 9:
        System.arraycopy(nearbyChar, 3, haveChanceChar, 0, 5);
        break;
      case 6:
        System.arraycopy(nearbyChar, 5, haveChanceChar, 0, 3);
        break;
      case 3:
        System.arraycopy(nearbyChar, 5, haveChanceChar, 0, 3);
        System.arraycopy(nearbyChar, 0, haveChanceChar, 3, 2);
        break;
      case 2:
        System.arraycopy(nearbyChar, 0, haveChanceChar, 0, 2);
        haveChanceChar[2] = nearbyChar[7];
        break;
      case 1:
        System.arraycopy(nearbyChar, 0, haveChanceChar, 0, 4);
        haveChanceChar[4] = nearbyChar[7];
        break;
      case 4:
        System.arraycopy(nearbyChar, 1, haveChanceChar, 0, 3);
        break;
      case 7:
        System.arraycopy(nearbyChar, 1, haveChanceChar, 0, 5);
        break;
      case 5:
        haveChanceChar = nearbyChar;
        break;
    }
    return haveChanceChar;
  }

  private char[] findNearbyChar() {
    char nearbyChar[] = new char[8];
    int count = 0;

    for (int i = X + 1; i >= X - 1; i--) {
      for (int j = Y + 1; j >= Y - 1; j--) {
        if (!(i == X && j == Y)) {
          int index = Tool.convertToIndex(i, j);
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

  public int[] handleMove(int move) {
    int tmp[] = new int[2];
    switch (move) {
      case 1:
        tmp[0] = X - 1;
        tmp[1] = Y - 1;
        return tmp;
      case 2:
        tmp[0] = X;
        tmp[1] = Y - 1;
        return tmp;
      case 3:
        tmp[0] = X + 1;
        tmp[1] = Y - 1;
        return tmp;
      case 4:
        tmp[0] = X - 1;
        tmp[1] = Y;
        return tmp;
      case 6:
        tmp[0] = X + 1;
        tmp[1] = Y;
        return tmp;
      case 7:
        tmp[0] = X - 1;
        tmp[1] = Y + 1;
        return tmp;
      case 8:
        tmp[0] = X;
        tmp[1] = Y + 1;
        return tmp;
      case 9:
        tmp[0] = X + 1;
        tmp[1] = Y + 1;
        return tmp;
      default:
        return null;
    }
  }

  public char[] moveTo(int move, int count, boolean noChance) throws CanNotPlaceException,
          CanNotMoveException, KingMoveException {
    if (move == 5) {
      addDefenseBonus();
      return null;
    }

    int tmp[] = handleMove(move);
    int x = tmp[0];
    int y = tmp[1];

    char aimChar = chessboard.charAt(Tool.convertToIndex(x, y));

    if (IsCanNotMove || x < 1 || x > 9 || y < 1 || y > 9) {
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
        return speciallyMoveTo(x, y, false, count);
      } else {
        return normallyMoveTo(x, y, noChance);
      }
    } else if (aimChar == '*') {
      return speciallyMoveTo(x, y, true, count);
    } else {
      return normallyMoveTo(x, y, noChance);
    }
  }

  private char[] normallyMoveTo(int x, int y, boolean noChance) {
    subDefenseBonus();
    SpecialMoveCheck = 0;
    char haveChanceChars[] = null;

    if (!noChance) {
      haveChanceChars = findHaveChanceChar(x, y);
    }

    remove();
    chessboard.setCharAt(Tool.convertToIndex(x, y), getCode());
    setXY(x, y);
    return haveChanceChars;
  }

  private char[] speciallyMoveTo(int x, int y, boolean into, int count) {
    subDefenseBonus();
    char haveChanceChars[] = null;
    SpecialMoveCheck++;

    if (SpecialMoveCheck == 2) {
      if (into) {
        addRiverBonus();
        setInRiver(true);
      } else {
        subRiverBonus();
        setInRiver(false);
      }

      SpecialMoveCheck = 0;
      remove();
      chessboard.setCharAt(Tool.convertToIndex(x, y), getCode());
      setXY(x, y);
    } else if (count == 2) {
      if (into) {
        haveChanceChars = findHaveChanceChar(x, y);
      }
      SpecialMoveCheck = 0;
    } else {
      if (into) {
        haveChanceChars = findHaveChanceChar(x, y);
      }
    }
    return haveChanceChars;
  }

  private void remove() {
    if (Y == 5) {
      if (X == 2 || X == 5 || X == 8) {
        chessboard.setCharAt(Tool.convertToIndex(X, Y), '|');
      } else {
        chessboard.setCharAt(Tool.convertToIndex(X, Y), '*');
      }
    } else {
      chessboard.setCharAt(Tool.convertToIndex(X, Y), ' ');
    }
    setXY(-1, -1);
  }

  public void place() throws CanNotPlaceException {
    if (chessboard.charAt(Tool.convertToIndex(X, Y)) != ' ') {
      throw new CanNotPlaceException();
    }
    chessboard.setCharAt(Tool.convertToIndex(X, Y), getCode());
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

  public void setCanNotMove(boolean canNotMove) {
    IsCanNotMove = canNotMove;
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

    return "\n姓名: " + getNameAndLV() + state + camp + "\n" +
            "生命: " + getCurrentHP() + "(" + getHitPoint() + ")\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n" +
            "技能: " + getSpell() + "\n";
  }
}
