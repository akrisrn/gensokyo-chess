package com.gensokyochess;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 工具类
 */
public class Tool {
  private static GuiFrame GuiFrame;
  private static boolean UseGui = false;
  private static StringBuffer Chessboard;
  private static ArrayList<Piece> Pieces;
  private static boolean CurrentCampIsRed = true;
  private final static String Arrows = "↙↓↘←→↖↑↗";
  private static Piece ActivatedPiece;
  private static int RoundCount = 0;
  private static boolean Lock = false;

  /**
   * 给鼠标点击事件上锁
   */
  public static void locked() {
    Lock = true;
  }

  /**
   * 给鼠标点击事件解锁
   */
  public static void unlock() {
    Lock = false;
  }

  public static boolean isLock() {
    return Lock;
  }

  public static int getRoundCount() {
    return RoundCount;
  }

  /**
   * 增加回合数
   */
  public static void CountRound() {
    RoundCount++;
  }

  /**
   * 设置激活的棋子
   *
   * @param piece 被激活的棋子
   */
  public static void setActivatedPiece(Piece piece) {
    ActivatedPiece = piece;
    if (piece == null) {
      GuiFrame.setSpellButtonText(0, "无");
    } else {
      GuiFrame.setSpellButtonText(1, piece.getSpellCode(1));
      if (piece.getTotalSpellNumber() > 1) {
        GuiFrame.setSpellButtonText(2, piece.getSpellCode(2));
      }
    }
  }

  public static Piece getActivatedPiece() {
    return ActivatedPiece;
  }

  /**
   * 设置当前回合的阵营是不是红方
   *
   * @param currentCampIsRed 当前的阵营
   */
  public static void setCurrentCampIsRed(boolean currentCampIsRed) {
    CurrentCampIsRed = currentCampIsRed;
  }

  public static boolean getCurrentCamp() {
    return CurrentCampIsRed;
  }

  public static GuiFrame getGuiFrame() {
    return GuiFrame;
  }

  public static StringBuffer getChessboard() {
    return Chessboard;
  }

  public static void setChessboard(StringBuffer chessboard) {
    Chessboard = chessboard;
  }

  /**
   * 设置是否使用 Gui
   *
   * @param guiFrame Gui 框架
   */
  public static void setGuiFrame(GuiFrame guiFrame) {
    GuiFrame = guiFrame;
    UseGui = true;
  }

  /**
   * 设置棋盘上所有棋子的列表
   *
   * @param pieces 棋子列表
   */
  public static void setPieces(ArrayList<Piece> pieces) {
    Pieces = pieces;
  }

  /**
   * 打印一行
   *
   * @param msg 要打印的内容
   */
  public static void print(String msg) {
    print(msg, true, true, 0);
  }

  /**
   * 打印一行
   *
   * @param msg   要打印的内容
   * @param delay 延时（0：不启用，1：启用）
   */
  public static void print(String msg, int delay) {
    print(msg, true, true, delay);
  }

  /**
   * 打印一行
   *
   * @param msg     要打印的内容
   * @param isALine 结尾是否有换行符
   */
  public static void print(String msg, boolean isALine) {
    print(msg, isALine, true, 0);
  }

  /**
   * 打印一行
   *
   * @param msg     要打印的内容
   * @param isALine 结尾是否有换行符
   * @param showMsg 是否显示
   */
  public static void print(String msg, boolean isALine, boolean showMsg) {
    print(msg, isALine, showMsg, 0);
  }

  /**
   * 打印一行
   *
   * @param msg          要打印的内容
   * @param notShowOnGui 是否不显示在 Gui 上
   * @param delay        延时（0：不启用，1：启用）
   */
  public static void print(String msg, boolean notShowOnGui, int delay) {
    if (!UseGui) {
      print(msg);
    } else if (!notShowOnGui) {
      print(msg, true, false, delay);
    }
  }

