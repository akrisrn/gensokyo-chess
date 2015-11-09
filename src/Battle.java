public class Battle {
  private static char normalAtk(Role role1, Role role2) {
    System.out.println(role1.getName() + " 的攻击击中了!");
    int damage = role1.rollDamage();
    role2.hpReduce(damage);
    System.out.println(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    System.out.println(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    return defeated(role2);
  }

  private static char criticalAtk(Role role1, Role role2) {
    System.out.println(role1.getName() + " 造成了重击!");
    int damage = role1.rollDamage() + role1.rollDamage();
    role2.hpReduce(damage);
    System.out.println(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    System.out.println(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    return defeated(role2);
  }

  private static char defeated(Role role2) {
    if (role2.getCurrentHP() <= 0) {
      System.out.println(role2.getName() + " 被打倒了!");
      role2.recoverHP();
      return role2.getName();
    } else {
      return ' ';
    }
  }

  private static char battleRound(Role role1, Role role2, int distance) {
    if (role1.getAttackType() != 0) {
      role1.recoverAB();
      role1.subDistanceBonus(distance);
    }

    int armorClass = role2.getArmorClass();
    int attackRoll = role1.rollAttack();
    System.out.println(role1.getName() + " 的攻击检定为:" + attackRoll);

    if (attackRoll - role1.getCurrentAB() == 1) {
      System.out.println(role1.getName() + " 的攻击失手了!");
      return ' ';
    } else if (attackRoll - role1.getCurrentAB() == 20) {
      if (role1.rollAttack() > armorClass) {
        return criticalAtk(role1, role2);
      } else {
        return normalAtk(role1, role2);
      }
    } else if (attackRoll > armorClass) {
      return normalAtk(role1, role2);
    } else if (attackRoll == armorClass) {
      if (role1.getStrength() > role2.getStrength()) {
        return normalAtk(role1, role2);
      } else {
        System.out.println(role1.getName() + " 的攻击失手了!");
        return ' ';
      }
    } else {
      System.out.println(role1.getName() + " 的攻击失手了!");
      return ' ';
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

  public static char frontalBattle(Role role1, Role role2) {
    boolean isRole1First = judgeInitiative(role1, role2);
    char result;
    while (true) {
      if (isRole1First) {
        result = battleRound(role1, role2, 0);
        if (result != ' ') {
          return result;
        }
      } else {
        result = battleRound(role2, role1, 0);
        if (result != ' ') {
          return result;
        }
      }
      isRole1First = !isRole1First;
    }
  }

  public static char remoteBattle(Role role1, Role role2, int distance) {
    return battleRound(role1, role2, distance);
  }

  public static char opportunityBattle(Role role1, Role role2) {
    return battleRound(role1, role2, 0);
  }
}
