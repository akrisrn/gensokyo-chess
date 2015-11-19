package com.gensokyochess;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class Role {
  private final int RIVER_BONUS = 5;
  private String Name;
  private char Code;
  private int Level;
  private int Strength;
  private int Dexterity;
  private int Constitution;
  private int HitPoint;
  private int CurrentHP;
  private int ArmorClass;
  private int Initiative;
  private int AttackBonus;
  private int CurrentAB;
  private int AttackType;
  private int BodyBonus;
  private int DistanceBonus = 0;
  private int MaxDamage;
  private int MinDamage;
  private int DamageBonus;
  private boolean Alive = true;

  public Role(char code, int level) {
    if (level > 5) {
      level = 5;
    }
    setRoleLevel(code, level);
  }

  private void init(char code) {
    String path = System.getProperty("user.dir") + "/role.csv";
    CsvReader reader = null;

    try {
      reader = new CsvReader(path, ',', Charset.forName("utf-8"));
    } catch (FileNotFoundException e) {
      System.out.println("没有找到角色文件");
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
          break;
        }
      }
    } catch (IOException | NumberFormatException e) {
      System.out.println("文件读取出错");
      System.exit(1);
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

  protected void setRoleLevel(char code, int level) {
    if (level <= 1) {
      init(code);
    } else {
      setRoleLevel(code, level - 1);
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

  public void hpReduce(int damage) {
    CurrentHP -= damage;
  }

  public int getArmorClass() {
    return ArmorClass;
  }

  public void addRiverBonus() {
    ArmorClass += RIVER_BONUS;
  }

  public void subRiverBonus() {
    ArmorClass -= RIVER_BONUS;
  }

  private int getBonus(int attr) {
    return (int) Math.floor((attr - 10) / 2.0);
  }

  public int getCurrentHP() {
    return CurrentHP;
  }

  public int getHitPoint() {
    return HitPoint;
  }

  public String getName() {
    return Name;
  }

  public String getNameAndLV() {
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

  public void subRemoteAttack(int gridNumber) {
    CurrentAB -= gridNumber + 2;
    if (gridNumber == 0) {
      DistanceBonus = 4;
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
    return "姓名: " + getName() + "\n" +
            "等级: " + getLevel() + "\n" +
            "生命: " + getHitPoint() + "\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n";
  }
}