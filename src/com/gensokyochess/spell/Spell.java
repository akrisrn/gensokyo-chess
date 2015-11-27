package com.gensokyochess.spell;

import com.csvreader.CsvReader;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

  public static Spell choiceSpell(String code) {
    return SpellsMap.get(code);
  }

  public Spell(String code) {
    Code = code;
  }

  public abstract boolean use(Piece piece1) throws KingSpellException, SameCampException;

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

  public void start(Piece piece, int i) {
    Tool.locked();
    Tool.print(piece.getNameAndLv() + " 使用了 " + piece.getSpellName(i) + "!", 1);
  }

  public void start(Piece piece1, Piece piece2, int i) throws KingSpellException {
    Tool.locked();
    if (piece2.isKing()) {
      Tool.unlock();
      throw new KingSpellException();
    }
    Tool.print(piece1.getNameAndLv() + " 使用了 " + piece1.getSpellName(i) + "!", 1);
  }

  public boolean over() {
    Tool.setActivatedPiece(null);
    Tool.print("技能结束", 1);
    Tool.unlock();
    return true;
  }

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
