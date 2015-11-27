package com.gensokyochess;

import com.gensokyochess.exception.*;

import java.util.ArrayList;

public class Main {
  private StringBuffer Chessboard = new Chessboard().getChessboard();
  private ArrayList<Piece> Pieces = new ArrayList<>();
  private boolean NoChance;
  private boolean HaveBattleOrSpell;
  private boolean IsRandomPlace = false;
  private Piece RedKing, BlackKing;
  private boolean CurrentCampIsRed = true;

  public static void main(String[] args) {
    Main main = new Main();
    main.start();
  }

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

    Tool.updateChessboard();
    beginRound();
  }

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

  private boolean startAction(int i) {
    boolean inputError;
    do {
      Tool.updateActionMsg(i, 0);

      ArrayList action = handleInput(Tool.input());
      inputError = !handleAction(action, i);

      if (!inputError) {
        Tool.updateChessboard();
        if (isGameOver()) {
          return false;
        }
      }
    } while (inputError);
    return true;
  }

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

  private boolean handleAction(ArrayList action, int i) {
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
        return moveAction(code, move, i);
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

  private String rollPlace(boolean isRed) {
    int bonus = 0;
    if (!isRed) {
      bonus = 5;
    }
    return "" + (int) (Math.random() * 9 + 1) +
            "" + (int) (Math.random() * 4 + 1 + bonus) +
            (char) (Math.random() * 94 + 33) +
            (int) (Math.random() * 5 + 1);
  }

  private int checkSpell(Piece piece, ArrayList action) {
    String spellCode = (String) action.get(2);
    for (int i = 1; i <= piece.getTotalSpellNumber(); i++) {
      if (spellCode.equals(piece.getSpellCode(i))) {
        return i;
      }
    }
    return 0;
  }

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
        Tool.print("该格已有棋子");
        return false;
      } catch (CanNotMoveException | KingMoveException e) {
        Tool.print("无法进行移动");
        return false;
      }
    } else {
      Tool.print("你没有这个棋子");
      return false;
    }
  }
}
