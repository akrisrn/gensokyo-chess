package com.gensokyochess;

import com.gensokyochess.exception.*;

/**
 * 棋子类
 */
public class Piece extends Role {
  private int X;
  private int Y;
  private boolean IsRed;
  private boolean IsKing;
  private int SpecialMoveCheck = 0;
  private StringBuffer Chessboard = Tool.getChessboard();
  private boolean IsCanNotMove = false;
  private int Duration = 0;

  /**
   * 实例化一个棋子，根据初始坐标来判断阵营和是否是国王
   *
   * @param x     棋子的初始 x 坐标
   * @param y     棋子的初始 y 坐标
   * @param code  棋子代码
   * @param level 棋子等级
   */
  public Piece(int x, int y, char code, int level) {
    super(code, level);
    X = x;
    Y = y;

    if (y > 5 && y < 10) {
      IsRed = false;
    } else if (y > 0 && y < 5) {
      IsRed = true;
    }

    if (IsKing = x == 5 && (y == 1 || y == 9)) {
      initRole(code, 0);
    } else if (level == 0) {
      initRole(code, 1);
    }
  }

  /**
   * 检查当前棋子和另一个棋子是否存活，如果阵亡则从棋盘上移去
   *
   * @param piece 检查的另一个棋子
   */
  public void checkAlive(Piece piece) {
    if (!isAlive()) {
      remove();
    } else if (!piece.isAlive()) {
      piece.remove();
    }
  }

  /**
   * 检查直线距离上是否有障碍在当前棋子和另一个棋子之间
   *
   * @param piece   另一个棋子
   * @param isXAxis 是否都在 x 轴上
   * @return 是否有障碍
   */
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

  /**
   * 对一个棋子进行借机攻击
   *
   * @param piece 被借机的棋子
   * @throws InRiverException 有棋子在河流中
   */
  public void opportunityBattleWith(Piece piece) throws InRiverException {
    if (getAttackType() == 0) {
      Battle.opportunityBattle(this, piece);
      checkAlive(piece);
    }
  }

