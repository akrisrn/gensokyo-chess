package com.gensokyochess;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  private Chessboard Chessboard = new Chessboard();
  private ArrayList<Piece> Pieces = new ArrayList<>();
  private boolean NoChance;
  private boolean HaveBattle;
  private boolean RandPlace = false;
  private Piece RedKing;
  private Piece BlackKing;

  public static void main(String[] args) {
    Main main = new Main();
    main.start();
  }

  protected String input() {
    Scanner in = new Scanner(System.in);
    return in.nextLine();
  }

  private void print(String msg) {
    print(msg, true);
  }

  private void print(String msg, boolean showMsg) {
    print(msg, showMsg, true);
  }

  protected void print(String msg, boolean showMsg, boolean isALine) {
    if (isALine && showMsg) {
      System.out.println(msg);
    } else if (showMsg) {
      System.out.print(msg);
    }
  }

  protected String getChessboard() {
    return String.valueOf(Chessboard.getChessboard());
  }

  protected void updateChessboard() {
    System.out.println(getChessboard());
  }

  protected void start() {
    print("是否使用随机布局?(Y/N)");
    if (input().equalsIgnoreCase("y")) {
      RandPlace = true;
      print("自动填入棋子:");
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
      print("第 " + round + " 回合");

      NoChance = false;
      HaveBattle = false;

      for (int i = 1; i <= 2; i++) {
        if (!action(camp, i)) {
          return;
        }
        NoChance = i == 1 && HaveBattle;
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
        print("红方", true, false);
      } else {
        print("黑方", true, false);
      }
      print("第 " + i + " 次行动:", true, false);

      ArrayList action = handleInput(input());
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

      Piece piece = findPiece(tmp[0]);
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

      Piece piece1 = findPiece(tmp[0]);
      Piece piece2 = findPiece(tmp[2]);

      if (piece1 == null || piece2 == null) {
        return null;
      } else {
        action.add(piece1);
        action.add(piece2);
      }
    } else {
      return null;
    }
    return action;
  }

  private boolean handleAction(ArrayList action, String camp, int i) {
    if (action == null) {
      print("输入有误");
      return false;
    }

    int id = (int) action.get(0);
    if (id == 1) {
      Piece piece = (Piece) action.get(1);
      updateChessboard();
      print(piece.toString());
      return false;
    } else if (id == 2) {
      char code = (char) action.get(1);
      int move = (int) action.get(2);
      return moveAction(code, move, camp, i);
    } else if (id == 3) {
      HaveBattle = true;
      return battleAction(action, camp);
    } else {
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
        print("当前棋子总等级: " + levelCount);
        print("请布置", true, false);
        if (camp.equals("red")) {
          print("红方", true, false);
        } else {
          print("黑方", true, false);
        }
        print("第 " + count + " 个棋子: ", true, false);

        place = input();
      } else {
        place = rollPlace(camp);
      }
      Piece piece = Chessboard.createPiece(place);

      try {
        if (piece != null) {
          levelCount += piece.getLevel();
          if (!piece.getCamp().equals(camp)) {
            print("请摆在己方区域", !RandPlace);
            levelCount -= piece.getLevel();
          } else if (levelCount > 15) {
            print("总等级要等于 15", !RandPlace);
            levelCount -= piece.getLevel();
          } else if (levelCount == 15 && !haveKing) {
            print("棋盘缺少国王", !RandPlace);
            levelCount -= piece.getLevel();
          } else if (findPiece(piece.getCode()) != null) {
            print("棋盘上已经有了相同棋子", !RandPlace);
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
          print("输入有误", !RandPlace);
        }
      } catch (CanNotPlaceException e) {
        print("无法放到该格", !RandPlace);
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

  private boolean battleAction(ArrayList action, String camp) {
    Piece piece1 = (Piece) action.get(1);
    if (!piece1.getCamp().equals(camp)) {
      print("你没有这个棋子");
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
      print("黑方胜利");
      return true;
    } else if (!BlackKing.isAlive()) {
      print("红方胜利");
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
      print("平局");
      return true;
    }
  }

  private Piece findPiece(char code) {
    for (Piece piece : Pieces) {
      if (piece.getCode() == code) {
        return piece;
      }
    }
    return null;
  }

  private Piece findPiece(String camp, char code) {
    for (Piece piece : Pieces) {
      if (piece.getCamp().equals(camp)) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }

  private void opportunityBattleAction(char[] haveChanceChars, Piece piece) {
    if (haveChanceChars != null) {
      for (char haveChanceChar : haveChanceChars) {
        if (haveChanceChar != ' ') {
          Piece haveChancePiece = findPiece(haveChanceChar);
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
      print("超出攻击范围");
      return false;
    } catch (HaveObstacleException e) {
      print("中间有障碍");
      return false;
    } catch (SameCampException e) {
      print("这是己方棋子");
      return false;
    } catch (InRiverException e) {
      print("在河流中无法进行攻击");
      return false;
    } catch (KingSpellException e) {
      print("国王不受来自对面区域的攻击");
      return false;
    }
  }

  private boolean frontalBattleAction(Piece piece1, Piece piece2) {
    try {
      piece1.frontalBattleWith(piece2, Chessboard.getChessboard());
      return true;
    } catch (SameCampException e) {
      print("这是己方棋子");
      return false;
    } catch (ExceedAttackRangeException e) {
      print("超出攻击范围");
      return false;
    } catch (InRiverException e) {
      print("在河流中无法进行攻击");
      return false;
    }
  }

  private boolean moveAction(char code, int move, String camp, int count) {
    Piece piece = findPiece(camp, code);

    if (piece != null) {
      try {
        if (move == 5) {
          print(piece.getNameAndLV() + " 进行原地防御");
        }
        char haveChanceChars[] = piece.moveTo(move, Chessboard.getChessboard(), count, NoChance);
        opportunityBattleAction(haveChanceChars, piece);
        return true;
      } catch (CanNotPlaceException e) {
        print("无法放到该格");
        return false;
      } catch (CanNotMoveException | KingMoveException e) {
        print("超出移动范围");
        return false;
      }
    } else {
      print("你没有这个棋子");
      return false;
    }
  }
}
