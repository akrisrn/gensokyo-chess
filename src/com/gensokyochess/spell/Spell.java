package com.gensokyochess.spell;

import com.csvreader.CsvReader;
import com.gensokyochess.Chessboard;
import com.gensokyochess.Piece;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

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

  public abstract void use(Piece piece, Chessboard chessboard, ArrayList<Piece> pieces);

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
        return new MusouFuuinnSpell("H1");
      default:
        return null;
    }
  }
}
