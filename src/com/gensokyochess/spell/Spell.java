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

  public static HashMap<String, Spell> getSpellsMap() {
    return SpellsMap;
  }

  public static Spell choiceSpell(String code) {
    return SpellsMap.get(code);
  }

  public Spell(String code) {
    Code = code;
  }

  public abstract boolean use(Piece piece1) throws KingSpellException, SameCampException;

  public Piece choiceAPiece() {
    Tool.print("请选择一个对方棋子");
    Tool.removeArrows();

    String input = Tool.input();
    Tool.setActivatedPiece(null);
    Piece piece;
    if (input.length() == 3) {
      piece = Tool.findPiece(input.charAt(2));
    } else {
      piece = Tool.findPiece(input.charAt(0));
    }
    if (piece == null) {
      Tool.print("输入有误");
    }
    return piece;
  }

  public void start(Piece piece, int i) {
    Tool.locked();
    Tool.print(piece.getNameAndLV() + " 使用了 " + piece.getSpellName(i) + "!", 1);
  }

  public void start(Piece piece1, Piece piece2, int i) throws KingSpellException {
    Tool.locked();
    if (piece2.isKing()) {
      throw new KingSpellException();
    }
    Tool.print(piece1.getNameAndLV() + " 使用了 " + piece1.getSpellName(i) + "!", 1);
  }

  public boolean over() {
    Tool.print("技能结束", 1);
    Tool.unlock();
    return true;
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
