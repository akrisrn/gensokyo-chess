package com.gensokyochess;

import java.util.ArrayList;

public class Main {
  private Chessboard Chessboard = new Chessboard();
  private ArrayList<Piece> Pieces = new ArrayList<>();
  private boolean NoChance;
  private boolean HaveBattleOrSpell;
  private boolean RandPlace = false;
  private Piece RedKing;
  private Piece BlackKing;

  public static void main(String[] args) {
    Main main = new Main();
    main.start();
  }

  protected String getChessboard() {
    return String.valueOf(Chessboard.getChessboard());
  }

  protected void updateChessboard() {
    System.out.println(getChessboard());
  }

  protected void start() {
    Tool.print("是否使用随机布局?(Y/N)");
    if (Tool.input().equalsIgnoreCase("y")) {
      RandPlace = true;
      Tool.print("自动填入棋子:");
    }

    place("red");
    place("black");
    updateChessboard();

    round();
  }

  private void round() {
    String camp = "red";
    int round = 0;
    int count = 0;

    while (true) {
      if (count % 2 != 1) {
        round++;
      }
      count++;
      Tool.print("第 " + round + " 回合");

      NoChance = false;
      HaveBattleOrSpell = false;

      for (int i = 1; i <= 2; i++) {
        if (!action(camp, i)) {
          return;
        }
        NoChance = i == 1 && HaveBattleOrSpell;
      }

      if (camp.equals("red")) {
        camp = "black";
      } else if (camp.equals("black")) {
        camp = "red";
      }
    }
  }

