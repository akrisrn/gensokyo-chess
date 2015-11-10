public class Role {
  private final int RIVER_BONUS = 5;
  private char Name;
  private int Strength;
  private int Dexterity;
  private int Constitution;
  private int HitPoint;
  private int CurrentHP;
  private int ArmorClass;
  private int Initiative;
  private int AttackBonus;
  private int CurrentAB;
  private int AttackType = 0;

  public Role(char name) {
    Name = name;
    switch (name) {
      case 'a':
        Strength = 11;
        Dexterity = 15;
        Constitution = 9;
        break;
      case 'b':
        Strength = 9;
        Dexterity = 15;
        Constitution = 11;
        AttackType = 1;
        break;
      case 'c':
        Strength = 12;
        Dexterity = 10;
        Constitution = 10;
        break;
      case 'd':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'e':
        Strength = 11;
        Dexterity = 10;
        Constitution = 13;
        break;
      case 'f':
        Strength = 12;
        Dexterity = 11;
        Constitution = 11;
        break;
      case 'g':
        Strength = 10;
        Dexterity = 12;
        Constitution = 10;
        break;
      case 'h':
        Strength = 10;
        Dexterity = 15;
        Constitution = 8;
        AttackType = 1;
        break;
      case 'i':
        Strength = 11;
        Dexterity = 12;
        Constitution = 11;
        break;
      case 'j':
        Strength = 13;
        Dexterity = 11;
        Constitution = 10;
        break;
      case 'k':
        Strength = 13;
        Dexterity = 11;
        Constitution = 10;
        break;
      case 'A':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'B':
        Strength = 10;
        Dexterity = 12;
        Constitution = 10;
        break;
      case 'C':
        Strength = 15;
        Dexterity = 8;
        Constitution = 10;
        break;
      case 'D':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'E':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'F':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'G':
        Strength = 10;
        Dexterity = 10;
        Constitution = 12;
        break;
      case 'H':
        Strength = 11;
        Dexterity = 13;
        Constitution = 11;
        break;
      case 'I':
        Strength = 10;
        Dexterity = 8;
        Constitution = 14;
        break;
      case 'J':
        Strength = 13;
        Dexterity = 10;
        Constitution = 10;
        break;
      case 'K':
        Strength = 13;
        Dexterity = 11;
        Constitution = 11;
        AttackType = 1;
        break;
    }

    HitPoint = 10 + getBonus(Constitution);
    CurrentHP = HitPoint;
    ArmorClass = 10 + getBonus(Dexterity);
    Initiative = getBonus(Dexterity);

    if (AttackType == 0) {
      AttackBonus = getBonus(Strength);
    } else {
      AttackBonus = getBonus(Dexterity);
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
      return roll(1, 7) + getBonus(Dexterity);
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

  public char getName() {
    return Name;
  }

  public int getCurrentAB() {
    return CurrentAB;
  }

  public void recoverAB() {
    CurrentAB = AttackBonus;
  }

  public void subDistanceBonus(int gridNumber) {
    CurrentAB -= gridNumber + 2;
  }

  public int getAttackType() {
    return AttackType;
  }

  public int getStrength() {
    return Strength;
  }

  public int getDexterity() {
    return Dexterity;
  }
}