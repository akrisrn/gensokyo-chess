import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  private static final String PLACES[] = {"x1y1a1", "x2y1b1", "x3y1c1", "x4y1d1", "x5y1e1",
          "x6y1f1", "x7y1g1", "x8y1h1", "x9y1i1", "x4y2j1", "x6y2k1",
          "x1y9A1", "x2y9B1", "x3y9C1", "x4y9D1", "x5y9E1",
          "x6y9F1", "x7y9G1", "x8y9H1", "x9y9I1", "x4y8J1", "x6y8K1"};
  private static Chessboard Chessboard = new Chessboard();
  private static ArrayList<Piece> Pieces = new ArrayList<>();
  private static char Loser;
  private static boolean NoChance;
  private static boolean HaveBattle;

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    System.out.println("是否使用默认布局?(Y/N)");
    if (in.nextLine().equalsIgnoreCase("y")) {
      place();
    } else {
      Chessboard.show();
      place(in, "red");
      place(in, "black");
    }
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

  public static void place() {
    Pieces = Chessboard.placePieces(PLACES);
    System.out.println("自动填入棋子:");
    Chessboard.show();
  }

  public static void place(Scanner in, String camp) {
    int count = 1;
    int levelCount = 0;
    boolean haveKing = false;

    while (levelCount != 10) {
      if (camp.equals("red")) {
        System.out.print("红方");
      } else {
        System.out.print("黑方");
      }
      System.out.print("第 " + count + " 个棋子: ");

      String place = in.nextLine();
      Piece piece = Chessboard.createPiece(place);

      try {
        if (piece != null) {
          levelCount += piece.getLevel();
          if (!piece.getCamp().equals(camp)) {
            System.out.println("请摆在己方区域");
            levelCount -= piece.getLevel();
          } else if (levelCount > 10) {
            System.out.println("总等级要等于 10");
            levelCount -= piece.getLevel();
          } else if (levelCount == 10 && !haveKing) {
            System.out.println("棋盘缺少国王");
            levelCount -= piece.getLevel();
          } else if (findPiece(piece.getCode()) != null) {
            System.out.println("棋盘上已经有了相同棋子");
            levelCount -= piece.getLevel();
          } else {
            piece.placeTo(Chessboard.getChessboard());
            if (piece.isKing()) {
              haveKing = true;
            }
            System.out.println("当前棋子总等级: " + levelCount);
            Chessboard.show();
            Pieces.add(piece);
            count++;
          }
        } else {
          System.out.println("输入有误");
        }
      } catch (CanNotPlaceException e) {
        System.out.println("无法放到该格");
        levelCount -= piece.getLevel();
      }
    }
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
    Piece loser = findPiece(Loser);

    if (loser != null) {
      if (loser.getCamp().equals("red") && loser.isKing()) {
        System.out.println("黑方胜利");
        return true;
      } else if (loser.getCamp().equals("black") && loser.isKing()) {
        System.out.println("红方胜利");
        return true;
      }
    }
    return false;
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
      Loser = piece1.remoteBattleWith(piece2, Chessboard.getChessboard());
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
      Loser = piece1.frontalBattleWith(piece2, Chessboard.getChessboard());
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
                  Loser = haveChancePiece.opportunityBattleWith(piece, Chessboard.getChessboard());
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
