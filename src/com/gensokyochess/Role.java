package com.gensokyochess;

import com.csvreader.CsvReader;
import com.gensokyochess.exception.HaveNotSpellException;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;
import com.gensokyochess.spell.Spell;

import java.io.IOException;

import static com.gensokyochess.spell.Spell.choiceSpell;

public class Role {
  private final int RIVER_BONUS = 5;
  private final int DEFENCE_BONUS = 2;
  private int DefenceBonusCount = 0;
  private String Name;
  private char Code;
  private int Level;
  private int Strength, Dexterity, Constitution;
  private int HitPoint, CurrentHP;
  private int ArmorClass;
  private int Initiative;
  private int BodyBonus;
  private int AttackBonus, CurrentAB, AttackType;
  private int MaxDamage, MinDamage, DamageBonus, DistanceBonus = 0;
  private boolean Alive = true;
  private boolean InRiver = false;
  private int TotalSpellNumber;
  private String[] SpellCode;
  private Spell[] Spell;

  public Role(char code, int level) {
    if (level > 5) {
      level = 5;
    }
    initRole(code, level);
    initSpell();
  }

  private void initLevel(char code) {
    CsvReader reader = Tool.getCsvReader("/lib/role.csv");
    if (reader == null) {
      System.exit(1);
    }

    try {
      reader.readHeaders();
      while (reader.readRecord()) {
        if (reader.get("Code").charAt(0) == code) {
          Name = reader.get("Name");
          Strength = Integer.parseInt(reader.get("Str"));
          Dexterity = Integer.parseInt(reader.get("Dex"));
          Constitution = Integer.parseInt(reader.get("Con"));
          BodyBonus = Integer.parseInt(reader.get("BodyBonus"));
          AttackType = Integer.parseInt(reader.get("AttackType"));
          Code = code;
          TotalSpellNumber = reader.getColumnCount() - 7;
          SpellCode = new String[TotalSpellNumber];
          for (int i = 1; i <= TotalSpellNumber; i++) {
            SpellCode[i - 1] = reader.get("SpellCode" + i);
          }
          break;
        }
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    } finally {
      reader.close();
    }
  }

  private void calculateBonus() {
    HitPoint = 10 + getBonus(Constitution) + (Level - 1) * 3;
    CurrentHP = HitPoint;
    ArmorClass = 10 + getBonus(Dexterity) + BodyBonus;
    Initiative = getBonus(Dexterity);

    if (AttackType == 0) {
      AttackBonus = getBonus(Strength) + BodyBonus;
      MaxDamage = Level + 1;
      MinDamage = 2;
      DamageBonus = getBonus(Strength);
    } else {
      AttackBonus = getBonus(Dexterity) + BodyBonus;
      MaxDamage = Level + 6;
      MinDamage = 1;
      DamageBonus = getBonus(Dexterity);
    }
    CurrentAB = AttackBonus;
  }

  protected void initRole(char code, int level) {
    if (level <= 1) {
      initLevel(code);
    } else {
      initRole(code, level - 1);
      if (Strength % 2 + Dexterity % 2 + Constitution % 2 == 3) {
        increaseAttr(3);
      } else if (Strength % 2 + Dexterity % 2 + Constitution % 2 == 2) {
        increaseAttr(0, 1, 3);
      } else if (Strength % 2 + Dexterity % 2 + Constitution % 2 == 1) {
        increaseAttr(1, 3, 1);
      } else {
        increaseAttr(5);
      }
    }
    Level = level;
    calculateBonus();
  }

  public boolean useSpell(int i) throws HaveNotSpellException, KingSpellException, SameCampException {
    if (Spell[i - 1] == null) {
      throw new HaveNotSpellException();
    }
    return Spell[i - 1].use((Piece) this);
  }

  public int getTotalSpellNumber() {
    return TotalSpellNumber;
  }

  public String getSpellCode(int i) {
    return SpellCode[i - 1];
  }

  public String getSpellName(int i) {
    if (Spell[i - 1] == null) {
      return "";
    } else {
      return Spell[i - 1].getName();
    }
  }

  public String getSpell() {
    String spell = "";
    for (int i = 0; i < TotalSpellNumber; i++) {
      if (Spell[i] != null) {
        spell += "\n" + Spell[i].toString();
      }
    }
    return spell + "\n";
  }

  private void initSpell() {
    Spell = new Spell[TotalSpellNumber];
    for (int i = 0; i < TotalSpellNumber; i++) {
      Spell[i] = choiceSpell(SpellCode[i]);
    }
  }

  private void increaseAttr(int rise) {
    if (Strength >= Dexterity && Strength >= Constitution) {
      Strength += rise;
    } else if (Dexterity >= Strength && Dexterity >= Constitution) {
      Dexterity += rise;
    } else {
      Constitution += rise;
    }
  }

  private void increaseAttr(int odevity, int rise1, int rise2) {
    if (Strength % 2 == odevity) {
      Strength += rise1;
      if (Dexterity <= Constitution) {
        Dexterity += rise2;
      } else {
        Constitution += rise2;
      }
    } else if (Dexterity % 2 == odevity) {
      Dexterity += rise1;
      if (Strength <= Constitution) {
        Strength += rise2;
      } else {
        Constitution += rise2;
      }
    } else {
      Constitution += rise1;
      if (Strength <= Dexterity) {
        Strength += rise2;
      } else {
        Dexterity += rise2;
      }
    }
  }

  public int roll(int x, int n) {
    int ran = 0;
    for (int i = 0; i < x; i++) {
      ran += (int) (Math.random() * n + 1);
    }
    return ran;
  }

  public int rollInitiative() {
    return Initiative + roll(1, 20);
  }

  public int rollAttack() {
    return CurrentAB + roll(1, 20);
  }

  public int rollDamage() {
    if (AttackType == 0) {
      return roll(MinDamage, MaxDamage) + DamageBonus;
    } else {
      return roll(MinDamage, MaxDamage - DistanceBonus) + DamageBonus;
    }
  }

  public void recoverHP() {
    CurrentHP = HitPoint;
  }

  public void reduceHp(int damage) {
    CurrentHP -= damage;
  }

  public int getArmorClass() {
    return ArmorClass;
  }

  public void addRiverBonus() {
    ArmorClass += RIVER_BONUS;
  }

  public void clearRiverBonus() {
    ArmorClass -= RIVER_BONUS;
  }

  public void addDefenseBonus() {
    if (DefenceBonusCount < 2) {
      DefenceBonusCount++;
      ArmorClass += DEFENCE_BONUS;
    }
  }

  public void clearDefenseBonus() {
    ArmorClass -= DEFENCE_BONUS * DefenceBonusCount;
    DefenceBonusCount = 0;
  }

  private int getBonus(int attr) {
    return (int) Math.floor((attr - 10) / 2.0);
  }

  public int getCurrentHP() {
    return CurrentHP;
  }

  public int getInitiative() {
    return Initiative;
  }

  public int getHitPoint() {
    return HitPoint;
  }

  public String getName() {
    return Name;
  }

  public String getNameAndLv() {
    return Name + "(Lv:" + Level + ")";
  }

  public char getCode() {
    return Code;
  }

  public int getLevel() {
    return Level;
  }

  public int getCurrentAB() {
    return CurrentAB;
  }

  public void recoverAB() {
    CurrentAB = AttackBonus;
  }

  public void weakenRemote(int gridNumber) {
    CurrentAB -= gridNumber + 2;
    if (gridNumber == 0) {
      DistanceBonus = 5;
    } else {
      DistanceBonus = 0;
    }
  }

  public int getAttackType() {
    return AttackType;
  }

  public int getStrength() {
    return Strength;
  }

  public int getBodyBonus() {
    return BodyBonus;
  }

  public int getConstitution() {
    return Constitution;
  }

  public int getDexterity() {
    return Dexterity;
  }

  public boolean isAlive() {
    return Alive;
  }

  public void setAlive(boolean alive) {
    Alive = alive;
  }

  public boolean isInRiver() {
    return InRiver;
  }

  public void setInRiver(boolean inRiver) {
    InRiver = inRiver;
  }

  public String getBodyType() {
    switch (getBodyBonus()) {
      case 0:
        return "中型";
      case 1:
        return "小型";
      case 2:
        return "超小型";
      case -1:
        return "大型";
      case -2:
        return "超大型";
      default:
        return "中型";
    }
  }

  public String getRawAttackType() {
    if (getAttackType() == 0) {
      return "近战";
    } else {
      return "远程";
    }
  }

  public String getDamageRange() {
    if (DamageBonus >= 0) {
      return MinDamage + "d" + MaxDamage + "+" + DamageBonus;
    } else {
      return MinDamage + "d" + MaxDamage + DamageBonus;
    }
  }

  @Override
  public String toString() {
    return "\n--------" + getName() + "\n" +
            "等级: " + getLevel() + "\n" +
            "生命: " + getHitPoint() + "\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n" +
            "--------技能" + getSpell();
  }
}