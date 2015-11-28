package com.gensokyochess;

import com.gensokyochess.exception.*;

import java.util.ArrayList;

/**
 * 主类
 */
public class Main {
  private StringBuffer Chessboard = new Chessboard().getChessboard();
  private ArrayList<Piece> Pieces = new ArrayList<>();
  private boolean NoChance;
  private boolean HaveBattleOrSpell;
  private boolean IsRandomPlace = false;
  private Piece RedKing, BlackKing;
  private boolean CurrentCampIsRed = true;

  /**
   * 主方法，实例化一个主类开始启动游戏
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    Main main = new Main();
    main.start();
  }

  /**
   * 启动游戏
   */
  protected void start() {
    Tool.setChessboard(Chessboard);
    Tool.setPieces(Pieces);

    Tool.print("是否使用随机布局?(Y/N)", true, 0);
    if (Tool.input().equalsIgnoreCase("y")) {
      IsRandomPlace = true;
      Tool.print("-----自动填入棋子-----");
    }

    placePieces(true);
    placePieces(false);

    Tool.setRedKing(RedKing);
    Tool.updateChessboard();
    beginRound();
  }

  /**
   * 开始一个个不断循环的游戏回合
   */
  private void beginRound() {
    int count = 0;
    while (true) {
      if (count % 2 != 1) {
        Tool.CountRound();
      }
      count++;
      Tool.updateRoundMsg();

      NoChance = false;
      HaveBattleOrSpell = false;

      for (int i = 1; i <= 2; i++) {
        if (!startAction(i)) {
          return;
        }
        NoChance = i == 1 && HaveBattleOrSpell;
      }
      CurrentCampIsRed = !CurrentCampIsRed;
      Tool.setCurrentCampIsRed(CurrentCampIsRed);
    }
  }

  /**
   * 一方开始进行行动
   *
   * @param count 第几次行动
   * @return 是否行动成功
   */
  private boolean startAction(int count) {
    boolean inputError;
    do {
      Tool.updateActionMsg(count, 0);

      ArrayList action;
      if (Tool.getCurrentCamp()) {
        action = handleInput(Tool.input());
      } else {
        action = handleInput(Tool.AiInput());
      }
      inputError = !executeAction(action, count);

      if (!inputError) {
        Tool.updateChessboard();
        if (isGameOver()) {
          return false;
        }
      }
    } while (inputError);
    return true;
  }

  /**
   * 根据长度和类型对输入指令进行处理并储存到一个列表中，列表第一个值记录指令的类型（0：空，1：查看，2：移动，3：战斗，4：技能）
   *
   * @param in 输入
   * @return 处理后的指令，输入错误返回 null
   */
  @SuppressWarnings("unchecked")
  private ArrayList handleInput(String in) {
    ArrayList action = new ArrayList();
    char tmp[] = in.toCharArray();

    switch (tmp.length) {
      case 1:
        if (tmp[0] == ' ') {
          action.add(0);
        } else {
          action.add(1);
          Piece piece = Tool.findPiece(tmp[0]);
          if (piece == null) return null;
          action.add(piece);
        }
        break;
      case 2:
        action.add(2);
        try {
          int move = Character.getNumericValue(tmp[1]);
          if (move < 1 || move > 9) return null;
          action.add(tmp[0]);
          action.add(move);
        } catch (NumberFormatException e) {
          return null;
        }
        break;
      case 3:
        action.add(3);
        Piece piece1 = Tool.findPiece(tmp[0]);
        Piece piece2 = Tool.findPiece(tmp[2]);
        if (piece1 == null || piece2 == null) return null;
        action.add(piece1);
        action.add(piece2);
        break;
      case 4:
        action.add(4);
        Piece piece = Tool.findPiece(tmp[2]);
        if (piece == null) return null;
        action.add(piece);
        action.add(String.valueOf(tmp[2]) + tmp[3]);
        break;
      default:
        return null;
    }
    return action;
  }

  /**
   * 读取处理后的指令并执行
   *
   * @param action 指令
   * @param count  第几次行动
   * @return 是否执行成功（0，1 类型指令不算行动）
   */
  private boolean executeAction(ArrayList action, int count) {
    if (action == null) {
      Tool.print("输入有误");
      return false;
    }

    int id = (int) action.get(0);
    switch (id) {
      case 0:
        return false;
      case 1:
        Piece piece = (Piece) action.get(1);
        Tool.updateChessboard();
        Tool.print(piece.toString());
        return false;
      case 2:
        char code = (char) action.get(1);
        int move = (int) action.get(2);
        return moveAction(code, move, count);
      case 3:
        HaveBattleOrSpell = true;
        return battleAction(action);
      case 4:
        HaveBattleOrSpell = true;
        return spellAction(action);
      default:
        return false;
    }
  }