  private boolean action(String camp, int i) {
    boolean inputError;
    do {
      if (camp.equals("red")) {
        Tool.print("红方", false);
      } else {
        Tool.print("黑方", false);
      }
      Tool.print("第 " + i + " 次行动:", false);

      ArrayList action = handleInput(Tool.input());
      inputError = !handleAction(action, camp, i);

      if (!inputError) {
        updateChessboard();
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

    if (tmp.length == 1) {
      action.add(1);

      Piece piece = Tool.findPiece(tmp[0], Pieces);
      if (piece == null) {
        return null;
      } else {
        action.add(piece);
      }
    } else if (tmp.length == 2) {
      action.add(2);

      try {
        int move = Character.getNumericValue(tmp[1]);
        if (move < 1 || move > 9) {
          return null;
        }
        char code = tmp[0];

        action.add(code);
        action.add(move);
      } catch (NumberFormatException e) {
        return null;
      }
    } else if (tmp.length == 3) {
      action.add(3);

      Piece piece1 = Tool.findPiece(tmp[0], Pieces);
      Piece piece2 = Tool.findPiece(tmp[2], Pieces);

      if (piece1 == null || piece2 == null) {
        return null;
      } else {
        action.add(piece1);
        action.add(piece2);
      }
    } else if (tmp.length == 4) {
      action.add(4);

      Piece piece = Tool.findPiece(tmp[2], Pieces);
      String spellCode = String.valueOf(tmp[2]) + String.valueOf(tmp[3]);

      if (piece == null) {
        return null;
      } else {
        action.add(piece);
        action.add(spellCode);
      }
    } else {
      return null;
    }
    return action;
  }

  private boolean handleAction(ArrayList action, String camp, int i) {
    if (action == null) {
      Tool.print("输入有误");
      return false;
    }

    int id = (int) action.get(0);
    if (id == 1) {
      Piece piece = (Piece) action.get(1);
      updateChessboard();
      Tool.print(piece.toString());
      return false;
    } else if (id == 2) {
      char code = (char) action.get(1);
      int move = (int) action.get(2);
      return moveAction(code, move, camp, i);
    } else if (id == 3) {
      HaveBattleOrSpell = true;
      return battleAction(action, camp);
    } else if (id == 4) {
      HaveBattleOrSpell = true;
      return spellAction(action, camp);
    }else {
      return false;
    }
  }

  private void place(String camp) {
    int count = 1;
    int levelCount = 0;
    boolean haveKing = false;
    String place;

    while (levelCount != 15) {
      if (!RandPlace) {
        updateChessboard();
        Tool.print("当前棋子总等级: " + levelCount);
        Tool.print("请布置", false);
        if (camp.equals("red")) {
          Tool.print("红方", false);
        } else {
          Tool.print("黑方", false);
        }
        Tool.print("第 " + count + " 个棋子: ", false);

        place = Tool.input();
      } else {
        place = rollPlace(camp);
      }
      Piece piece = Chessboard.createPiece(place);

      try {
        if (piece != null) {
          levelCount += piece.getLevel();
          if (!piece.getCamp().equals(camp)) {
            Tool.print("请摆在己方区域", true, !RandPlace);
            levelCount -= piece.getLevel();
          } else if (levelCount > 15) {
            Tool.print("总等级要等于 15", true, !RandPlace);
            levelCount -= piece.getLevel();
          } else if (levelCount == 15 && !haveKing) {
            Tool.print("棋盘缺少国王", true, !RandPlace);
            levelCount -= piece.getLevel();
          } else if (Tool.findPiece(piece.getCode(), Pieces) != null) {
            Tool.print("棋盘上已经有了相同棋子", true, !RandPlace);
            levelCount -= piece.getLevel();
          } else {
            piece.placeTo(Chessboard.getChessboard());
            Pieces.add(piece);
            if (piece.isKing()) {
              haveKing = true;
              if (camp.equals("red")) {
                RedKing = piece;
              } else {
                BlackKing = piece;
              }
            }
            count++;
          }
        } else {
          Tool.print("输入有误", true, !RandPlace);
        }
      } catch (CanNotPlaceException e) {
        Tool.print("无法放到该格", true, !RandPlace);
        levelCount -= piece.getLevel();
      }
    }
  }

  private String rollPlace(String camp) {
    int bonus = 0;
    if (camp.equals("black")) {
      bonus = 5;
    }
    return "" + (int) (Math.random() * 9 + 1) +
            "" + (int) (Math.random() * 4 + 1 + bonus) +
            (char) (Math.random() * 94 + 33) +
            (int) (Math.random() * 5 + 1);
  }

  private boolean spellAction(ArrayList action, String camp) {
    Piece piece = (Piece) action.get(1);
    if (!piece.getCamp().equals(camp)) {
      Tool.print("你没有这个棋子");
      return false;
    }
    String spellCode = (String) action.get(2);

    if (spellCode.equals(piece.getSpellCode())) {
      try {
        piece.useSpell(Chessboard, Pieces);
        return true;
      } catch (HaveNotSpellException e) {
        Tool.print("这个棋子没有技能");
        return false;
      }
    } else {
      Tool.print("没有这个技能");
      return false;
    }
  }

  private boolean battleAction(ArrayList action, String camp) {
    Piece piece1 = (Piece) action.get(1);
    if (!piece1.getCamp().equals(camp)) {
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
      Tool.print("黑方胜利");
      return true;
    } else if (!BlackKing.isAlive()) {
      Tool.print("红方胜利");
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
      Tool.print("平局");
      return true;
    }
  }

  private void opportunityBattleAction(char[] haveChanceChars, Piece piece) {
    if (haveChanceChars != null) {
      for (char haveChanceChar : haveChanceChars) {
        if (haveChanceChar != ' ') {
          Piece haveChancePiece = Tool.findPiece(haveChanceChar, Pieces);
          if (haveChancePiece != null && !haveChancePiece.getCamp().equals(piece.getCamp()) && piece.isAlive()) {
            haveChancePiece.opportunityBattleWith(piece, Chessboard.getChessboard());
          }
        }
      }
    }
  }

  private boolean remoteBattleAction(Piece piece1, Piece piece2) {
    try {
      char haveChanceChars[] = piece1.findHaveChanceChar(piece1.getX(), piece1.getY(), Chessboard.getChessboard());
      opportunityBattleAction(haveChanceChars, piece1);
      if (piece1.isAlive()) {
        piece1.remoteBattleWith(piece2, Chessboard.getChessboard());
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
      piece1.frontalBattleWith(piece2, Chessboard.getChessboard());
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

  private boolean moveAction(char code, int move, String camp, int count) {
    Piece piece = Tool.findPiece(camp, code, Pieces);

    if (piece != null) {
      try {
        if (move == 5) {
          Tool.print(piece.getNameAndLV() + " 进行原地防御");
        }
        char haveChanceChars[] = piece.moveTo(move, Chessboard.getChessboard(), count, NoChance);
        opportunityBattleAction(haveChanceChars, piece);
        return true;
      } catch (CanNotPlaceException e) {
        Tool.print("无法放到该格");
        return false;
      } catch (CanNotMoveException | KingMoveException e) {
        Tool.print("超出移动范围");
        return false;
      }
    } else {
      Tool.print("你没有这个棋子");
      return false;
    }
  }
}
