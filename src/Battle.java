public class Battle {
  static boolean IsBattleOver;
  static char Loser;

  private static void normalAtk(Role role1, Role role2) {
    System.out.println(role1.getName() + " 的攻击击中了!");
    int damage = role1.rollDamage();
    role2.hpReduce(damage);
    System.out.println(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    System.out.println(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    isDefeated(role2);
  }

  private static void criticalAtk(Role role1, Role role2) {
    System.out.println(role1.getName() + " 造成了重击!");
    int damage = role1.rollDamage() + role1.rollDamage();
    role2.hpReduce(damage);
    System.out.println(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    System.out.println(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    isDefeated(role2);
  }

  private static void isDefeated(Role role2) {
    if (role2.getCurrentHP() <= 0) {
      System.out.println(role2.getName() + " 被打倒了!");
      Loser = role2.getName();
      role2.recoverHP();
      IsBattleOver = true;
    }
  }

  private static void battleRound(Role role1, Role role2) {
    int armorClass = role2.getArmorClass();
    int attackRoll = role1.rollAttack();
    System.out.println(role1.getName() + " 的攻击检定为:" + attackRoll);

    if (attackRoll - role1.getAttackBonus() == 1) {
      System.out.println(role1.getName() + " 的攻击失手了!");
    } else if (attackRoll - role1.getAttackBonus() == 20) {
      if (role1.rollAttack() > armorClass) {
        criticalAtk(role1, role2);
      } else {
        normalAtk(role1, role2);
      }
    } else if (attackRoll > armorClass) {
      normalAtk(role1, role2);
    } else if (attackRoll == armorClass) {
      if (role1.getStrength() > role2.getStrength()) {
        normalAtk(role1, role2);
      } else {
        System.out.println(role1.getName() + " 的攻击失手了!");
      }
    } else {
      System.out.println(role1.getName() + " 的攻击失手了!");
    }
  }

  private static boolean judgeInitiative(Role role1, Role role2) {
    int initiativeRoll1 = role1.rollInitiative();
    int initiativeRoll2 = role2.rollInitiative();

    System.out.println(role1.getName() + " 的先攻检定为:" + initiativeRoll1);
    System.out.println(role2.getName() + " 的先攻检定为:" + initiativeRoll2);

    if (initiativeRoll1 > initiativeRoll2) {
      System.out.println(role1.getName() + " 先攻!");
      return true;
    } else if (initiativeRoll1 < initiativeRoll2) {
      System.out.println(role2.getName() + " 先攻!");
    } else {
      if (role1.getDexterity() > role2.getDexterity()) {
        System.out.println(role1.getName() + " 先攻!");
        return true;
      } else {
        System.out.println(role2.getName() + " 先攻!");
      }
    }
    return false;
  }

  public static void start(Role role1, Role role2) {
    boolean isRole1First = judgeInitiative(role1, role2);

    IsBattleOver = false;
    while (!IsBattleOver) {
      if (isRole1First) {
        battleRound(role1, role2);
      } else {
        battleRound(role2, role1);
      }
      isRole1First = !isRole1First;
    }
  }
}
