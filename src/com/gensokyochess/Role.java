package com.gensokyochess;

import com.csvreader.CsvReader;
import com.gensokyochess.exception.HaveNotSpellException;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;
import com.gensokyochess.spell.Spell;

import java.io.IOException;

import static com.gensokyochess.spell.Spell.choiceSpell;

/**
 * 角色类
 */
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
  private int MaxDamage, MinDamage, DamageBonus, MeleeBonus = 0;
  private boolean Alive = true;
  private boolean InRiver = false;
  private int TotalSpellNumber;
  private String[] SpellCode;
  private Spell[] Spell;

  /**
   * 实例化一个角色
   *
   * @param code  角色代码
   * @param level 角色等级
   */
  public Role(char code, int level) {
    if (level > 5) {
      level = 5;
    }
    initRole(code, level);
    initSpell();
  }

  /**
   * 读取 role.csv 文件初始化棋子 1 级时的属性
   *
   * @param code 角色代码
   */
  private void initAttribute(char code) {
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

  /**
   * 计算属性调整值
   */
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

  /**
   * 初始化技能
   */
  private void initSpell() {
    Spell = new Spell[TotalSpellNumber];
    for (int i = 0; i < TotalSpellNumber; i++) {
      Spell[i] = choiceSpell(SpellCode[i]);
    }
  }

  /**
   * 初始化角色
   *
   * @param code  角色代码
   * @param level 角色等级
   */
  protected void initRole(char code, int level) {
    if (level <= 1) {
      initAttribute(code);
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

  /**
   * 提高属性
   *
   * @param odevity  要提高的属性值的奇偶性
   * @param improve1 第一种提高值
   * @param improve2 第二种提高值
   */
  private void increaseAttr(int odevity, int improve1, int improve2) {
    if (Strength % 2 == odevity) {
      Strength += improve1;
      if (Dexterity <= Constitution) {
        Dexterity += improve2;
      } else {
        Constitution += improve2;
      }
    } else if (Dexterity % 2 == odevity) {
      Dexterity += improve1;
      if (Strength <= Constitution) {
        Strength += improve2;
      } else {
        Constitution += improve2;
      }
    } else {
      Constitution += improve1;
      if (Strength <= Dexterity) {
        Strength += improve2;
      } else {
        Dexterity += improve2;
      }
    }
  }

  /**
   * 使用技能
   *
   * @param num 技能编号
   * @throws HaveNotSpellException 还没有技能
   * @throws KingSpellException    国王技能
   * @throws SameCampException     相同阵营
   */
  public void useSpell(int num) throws HaveNotSpellException, KingSpellException, SameCampException {
    if (Spell[num - 1] == null) {
      throw new HaveNotSpellException();
    }
    while (true) {
      if (Spell[num - 1].use((Piece) this)) {
        break;
      }
    }
  }

  /**
   * 提高属性
   *
   * @param increase 提高值
   */
  private void increaseAttr(int increase) {
    if (Strength >= Dexterity && Strength >= Constitution) {
      Strength += increase;
    } else if (Dexterity >= Strength && Dexterity >= Constitution) {
      Dexterity += increase;
    } else {
      Constitution += increase;
    }
  }

  /**
   * 掷骰（ndx）
   *
   * @param n d 前面的
   * @param x d 后面的
   * @return 骰子值
   */
  public int roll(int n, int x) {
    int ran = 0;
    for (int i = 0; i < n; i++) {
      ran += (int) (Math.random() * x + 1);
    }
    return ran;
  }

  /**
   * 投先攻检定
   *
   * @return 先攻值
   */
  public int rollInitiative() {
    return Initiative + roll(1, 20);
  }

  /**
   * 投攻击检定
   *
   * @return 攻击检定值
   */
  public int rollAttack() {
    return CurrentAB + roll(1, 20);
  }

  /**
   * 投伤害
   *
   * @return 伤害值
   */
  public int rollDamage() {
    if (AttackType == 0) {
      return roll(MinDamage, MaxDamage) + DamageBonus;
    } else {
      return roll(MinDamage, MaxDamage - MeleeBonus) + DamageBonus;
    }
  }

  /**
   * 减弱远程攻击加值，重置近战减值
   *
   * @param gridNumber 格子数
   */
  public void weakenRemote(int gridNumber) {
    CurrentAB -= gridNumber + 2;
    if (gridNumber == 0) {
      MeleeBonus = 5;
    } else {
      MeleeBonus = 0;
    }
  }

  /**
   * 恢复攻击加值
   */
  public void recoverAB() {
    CurrentAB = AttackBonus;
  }

  /**
   * 恢复生命值
   */
  public void recoverHP() {
    CurrentHP = HitPoint;
  }

  /**
   * 减少生命值
   *
   * @param damage 伤害值
   */
  public void reduceHp(int damage) {
    CurrentHP -= damage;
  }

  /**
   * 添加河流加值
   */
  public void addRiverBonus() {
    ArmorClass += RIVER_BONUS;
  }

  /**
   * 清除河流加值
   */
  public void clearRiverBonus() {
    ArmorClass -= RIVER_BONUS;
  }

  /**
   * 添加防御加值
   */
  public void addDefenseBonus() {
    if (DefenceBonusCount < 2) {
      DefenceBonusCount++;
      ArmorClass += DEFENCE_BONUS;
    }
  }

  /**
   * 清除防御加值
   */
  public void clearDefenseBonus() {
    ArmorClass -= DEFENCE_BONUS * DefenceBonusCount;
    DefenceBonusCount = 0;
  }

  /**
   * 增加防御等级
   *
   * @param increase 增加值
   */
  public void addArmorClass(int increase) {
    ArmorClass += increase;
  }

  /**
   * 减少防御等级
   *
   * @param decrease 减少值
   */
  public void subArmorClass(int decrease) {
    ArmorClass -= decrease;
  }

  /**
   * 增加攻击加值
   *
   * @param increase 增加值
   */
  public void addAttackBonus(int increase) {
    AttackBonus += increase;
    recoverAB();
  }

  /**
   * 减少攻击加值
   *
   * @param decrease 减少值
   */
  public void subAttackBonus(int decrease) {
    AttackBonus -= decrease;
    recoverAB();
  }

  public void addHitPoint(int recover) {
    CurrentHP += recover;
    if (CurrentHP > HitPoint) {
      CurrentHP = HitPoint;
    }
  }

  /**
   * 获取调整值
   *
   * @param attr 属性值
   * @return 调整值
   */
  private int getBonus(int attr) {
    return (int) Math.floor((attr - 10) / 2.0);
  }

  public int getArmorClass() {
    return ArmorClass;
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

  public int getDefenceBonusCount() {
    return DefenceBonusCount;
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

  public String getSpell() {
    String spell = "";
    for (int i = 0; i < TotalSpellNumber; i++) {
      if (Spell[i] != null) {
        spell += "\n" + Spell[i].toString();
      }
    }
    return spell + "\n";
  }

  @Override
  public String toString() {
    return "\n-----" + getName() + "\n" +
            "等级: " + getLevel() + "\n" +
            "生命: " + getHitPoint() + "\n" +
            "伤害: " + getDamageRange() + "\n" +
            "力量: " + getStrength() + "\n" +
            "敏捷: " + getDexterity() + "\n" +
            "体质: " + getConstitution() + "\n" +
            "体型: " + getBodyType() + "\n" +
            "攻击方式: " + getRawAttackType() + "\n" +
            "-----技能" + getSpell();
  }
}