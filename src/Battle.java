public class Battle {
  private static char normalAtk(Role role1, Role role2) {
    print(role1.getName() + " 的攻击击中了!");
    int damage = role1.rollDamage();
    role2.hpReduce(damage);
    print(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    print(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    return defeated(role2);
  }

  private static char criticalAtk(Role role1, Role role2) {
    print(role1.getName() + " 造成了重击!");
    int damage = role1.rollDamage() + role1.rollDamage();
    role2.hpReduce(damage);
    print(role1.getName() + " 对 " + role2.getName() + " 造成了 " + damage + " 点伤害");
    print(role2.getName() + " 的 HP 现在是:" + role2.getCurrentHP());
    return defeated(role2);
  }

  private static char defeated(Role role2) {
    if (role2.getCurrentHP() <= 0) {
      print(role2.getName() + " 被打倒了!");
      role2.setAlive(false);
      role2.recoverHP();
      return role2.getCode();
    } else {
      return ' ';
    }
  }

  private static char battleRound(Role role1, Role role2, int distance) {
    if (role1.getAttackType() == 1) {
      role1.recoverAB();
      role1.subRemoteAttack(distance);
    }

    int armorClass = role2.getArmorClass();
    int attackRoll = role1.rollAttack();
    print(role1.getName() + " 的攻击检定为:" + attackRoll);

    if (attackRoll - role1.getCurrentAB() == 1) {
      print(role1.getName() + " 的攻击失手了!");
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
        print(role1.getName() + " 的攻击失手了!");
        return ' ';
      }
    } else {
      print(role1.getName() + " 的攻击失手了!");
      return ' ';
    }
  }

  private static boolean judgeInitiative(Role role1, Role role2) {
    int initiativeRoll1 = role1.rollInitiative();
    int initiativeRoll2 = role2.rollInitiative();
    print(role1.getName() + " 的先攻检定为:" + initiativeRoll1);
    print(role2.getName() + " 的先攻检定为:" + initiativeRoll2);

    if (initiativeRoll1 > initiativeRoll2) {
      print(role1.getName() + " 先攻!");
      return true;
    } else if (initiativeRoll1 < initiativeRoll2) {
      print(role2.getName() + " 先攻!");
    } else {
      if (role1.getDexterity() > role2.getDexterity()) {
        print(role1.getName() + " 先攻!");
        return true;
      } else {
        print(role2.getName() + " 先攻!");
      }
    }
    return false;
  }

  public static char frontalBattle(Role role1, Role role2) {
    print(role1.getName() + " 和 " + role2.getName() + " 展开正面战斗");

    boolean isRole1First = judgeInitiative(role1, role2);
    char loser;

    while (true) {
      if (isRole1First) {
        loser = battleRound(role1, role2, 0);
        if (loser != ' ') {
          return loser;
        }
      } else {
        loser = battleRound(role2, role1, 0);
        if (loser != ' ') {
          return loser;
        }
      }
      isRole1First = !isRole1First;
    }
  }

  public static char remoteBattle(Role role1, Role role2, int distance) {
    print(role1.getName() + " 对 " + role2.getName() + " 进行远程攻击");
    return battleRound(role1, role2, distance);
  }

  public static char opportunityBattle(Role role1, Role role2) {
    print(role1.getName() + " 对 " + role2.getName() + " 进行借机攻击");
    return battleRound(role1, role2, 0);
  }

  private static void print(String msg) {
    System.out.println(msg);
    try {
      Thread.sleep(1500);
    } catch (InterruptedException ignored) {
    }
  }
}