  /**
   * 放置一方的棋子
   *
   * @param isRed 是否是红方
   */
  private void placePieces(boolean isRed) {
    int count = 1;
    int levelCount = 0;
    boolean haveKing = false;
    String place;

    while (levelCount != 15) {
      if (!IsRandomPlace) {
        Tool.updateChessboard();
        Tool.print("-----当前棋子总等级: " + levelCount + "-----");
        Tool.print("请布置", false);
        if (isRed) {
          Tool.print("红方", false);
        } else {
          Tool.print("黑方", false);
        }
        Tool.print("第 " + count + " 个棋子: ", true);
        place = Tool.input();
      } else {
        place = rollPlace(isRed);
      }
      Piece piece = Tool.createPiece(place);

      if (piece != null) {
        if (piece.getCamp() != isRed) {
          Tool.print("请摆在己方区域", true, !IsRandomPlace);
        } else if (Tool.findPiece(piece.getCode()) != null) {
          Tool.print("棋盘上已经有了相同棋子", true, !IsRandomPlace);
        } else {
          levelCount += piece.getLevel();
          if (levelCount > 15) {
            Tool.print("总等级要等于 15", true, !IsRandomPlace);
            levelCount -= piece.getLevel();
          } else if (levelCount == 15 && !haveKing) {
            Tool.print("棋盘缺少国王", true, !IsRandomPlace);
            levelCount -= piece.getLevel();
          } else {
            try {
              piece.place();
              Pieces.add(piece);
              if (piece.isKing()) {
                haveKing = true;
                if (isRed) {
                  RedKing = piece;
                } else {
                  BlackKing = piece;
                }
              }
              count++;
            } catch (CanNotPlaceException e) {
              Tool.print("无法放到该格", true, !IsRandomPlace);
              levelCount -= piece.getLevel();
            }
          }
        }
      } else {
        Tool.print("输入有误", true, !IsRandomPlace);
      }
    }
    if (isRed) {
      Tool.print("-----红方布置完成------", true, !IsRandomPlace);
    } else {
      Tool.print("-----黑方布置完成------", true, !IsRandomPlace);
      Tool.print("-----开始游戏-----");
    }
  }

  /**
   * 随机生成一个放置棋子指令
   *
   * @param isRed 是否是红方
   * @return 随机生成的指令
   */
  private String rollPlace(boolean isRed) {
    int bonus = 1;
    if (!isRed) {
      bonus = 6;
    }
    return "" + Tool.random(1, 9) +
            "" + Tool.random(bonus, 4) +
            (char) Tool.random(33, 94) +
            Tool.random(1, 5);
  }

  /**
   * 判断是否游戏结束
   *
   * @return 是否游戏结束
   */
  private boolean isGameOver() {
    if (!RedKing.isAlive()) {
      Tool.updateActionMsg(0, -1);
      return true;
    } else if (!BlackKing.isAlive()) {
      Tool.updateActionMsg(0, 1);
      return true;
    } else {
      int count = 0;
      for (Piece piece : Pieces) {
        if (piece.isAlive()) {
          count++;
          if (count > 2) {
            return false;
          }
        }
      }
      Tool.updateActionMsg(0, 0);
      return true;
    }
  }

  /**
   * 检查使用哪一个技能
   *
   * @param piece  使用技能的棋子
   * @param action 技能指令
   * @return 使用技能的编号，0 是没有这个技能
   */
  private int checkSpell(Piece piece, ArrayList action) {
    String spellCode = (String) action.get(2);
    for (int i = 1; i <= piece.getTotalSpellNumber(); i++) {
      if (spellCode.equals(piece.getSpellCode(i))) {
        return i;
      }
    }
    return 0;
  }

