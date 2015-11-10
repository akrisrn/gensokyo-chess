import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  private static final String PLACE = "x1y1a x2y1b x3y1c x4y1d x5y1e x6y1f x7y1g x8y1h x9y1i x4y2j x6y2k " +
          "x1y9A x2y9B x3y9C x4y9D x5y9E x6y9F x7y9G x8y9H x9y9I x4y8J x6y8K";
  private static ArrayList<Piece> Pieces;
  private static char Loser;

  public static void main(String[] args) {
    Chessboard chessboard = new Chessboard();
    Scanner in = new Scanner(System.in);
    try {
      System.out.println("自动填入棋子:");
      Pieces = chessboard.placePieces(PLACE);
      chessboard.show();
    } catch (CanNotPlaceException e) {
      System.out.println("无法放到该格");
      System.exit(1);
    }

    String camp = "red";
    int round = 0;
    int count = 0;
    while (true) {
      if (count % 2 != 1) {
        round++;
      }
      count++;
      System.out.println("第 " + round + " 回合");
      boolean inputError = false;
      boolean haveBattle = false;
      boolean noChance = false;
      for (int i = 1; i <= 2; i++) {
        do {
          if (camp.equals("red")) {
            System.out.print("红方");
          } else {
            System.out.print("黑方");
          }
          System.out.print("第 " + i + " 次行动:");

          ArrayList acton = handleInput(in.nextLine());
          if (acton == null) {
            System.out.println("输入有误");
            inputError = true;
            continue;
          }

          if ((int) acton.get(0) == 3) {
            int x = (int) acton.get(1);
            int y = (int) acton.get(2);
            char p = (char) acton.get(3);
            if (!moveAction(x, y, p, camp, chessboard.getChessboard(), i, noChance)) {
              inputError = true;
            } else {
              inputError = false;
              chessboard.show();
            }
          } else if ((int) acton.get(0) == 2) {
            haveBattle = true;
            Piece piece1 = (Piece) acton.get(1);
            Piece piece2 = (Piece) acton.get(2);
            if (piece1.getAttackType() == 0) {
              if (!frontalBattleAction(piece1, piece2, chessboard.getChessboard())) {
                inputError = true;
              } else {
                inputError = false;
                chessboard.show();
              }
            } else {
              if (!remoteBattleAction(piece1, piece2, chessboard.getChessboard())) {
                inputError = true;
              } else {
                inputError = false;
                chessboard.show();
              }
            }
            if (Loser == 'e') {
              System.out.println("黑方胜利");
              System.exit(0);
            } else if (Loser == 'E') {
              System.out.println("红方胜利");
              System.exit(0);
            }
          }
        } while (inputError);

        if (i == 1 && haveBattle) {
          noChance = true;
        }
      }

      if (camp.equals("red")) {
        camp = "black";
      } else if (camp.equals("black")) {
        camp = "red";
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static ArrayList handleInput(String in) {
    ArrayList action = new ArrayList();
    String tmp[] = in.split("");

    if (tmp.length == 3) {
      action.add(2);
      Piece piece1 = findPiece(tmp[0].charAt(0));
      Piece piece2 = findPiece(tmp[2].charAt(0));
      if (piece1 == null || piece2 == null) {
        return null;
      } else {
        action.add(piece1);
        action.add(piece2);
      }
    } else if (tmp.length == 5) {
      action.add(3);
      try {
        int x = Integer.parseInt(tmp[1]);
        int y = Integer.parseInt(tmp[3]);
        char p = tmp[4].charAt(0);
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

  public static Piece findPiece(char p) {
    for (Piece piece : Pieces) {
      if (piece.getP() == p) {
        return piece;
      }
    }
    return null;
  }

  public static Piece findPiece(String camp, char p) {
    for (Piece piece : Pieces) {
      if (piece.getCamp().equals(camp)) {
        if (piece.getP() == p) {
          return piece;
        }
      }
    }
    return null;
  }

  public static boolean remoteBattleAction(Piece piece1, Piece piece2, StringBuffer chessboard) {
    try {
      Loser = piece1.remoteBattleWith(piece2, chessboard);
      return true;
    } catch (ExceedAttackRangeException e) {
      System.out.println("超出攻击范围");
      return false;
    } catch (haveObstacleException e) {
      System.out.println("中间有障碍");
      return false;
    } catch (SameCampException e) {
      System.out.println("这是己方棋子");
      return false;
    }
  }

  public static boolean frontalBattleAction(Piece piece1, Piece piece2, StringBuffer chessboard) {
    try {
      Loser = piece1.frontalBattleWith(piece2, chessboard);
      return true;
    } catch (SameCampException e) {
      System.out.println("这是己方棋子");
      return false;
    } catch (ExceedAttackRangeException e) {
      System.out.println("超出攻击范围");
      return false;
    }
  }

  public static boolean moveAction(int x, int y, char p, String camp, StringBuffer chessboard, int count, boolean noChance) {
    Piece piece = findPiece(camp, p);

    if (piece != null) {
      try {
        char haveChanceChars[] = piece.moveTo(x, y, chessboard, count, noChance);
        if (haveChanceChars != null) {
          for (char haveChanceChar : haveChanceChars) {
            if (haveChanceChar != ' ') {
              Piece haveChancePiece = findPiece(haveChanceChar);
              System.out.println(haveChanceChar);
              if (haveChancePiece != null) {
                if (!haveChancePiece.getCamp().equals(piece.getCamp())) {
                  haveChancePiece.opportunityBattleWith(piece, chessboard);
                }
              }
            }
          }
        }
        return true;
      } catch (CanNotPlaceException e) {
        System.out.println("无法放到该格");
        return false;
      } catch (CanNotMoveException e) {
        System.out.println("超出移动范围");
        return false;
      }
    } else {
      System.out.println("你没有这个棋子");
      return false;
    }
  }
}