  /**
   * 打印一行
   *
   * @param msg     要打印的内容
   * @param isALine 结尾是否有换行符
   * @param showMsg 是否显示
   * @param delay   延时（0：不启用，1：启用）
   */
  public static void print(String msg, boolean isALine, boolean showMsg, int delay) {
    if (!UseGui) {
      if (isALine && showMsg) {
        System.out.println(msg);
      } else if (showMsg) {
        System.out.print(msg);
      }
    } else {
      if (showMsg) {
        GuiFrame.appendLog(msg, isALine);
      }
    }
    if (delay != 0) {
      try {
        Thread.sleep(1200);
      } catch (InterruptedException ignored) {
      }
    }
  }

  /**
   * 输入一行
   *
   * @return 输入值 string
   */
  public static String input() {
    if (!UseGui) {
      Scanner in = new Scanner(System.in);
      return in.nextLine();
    } else {
      String input;
      while (true) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        input = uncheckedInput();
        if (input != null) {
          break;
        }
      }
      return input;
    }
  }

  /**
   * 输入尚未检查的一行
   *
   * @return 未检查的输入值 string
   */
  private static String uncheckedInput() {
    try {
      Scanner in = GuiFrame.getScanner();
      return in.nextLine();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  /**
   * 寻找一条直线上两个格子之间的棋子
   *
   * @param isXAxis     是否在 x 轴
   * @param commonValue 共同的值（x 轴共同的是 y 坐标， y 轴共同的是 x 坐标）
   * @param startXOrY   起始格子的 x 或 y 坐标
   * @param overXOrY    结束格子的 x 或 y 坐标
   * @return 找到的棋子列表 ，没找到则返回 null
   */
  public static ArrayList<Piece> findPieces(boolean isXAxis, int commonValue, int startXOrY, int overXOrY) {
    ArrayList<Piece> pieces = new ArrayList<>();
    int reversal = 1;
    if (startXOrY > overXOrY) {
      reversal = -1;
    }

    char aimChar;
    int index;
    for (int i = startXOrY + reversal; i * reversal < overXOrY * reversal; i += reversal) {
      if (isXAxis) {
        index = convertXY2Index(i, commonValue);
        aimChar = Chessboard.charAt(index);
      } else {
        index = convertXY2Index(commonValue, i);
        aimChar = Chessboard.charAt(index);
      }

      Piece piece;
      if (aimChar == '*') {
        piece = findSpecialPiece(index, '*');
      } else if (aimChar == '|') {
        piece = findSpecialPiece(index, '|');
      } else {
        piece = findPiece(aimChar);
      }
      if (piece != null) {
        pieces.add(piece);
      }
    }
    return pieces;
  }

  /**
   * 寻找特殊的棋子（* 或 |）
   *
   * @param index 棋盘上的索引
   * @param code  目标棋子代码
   * @return 如果找到了棋子且在索引位置上 ，返回这个棋子，没找到或不在索引位置上则返回 null
   */
  public static Piece findSpecialPiece(int index, char code) {
    Piece piece = findPiece(code);
    if (piece != null && (index == convertXY2Index(piece.getX(), piece.getY()))) {
      return piece;
    }
    else {
      return null;
    }
  }

  /**
   * 寻找棋子
   *
   * @param x x 坐标
   * @param y y 坐标
   * @return 找到的棋子 ，没找到则返回 null
   */
  public static Piece findPiece(int x, int y) {
    return findPiece(Chessboard.charAt(convertXY2Index(x, y)));
  }

  /**
   * 寻找棋子
   *
   * @param code 棋子代码
   * @return 找到的棋子 ，没找到则返回 null
   */
  public static Piece findPiece(char code) {
    for (Piece piece : Pieces) {
      if (piece.getCode() == code) {
        return piece;
      }
    }
    return null;
  }

  /**
   * 寻找棋子
   *
   * @param camp 阵营
   * @param code 棋子代码
   * @return the piece
   */
  public static Piece findPiece(boolean camp, char code) {
    for (Piece piece : Pieces) {
      if (piece.getCamp() == camp) {
        if (piece.getCode() == code) {
          return piece;
        }
      }
    }
    return null;
  }

  /**
   * 转换格子坐标为棋盘上的索引
   *
   * @param x x 坐标
   * @param y y 坐标
   * @return 索引值 int
   */
  public static int convertXY2Index(int x, int y) {
    if (x < 1 || y < 1 || x > 9 || y > 9) {
      return 0;
    } else {
      return 648 - (y - 1) * 76 + (x - 1) * 4;
    }
  }

  /**
   * 判断移动的方向
   *
   * @param aimX 要移动格子的 x 坐标
   * @param aimY 要移动格子的 y 坐标
   * @param curX 当前格子的 x 坐标
   * @param curY 当前格子的 y 坐标
   * @return 方向值 int
   */
  public static int decideDirection(int aimX, int aimY, int curX, int curY) {
    if (aimX > curX) {
      if (aimY > curY) {
        return 9;
      } else if (aimY < curY) {
        return 3;
      } else {
        return 6;
      }
    } else if (aimX < curX) {
      if (aimY > curY) {
        return 7;
      } else if (aimY < curY) {
        return 1;
      } else {
        return 4;
      }
    } else {
      if (aimY > curY) {
        return 8;
      } else if (aimY < curY) {
        return 2;
      } else {
        return 5;
      }
    }
  }

  /**
   * 刷新棋盘
   */
  public static void updateChessboard() {
    if (!UseGui) {
      System.out.println(Chessboard);
    } else {
      GuiFrame.updateChessboard(String.valueOf(Chessboard));
    }
  }

  /**
   * 更新回合信息
   */
  public static void updateRoundMsg() {
    String msg = "第 " + RoundCount + " 回合";
    if (!UseGui) {
      System.out.println(msg);
    } else {
      GuiFrame.updateRoundLabel(msg);
    }
  }

  /**
   * 更新行动信息
   *
   * @param count 第几次行动（为 0 时表示游戏结束）
   * @param over  结束（0：平局， 1：红胜，-1：黑胜）
   */
  public static void updateActionMsg(int count, int over) {
    String msg;
    if (count == 0) {
      switch (over) {
        case 1:
          msg = "红方胜利";
          break;
        case -1:
          msg = "黑方胜利";
          break;
        default:
          msg = "平局";
          break;
      }
    } else {
      String camp;
      if (CurrentCampIsRed) {
        camp = "红方";
      } else {
        camp = "黑方";
      }
      msg = camp + "第 " + count + " 次行动:";
    }
    if (!UseGui) {
      System.out.println(msg);
    } else {
      GuiFrame.updateActionLabel(msg);
    }
  }

  /**
   * 在棋子周围画出箭头
   *
   * @param piece       要画出箭头的棋子
   * @param isAllArrows 是否画出所有的箭头
   */
  public static void drawArrows(Piece piece, boolean isAllArrows) {
    int x = piece.getX();
    int y = piece.getY();

    ArrayList<Integer> directions = markDirection(x, y);
    int k = 0;
    for (int j = y - 1; j <= y + 1; j++) {
      for (int i = x - 1; i <= x + 1; i++) {
        if (!(i == x && j == y)) {
          for (int direction : directions) {
            if (Tool.decideDirection(i, j, x, y) == direction) {
              Tool.getChessboard().setCharAt(Tool.convertXY2Index(i, j), Arrows.charAt(k));
            }
          }
          k++;
        }
      }
    }
    if (!isAllArrows) {
      eraseArrows(false);
    }
    Tool.updateChessboard();
  }

  /**
   * 标记指定坐标周围可以画上箭头的方向
   *
   * @param x x 坐标
   * @param y y 坐标
   * @return 方向列表 array list
   */
  private static ArrayList<Integer> markDirection(int x, int y) {
    ArrayList<Integer> directions = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (!(i == x && j == y)) {
          int index = Tool.convertXY2Index(i, j);
          if (index != 0) {
            int direction = Tool.decideDirection(i, j, x, y);
            char aimChar = Tool.getChessboard().charAt(index);
            if (aimChar == ' ') {
              directions.add(direction);
            } else if (aimChar == '*' && Tool.findSpecialPiece(index, '*') == null) {
              directions.add(direction);
            } else if (aimChar == '|' && Tool.findSpecialPiece(index, '|') == null) {
              directions.add(direction);
            }
          }
        }
      }
    }
    return directions;
  }

  /**
   * 擦除箭头
   */
  public static void eraseArrows() {
    eraseArrows(true);
  }

  /**
   * 擦除箭头
   *
   * @param isAll 是否擦除所有箭头
   */
  public static void eraseArrows(boolean isAll) {
    String arrows;
    int max;
    if (isAll) {
      arrows = Arrows;
      max = 8;
    } else {
      arrows = "↙↘↖↗";
      max = 4;
    }
    for (int i = 0; i < max; i++) {
      int index = Tool.getChessboard().indexOf(String.valueOf(arrows.charAt(i)));
      if (index != -1) {
        if (index == Tool.convertXY2Index(2, 5) || index == Tool.convertXY2Index(5, 5) ||
                index == Tool.convertXY2Index(8, 5)) {
          Tool.getChessboard().setCharAt(index, '|');
        } else if (index == Tool.convertXY2Index(1, 5) || index == Tool.convertXY2Index(3, 5) ||
                index == Tool.convertXY2Index(4, 5) || index == Tool.convertXY2Index(6, 5) ||
                index == Tool.convertXY2Index(7, 5) || index == Tool.convertXY2Index(9, 5)) {
          Tool.getChessboard().setCharAt(index, '*');
        } else {
          Tool.getChessboard().setCharAt(index, ' ');
        }
      }
    }
    Tool.updateChessboard();
  }

  /**
   * 根据箭头获取移动的方向
   *
   * @param arrow 指定的箭头
   * @return 移动的方向 ，没找到箭头则返回 0
   */
  public static int getMove(char arrow) {
    int arrowIndex = Arrows.indexOf(arrow);
    if (arrowIndex != -1) {
      return 5 * arrowIndex / 4 + 1;
    } else {
      return 0;
    }
  }

  /**
   * 获取指定 csv 文件的读取器
   *
   * @param name 文件名
   * @return 读取器 ，没找到文件则返回 null
   */
  public static CsvReader getCsvReader(String name) {
    String path = System.getProperty("user.dir") + name;
    try {
      return new CsvReader(path, ',', Charset.forName("utf-8"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 根据放置指令创建一个棋子
   *
   * @param place 放置指令
   * @return 创建的棋子 ，如果输入有误则返回 null
   */
  public static Piece createPiece(String place) {
    String tmp[] = place.split("");
    if (tmp.length != 4) {
      return null;
    }
    try {
      return new Piece(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),
              tmp[2].charAt(0), Integer.parseInt(tmp[3]));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * 转换移动的方向为格子坐标
   *
   * @param x    x 坐标
   * @param y    y 坐标
   * @param move 移动方向
   * @return 要移动格子的坐标 ，如果输入有误则返回 null
   */
  public static int[] convertMove2XY(int x, int y, int move) {
    switch (move) {
      case 1:
        return new int[]{x - 1, y - 1};
      case 2:
        return new int[]{x, y - 1};
      case 3:
        return new int[]{x + 1, y - 1};
      case 4:
        return new int[]{x - 1, y};
      case 6:
        return new int[]{x + 1, y};
      case 7:
        return new int[]{x - 1, y + 1};
      case 8:
        return new int[]{x, y + 1};
      case 9:
        return new int[]{x + 1, y + 1};
      default:
        return null;
    }
  }
}
