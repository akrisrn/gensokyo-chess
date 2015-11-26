package com.gensokyochess.spell;

import com.csvreader.CsvReader;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class Spell {
  private String Code;
  private String Name;
  private String Description;

  public Spell(String code) {
    String path = System.getProperty("user.dir") + "/lib/spell.csv";
    CsvReader reader = null;

    try {
      reader = new CsvReader(path, ',', Charset.forName("utf-8"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    try {
      reader.readHeaders();
      while (reader.readRecord()) {
        if (reader.get("Code").equals(code)) {
          setCode(code);
          setName(reader.get("Name"));
          setDescription(reader.get("Description"));
          break;
        }
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    } finally {
      reader.close();
    }
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

  public void setCode(String code) {
    Code = code;
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

  public static Spell switchSpell(String code) {
    switch (code) {
      case "H1":
        return new FantasySeal("H1");
      case "H2":
        return new EvilSealingCircle("H2");
      case "K1":
        return new MasterSpark("K1");
      default:
        return null;
    }
  }
}
