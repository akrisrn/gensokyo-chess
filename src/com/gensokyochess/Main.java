package com.gensokyochess;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  private final static int MAX_LEVEL = 15;
  private static Chessboard Chessboard = new Chessboard();
  private static ArrayList<Piece> Pieces = new ArrayList<>();
  private static boolean NoChance;
  private static boolean HaveBattle;
  private static boolean RandPlace = false;
  private static Piece RedKing;
  private static Piece BlackKing;

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    System.out.println("是否使用随机布局?(Y/N)");
    if (in.nextLine().equalsIgnoreCase("y")) {
      RandPlace = true;
      System.out.println("自动填入棋子:");
    }

    place(in, "red");
    place(in, "black");
    Chessboard.show();

    round(in);
  }

  public static void round(Scanner in) {
    String camp = "red";
    int round = 0;
    int count = 0;

    while (true) {
      if (count % 2 != 1) {
        round++;
      }
      count++;
      System.out.println("第 " + round + " 回合");

      NoChance = false;
      HaveBattle = false;

      for (int i = 1; i <= 2; i++) {
        if (!action(in, camp, i)) {
          System.exit(0);
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

  public static boolean action(Scanner in, String camp, int i) {
    boolean inputError;
    do {
      if (camp.equals("red")) {
        System.out.print("红方");
      } else {
        System.out.print("黑方");
      }
      System.out.print("第 " + i + " 次行动:");

      ArrayList action = handleInput(in.nextLine());
      inputError = !handleAction(action, camp, i);

      if (!inputError) {
        Chessboard.show();
        if (isGameOver()) {
          return false;
        }
      }
    } while (inputError);
    return true;
  }

  public static boolean handleAction(ArrayList action, String camp, int i) {
    if (action == null) {
      System.out.println("输入有误");
      return false;
    }

    int id = (int) action.get(0);
    if (id == 3) {
      int x = (int) action.get(1);
      int y = (int) action.get(2);
      char code = (char) action.get(3);
      return moveAction(x, y, code, camp, i);
    } else if (id == 2) {
      HaveBattle = true;
      return battleAction(action, camp);
    } else {
      Piece piece = (Piece) action.get(1);
      Chessboard.show();
      System.out.println(piece);
      return false;
    }
  }

  public static void place(Scanner in, String camp) {
    int count = 1;
    int levelCount = 0;
    boolean haveKing = false;
    String place;

    while (levelCount != MAX_LEVEL) {
      if (!RandPlace) {
        Chessboard.show();
        System.out.println("当前棋子总等级: " + levelCount);
        System.out.print("请布置");
        if (camp.equals("red")) {
          System.out.print("红方");
        } else {
          System.out.print("黑方");
        }
        System.out.print("第 " + count + " 个棋子: ");
        place = in.nextLine();
      } else {
        place = rollPlace(camp);
      }
      Piece piece = Chessboard.createPiece(place);

      try {
        if (piece != null) {
          levelCount += piece.getLevel();
          if (!piece.getCamp().equals(camp)) {
            controllablePrint("请摆在己方区域");
            levelCount -= piece.getLevel();
          } else if (levelCount > MAX_LEVEL) {
            controllablePrint("总等级要等于 " + MAX_LEVEL);
            levelCount -= piece.getLevel();
          } else if (levelCount == MAX_LEVEL && !haveKing) {
            controllablePrint("棋盘缺少国王");
            levelCount -= piece.getLevel();
          } else if (findPiece(piece.getCode()) != null) {
            controllablePrint("棋盘上已经有了相同棋子");
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
          controllablePrint("输入有误");
        }
      } catch (CanNotPlaceException e) {
        controllablePrint("无法放到该格");
        levelCount -= piece.getLevel();
      }
    }
  }

  public static void controllablePrint(String msg) {
    if (!RandPlace) {
      System.out.println(msg);
    }
  }

  public static String rollPlace(String camp) {
    int bonus = 0;
    if (camp.equals("black")) {
      bonus = 5;
    }
    return "x" + (int) (Math.random() * 9 + 1) +
            "y" + (int) (Math.random() * 4 + 1 + bonus) +
            (char) (Math.random() * 94 + 33) +
            (int) (Math.random() * 5 + 1);
  }

  public static boolean battleAction(ArrayList action, String camp) {
    Piece piece1 = (Piece) action.get(1);
    if (!piece1.getCamp().equals(camp)) {
      System.out.println("你没有这个棋子");
      return false;
    }
    Piece piece2 = (Piece) action.get(2);

    if (piece1.getAttackType() == 0) {
      return frontalBattleAction(piece1, piece2);
    } else {
      return remoteBattleAction(piece1, piece2);
    }
  }

  public static boolean isGameOver() {
    if (!RedKing.isAlive()) {
      System.out.println("黑方胜利");
      return true;
    } else if (!BlackKing.isAlive()) {
      System.out.println("红方胜利");
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
      System.out.println("平局");
      return true;
    }
  }

  @SuppressWarnings("unchecked")
  public static ArrayList handleInput(String in) {
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
    } else if (tmp.length == 3) {
      action.add(2);

      Piece piece1 = findPiece(tmp[0]);
      Piece piece2 = findPiece(tmp[2]);

      if (piece1 == null || piece2 == null) {
        return null;
      } else {
        action.add(piece1);
        action.add(piece2);
      }
    } else if (tmp.length == 5) {
      action.add(3);

      try {
        int x = Character.getNumericValue(tmp[1]);
        int y = Character.getNumericValue(tmp[3]);
        char p = tmp[4];

        action.add(x);
        action.add(y);
        action.add(p);
      } catch (NumberFormatException e) {
        return null;
      }
    } else {
      return null;
    }
    return action;
  }

  public static Piece findPiece(char code) {
    for (Piece piece : Pieces) {
      if (piece.getCode() == code) {
        return piece;
      }
    }
    return null;
  }

  public static Piece findPiece(String camp, char code) {
    for (Piece piece : Pieces) {
      if (piece.getCamp().equals(camp)) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }

  public static boolean remoteBattleAction(Piece piece1, Piece piece2) {
    try {
      piece1.remoteBattleWith(piece2, Chessboard.getChessboard());
      return true;
    } catch (ExceedAttackRangeException e) {
      System.out.println("超出攻击范围");
      return false;
    } catch (HaveObstacleException e) {
      System.out.println("中间有障碍");
      return false;
    } catch (SameCampException e) {
      System.out.println("这是己方棋子");
      return false;
    } catch (InRiverException e) {
      System.out.println("在河流中无法进行攻击");
      return false;
    } catch (KingSpellException e) {
      System.out.println("国王不受来自对面区域的攻击");
      return false;
    }
  }

  public static boolean frontalBattleAction(Piece piece1, Piece piece2) {
    try {
      piece1.frontalBattleWith(piece2, Chessboard.getChessboard());
      return true;
    } catch (SameCampException e) {
      System.out.println("这是己方棋子");
      return false;
    } catch (ExceedAttackRangeException e) {
      System.out.println("超出攻击范围");
      return false;
    } catch (InRiverException e) {
      System.out.println("在河流中无法进行攻击");
      return false;
    }
  }

  public static boolean moveAction(int x, int y, char code, String camp, int count) {
    Piece piece = findPiece(camp, code);

    if (piece != null) {
      try {
        char haveChanceChars[] = piece.moveTo(x, y, Chessboard.getChessboard(), count, NoChance);
        if (haveChanceChars != null) {
          for (char haveChanceChar : haveChanceChars) {
            if (haveChanceChar != ' ') {
              Piece haveChancePiece = findPiece(haveChanceChar);
              if (haveChancePiece != null) {
                if (!haveChancePiece.getCamp().equals(piece.getCamp())) {
                  haveChancePiece.opportunityBattleWith(piece, Chessboard.getChessboard());
                }
              }
            }
          }
        }
        return true;
      } catch (CanNotPlaceException e) {
        System.out.println("无法放到该格");
        return false;
      } catch (CanNotMoveException | KingMoveException e) {
        System.out.println("超出移动范围");
        return false;
      }
    } else {
      System.out.println("你没有这个棋子");
      return false;
    }
  }
}