  /**
   * 对一个棋子进行远程攻击
   *
   * @param piece 被攻击的棋子
   * @throws ExceedAttackRangeException 超出攻击范围
   * @throws SameCampException          相同阵营
   * @throws HaveObstacleException      棋子之间有障碍
   * @throws InRiverException           被攻击棋子在河流中
   * @throws KingSpellException         国王技能
   */
  public void remoteBattleWith(Piece piece) throws ExceedAttackRangeException,
          SameCampException, HaveObstacleException, InRiverException, KingSpellException {
    if (getCamp() == piece.getCamp()) {
      throw new SameCampException();
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

  /**
   * 对一个棋子进行正面攻击
   *
   * @param piece 被攻击的棋子
   * @throws ExceedAttackRangeException 超出攻击范围
   * @throws SameCampException          相同阵营
   * @throws InRiverException           有棋子在河流中
   */
  public void frontalBattleWith(Piece piece) throws ExceedAttackRangeException,
          SameCampException, InRiverException {
    if (Math.abs(X - piece.getX()) > 1 || Math.abs(Y - piece.getY()) > 1) {
      throw new ExceedAttackRangeException();
    }

    if (getCamp() == piece.getCamp()) {
      throw new SameCampException();
    }

    Battle.frontalBattle(this, piece);
    checkAlive(piece);
  }

  /**
   * 寻找在棋子移动、远程攻击和使用技能时有机会进行借机的棋子
   *
   * @param x 移动到的 x 坐标
   * @param y 移动到的 y 坐标
   * @return 有机会进行借机的棋子
   */
  public char[] findHaveChanceChar(int x, int y) {
    char[] haveChanceChar = new char[5];
    char[] nearbyChar = findNearbyChar();
    int direction = Tool.convertXY2Move(x, y, X, Y);

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

  /**
   * 寻找当前棋子威胁区域内的所有棋子，对没有棋子的格子返回一个空格
   *
   * @return 威胁区域内的棋子列表
   */
  public char[] findNearbyChar() {
    char[] nearbyChar = new char[8];
    int count = 0;

    for (int i = X + 1; i >= X - 1; i--) {
      for (int j = Y + 1; j >= Y - 1; j--) {
        if (!(i == X && j == Y)) {
          int index = Tool.convertXY2Index(i, j);
          if (index == 0) {
            nearbyChar[count] = ' ';
          } else {
            char aimChar = Chessboard.charAt(index);
            if (aimChar == '*' && Tool.findSpecialPiece(index, '*') != null) {
              nearbyChar[count] = aimChar;
            } else if (aimChar == '|' && Tool.findSpecialPiece(index, '|') != null) {
              nearbyChar[count] = aimChar;
            } else if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
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

  /**
   * 使当前棋子进行移动
   *
   * @param move     移动方向
   * @param count    第几回合
   * @param noChance 是否没有可能引发借机
   * @return 有可能进行借机的棋子，没有则返回 null
   * @throws CanNotMoveException  要移动的格子已经有棋子
   * @throws CanNotPlaceException 超出棋盘范围
   * @throws KingMoveException    国王离开了己方区域
   */
  public char[] moveTo(int move, int count, boolean noChance) throws CanNotMoveException,
          CanNotPlaceException, KingMoveException {
    return moveTo(move, count, noChance, false);
  }

  /**
   * 使当前棋子进行移动
   *
   * @param move      移动方向
   * @param count     第几回合
   * @param noChance  是否没有可能引发借机
   * @param noSpecial 是否不进行特殊移动
   * @return 有可能进行借机的棋子，没有则返回 null
   * @throws CanNotMoveException  要移动的格子已经有棋子
   * @throws CanNotPlaceException 超出棋盘范围
   * @throws KingMoveException    国王离开了己方区域
   */
  public char[] moveTo(int move, int count, boolean noChance, boolean noSpecial) throws CanNotPlaceException,
          CanNotMoveException, KingMoveException {
    if (move == 5) {
      addDefenseBonus();
      return null;
    }

    int[] tmp = Tool.convertMove2XY(X, Y, move);
    assert tmp != null;
    int x = tmp[0];
    int y = tmp[1];
    char aimChar = Chessboard.charAt(Tool.convertXY2Index(x, y));

    if (Tool.getRoundCount() > Duration) {
      IsCanNotMove = false;
    }

    if (IsCanNotMove || x < 1 || x > 9 || y < 1 || y > 9) {
      throw new CanNotMoveException();
    }
    if (aimChar != ' ' && aimChar != '*' && aimChar != '|') {
      throw new CanNotPlaceException();
    }
    if (isKing() && y == 5) {
      throw new KingMoveException();
    }

    if (noSpecial) {
      return normallyMoveTo(x, y, noChance);
    } else {
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
  }

  /**
   * 普通移动
   *
   * @param x        要移动的 x 坐标
   * @param y        要移动的 x 坐标
   * @param noChance 是否没有可能引发借机
   * @return 有可能进行借机的棋子，没有则返回 null
   */
  private char[] normallyMoveTo(int x, int y, boolean noChance) {
    clearDefenseBonus();
    SpecialMoveCheck = 0;
    char[] haveChanceChars = null;

    if (!noChance) {
      haveChanceChars = findHaveChanceChar(x, y);
    }

    remove();
    Chessboard.setCharAt(Tool.convertXY2Index(x, y), getCode());
    setXY(x, y);
    return haveChanceChars;
  }

  /**
   * 特殊移动（进河或出河）
   *
   * @param x     要移动的 x 坐标
   * @param y     要移动的 x 坐标
   * @param into  是否是进河
   * @param count 第几回合
   * @return 有可能进行借机的棋子，没有则返回 null
   */
  private char[] speciallyMoveTo(int x, int y, boolean into, int count) {
    clearDefenseBonus();
    SpecialMoveCheck++;
    char[] haveChanceChars = null;

    if (SpecialMoveCheck == 2) {
      if (into) {
        addRiverBonus();
        setInRiver(true);
      } else {
        clearRiverBonus();
        setInRiver(false);
      }

      SpecialMoveCheck = 0;
      remove();
      Chessboard.setCharAt(Tool.convertXY2Index(x, y), getCode());
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

  /**
   * 从棋盘上移除棋子
   */
  private void remove() {
    if (Y == 5) {
      if (X == 2 || X == 5 || X == 8) {
        Chessboard.setCharAt(Tool.convertXY2Index(X, Y), '|');
      } else {
        Chessboard.setCharAt(Tool.convertXY2Index(X, Y), '*');
      }
    } else {
      Chessboard.setCharAt(Tool.convertXY2Index(X, Y), ' ');
    }
    setXY(-1, -1);
  }

  /**
   * 放置棋子到棋盘上
   *
   * @throws CanNotPlaceException 放置的格子不为空
   */
  public void place() throws CanNotPlaceException {
    if (Chessboard.charAt(Tool.convertXY2Index(X, Y)) != ' ') {
      throw new CanNotPlaceException();
    }
    Chessboard.setCharAt(Tool.convertXY2Index(X, Y), getCode());
  }

  public boolean isKing() {
    return IsKing;
  }

  public boolean getCamp() {
    return IsRed;
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

  /**
   * 使棋子无法移动
   *
   * @param duration 持续的回合数
   */
  public void setCanNotMove(int duration) {
    IsCanNotMove = true;
    Duration = Tool.getRoundCount() + duration;
    if (Tool.getCurrentCamp()) {
      Duration--;
    }
  }

  @Override
  public String toString() {
    String camp;
    String state;

    if (IsRed) {
      camp = "(红)";
    } else {
      camp = "(黑)";
    }

    if (IsKing) {
      state = "(国王)";
    } else {
      if (isAlive()) {
        state = "(存活)";
      } else {
        state = "(阵亡)";
      }
    }

    return "\n-----" + getNameAndLv() + state + camp + "\n" +
            "生命: " + getCurrentHP() + "(" + getHitPoint() + ")\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n" +
            "-----技能" + getSpell();
  }
}
