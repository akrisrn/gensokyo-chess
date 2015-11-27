package com.gensokyochess.spell;

import com.csvreader.CsvReader;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 技能类
 */
public abstract class Spell {
  private String Code;
  private String Name;
  private String Description;
  private static HashMap<String, Spell> SpellsMap = new HashMap<>();

  static {
    ArrayList<Spell> spells = new ArrayList<>();
    spells.add(new EvilSealingCircle());
    spells.add(new FantasySeal());
    spells.add(new MasterSpark());
    spells.add(new BlazingStar());
    spells.add(new Darkness());

    for (Spell spell : spells) {
      SpellsMap.put(spell.getCode(), spell);
    }
  }

  static {
    CsvReader reader = Tool.getCsvReader("/lib/spell.csv");
    if (reader == null) {
      System.exit(1);
    }

    try {
      reader.readHeaders();
      while (reader.readRecord()) {
        Spell spell = SpellsMap.get(reader.get("Code"));
        if (spell != null) {
          spell.setName(reader.get("Name"));
          spell.setDescription(reader.get("Description"));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      reader.close();
    }
  }

  /**
   * 实例化技能
   *
   * @param code 技能代码
   */
  public Spell(String code) {
    Code = code;
  }

  /**
   * 选择一个技能
   *
   * @param code 技能代码
   * @return 技能
   */
  public static Spell choiceSpell(String code) {
    return SpellsMap.get(code);
  }

  /**
   * 使用技能
   *
   * @param piece1 使用技能的棋子
   * @return 是否使用成功
   * @throws KingSpellException 国王技能
   * @throws SameCampException  相同阵营
   */
  public abstract boolean use(Piece piece1) throws KingSpellException, SameCampException;

  /**
   * 选择一个棋子
   *
   * @return 棋子
   */
  public Piece choicePiece() {
    Tool.print("请选择一个棋子");
    Tool.eraseArrows();

    String input = Tool.input();
    Piece piece;
    if (input.length() == 3) {
      piece = Tool.findPiece(input.charAt(2));
    } else {
      piece = Tool.findPiece(input.charAt(0));
    }
    return piece;
  }

  /**
   * 选择一个方向
   *
   * @param piece       要选择方向的棋子
   * @param isAllArrows 是否画出所有的箭头
   * @return 方向值
   */
  public int choiceDirection(Piece piece, boolean isAllArrows) {
    Tool.print("请选择一个方向");
    Tool.eraseArrows();
    Tool.drawArrows(piece, isAllArrows);

    String input = Tool.input();
    Tool.eraseArrows();
    if (input.equals(piece.getCode() + "1") || input.equals("1")) {
      return 1;
    } else if (input.equals(piece.getCode() + "2") || input.equals("2")) {
      return 2;
    } else if (input.equals(piece.getCode() + "3") || input.equals("3")) {
      return 3;
    } else if (input.equals(piece.getCode() + "4") || input.equals("4")) {
      return 4;
    } else if (input.equals(piece.getCode() + "6") || input.equals("6")) {
      return 6;
    } else if (input.equals(piece.getCode() + "7") || input.equals("7")) {
      return 7;
    } else if (input.equals(piece.getCode() + "8") || input.equals("8")) {
      return 8;
    }else if (input.equals(piece.getCode() + "9") || input.equals("9")) {
      return 9;
    } else {
      return 0;
    }
  }

  /**
   * 技能开始
   *
   * @param piece 使用技能的棋子
   * @param num   技能编号
   */
  public void start(Piece piece, int num) {
    Tool.locked();
    Tool.print(piece.getNameAndLv() + " 使用了 " + piece.getSpellName(num) + "!", 1);
  }

  /**
   * 技能开始
   *
   * @param piece1 使用技能的棋子
   * @param piece2 技能目标的棋子
   * @param num    技能编号
   * @throws KingSpellException 国王技能
   */
  public void start(Piece piece1, Piece piece2, int num) throws KingSpellException {
    Tool.locked();
    if (piece2.isKing()) {
      Tool.unlock();
      throw new KingSpellException();
    }
    Tool.print(piece1.getNameAndLv() + " 使用了 " + piece1.getSpellName(num) + "!", 1);
  }

  /**
   * 技能结束
   *
   * @return true
   */
  public boolean over() {
    Tool.setActivatedPiece(null);
    Tool.print("技能结束", 1);
    Tool.unlock();
    return true;
  }

  /**
   * 技能错误
   *
   * @param piece 使用技能的棋子
   * @param type  错误类型（0：无，1：相同阵营，2：国王技能）
   * @return false
   */
  public boolean error(Piece piece, int type) {
    if (type == 1) {
      Tool.print("这是己方棋子");
    } else if (type == 2) {
      Tool.print("国王不受技能影响");
    }
    Tool.setActivatedPiece(piece);
    Tool.unlock();
    Tool.print("输入有误");
    return false;
  }

  public String getCode() {
    return Code;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String description) {
    Description = description;
  }

  @Override
  public String toString() {
    return getName() + "(" + getCode() + "): " + getDescription();
  }
}