  /**
   * 使用技能的行动
   *
   * @param action 技能指令
   * @return 是否行动成功
   */
  private boolean spellAction(ArrayList action) {
    Piece piece = (Piece) action.get(1);
    if (piece.getCamp() != Tool.getCurrentCamp()) {
      Tool.print("你没有这个棋子");
      return false;
    }
    int spellNumber = checkSpell(piece, action);
    if (spellNumber != 0) {
      try {
        opportunityBattleAction(piece.findHaveChanceChar(piece.getX(), piece.getY()), piece);
        if (piece.isAlive()) {
          piece.useSpell(spellNumber);
        } else {
          Tool.eraseArrows();
        }
        return true;
      } catch (HaveNotSpellException e) {
        Tool.print("技能未实装");
        return false;
      } catch (KingSpellException e) {
        Tool.print("国王不受技能影响");
        return false;
      } catch (SameCampException e) {
        Tool.print("这是己方棋子");
        return false;
      }
    } else {
      Tool.print("没有这个技能");
      return false;
    }
  }

  /**
   * 战斗的行动，根据攻击棋子的攻击类型选择进行哪种攻击
   *
   * @param action 战斗指令
   * @return 是否行动成功
   */
  private boolean battleAction(ArrayList action) {
    Piece piece1 = (Piece) action.get(1);
    if (piece1.getCamp() != Tool.getCurrentCamp()) {
      Tool.print("你没有这个棋子");
      return false;
    }
    Piece piece2 = (Piece) action.get(2);

    if (piece1.getAttackType() == 0) {
      return frontalBattleAction(piece1, piece2);
    } else {
      return remoteBattleAction(piece1, piece2);
    }
  }

  /**
   * 远程攻击的行动
   *
   * @param piece1 攻击棋子
   * @param piece2 被攻击棋子
   * @return 是否行动成功
   */
  private boolean remoteBattleAction(Piece piece1, Piece piece2) {
    try {
      opportunityBattleAction(piece1.findHaveChanceChar(piece1.getX(), piece1.getY()), piece1);
      if (piece1.isAlive()) {
        piece1.remoteBattleWith(piece2);
      } else {
        Tool.eraseArrows();
      }
      return true;
    } catch (ExceedAttackRangeException e) {
      Tool.print("超出攻击范围");
      return false;
    } catch (HaveObstacleException e) {
      Tool.print("中间有障碍");
      return false;
    } catch (SameCampException e) {
      Tool.print("这是己方棋子");
      return false;
    } catch (InRiverException e) {
      Tool.print("在河流中无法进行攻击");
      return false;
    } catch (KingSpellException e) {
      Tool.print("国王不受来自对面区域的攻击");
      return false;
    }
  }

  /**
   * 正面攻击的行动
   *
   * @param piece1 攻击棋子
   * @param piece2 被攻击棋子
   * @return 是否行动成功
   */
  private boolean frontalBattleAction(Piece piece1, Piece piece2) {
    try {
      piece1.frontalBattleWith(piece2);
      return true;
    } catch (SameCampException e) {
      Tool.print("这是己方棋子");
      return false;
    } catch (ExceedAttackRangeException e) {
      Tool.print("超出攻击范围");
      return false;
    } catch (InRiverException e) {
      Tool.print("在河流中无法进行攻击");
      return false;
    }
  }

  /**
   * 移动的行动
   *
   * @param code  移动的棋子代码
   * @param move  移动的方向
   * @param count 第几次行动
   * @return 是否行动成功
   */
  private boolean moveAction(char code, int move, int count) {
    Piece piece = Tool.findPiece(Tool.getCurrentCamp(), code);
    if (piece != null) {
      try {
        if (move == 5) {
          Tool.print(piece.getNameAndLv() + " 进行原地防御");
        }
        opportunityBattleAction(piece.moveTo(move, count, NoChance), piece);
        return true;
      } catch (CanNotPlaceException e) {
        Tool.print("该格已有棋子", true, Tool.getCurrentCamp());
        return false;
      } catch (CanNotMoveException | KingMoveException e) {
        Tool.print("无法进行移动", true, Tool.getCurrentCamp());
        return false;
      }
    } else {
      Tool.print("你没有这个棋子");
      return false;
    }
  }

  /**
   * 借机攻击的行动
   *
   * @param haveChanceChars 有机会进行借机的棋子
   * @param piece           被借机的棋子
   */
  private void opportunityBattleAction(char[] haveChanceChars, Piece piece) {
    if (haveChanceChars != null) {
      for (char haveChanceChar : haveChanceChars) {
        if (haveChanceChar != ' ') {
          Piece haveChancePiece = Tool.findPiece(haveChanceChar);
          if (haveChancePiece != null && haveChancePiece.getCamp() != piece.getCamp() && piece.isAlive()) {
            try {
              haveChancePiece.opportunityBattleWith(piece);
            } catch (InRiverException ignored) {
            }
          }
        }
      }
    }
  }
}
