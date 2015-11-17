import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class Role {
  private final int RIVER_BONUS = 5;
  private String Name;
  private char Code;
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

  public Role(char code) {
    String path = System.getProperty("user.dir") + "/role.csv";
    CsvReader reader = null;

    try {
      reader = new CsvReader(path, ',', Charset.forName("utf-8"));
    } catch (FileNotFoundException e) {
      System.out.println("没有找到角色文件");
      System.exit(1);
    }

    boolean NotFindCode = true;
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
          NotFindCode = false;
          break;
        }
      }
    } catch (IOException | NumberFormatException e) {
      System.out.println("文件读取出错");
      System.exit(1);
    } finally {
      reader.close();
    }

    if (NotFindCode) {
      System.out.println("没有角色代码为: " + code);
      System.exit(1);
    }

    HitPoint = 10 + getBonus(Constitution);
    CurrentHP = HitPoint;
    ArmorClass = 10 + getBonus(Dexterity) + BodyBonus;
    Initiative = getBonus(Dexterity);

    if (AttackType == 0) {
      AttackBonus = getBonus(Strength) + BodyBonus;
    } else {
      AttackBonus = getBonus(Dexterity) + BodyBonus;
    }
    CurrentAB = AttackBonus;
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
      return roll(2, 2) + getBonus(Strength);
    } else {
      return roll(1, 7 - DistanceBonus) + getBonus(Dexterity);
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

  public String getName() {
    return Name;
  }

  public char getCode() {
    return Code;
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
}