package com.gensokyochess;

import com.gensokyochess.exception.InRiverException;

public class Battle {
  private static void normalAtk(Role role1, Role role2) {
    Tool.print(role1.getNameAndLV() + " 的攻击击中了!", 1);
    int damage = role1.rollDamage();
    role2.hpReduce(damage);
    Tool.print(role1.getNameAndLV() + " 对 " + role2.getNameAndLV() + " 造成了 " + damage + " 点伤害", 1);
    Tool.print(role2.getNameAndLV() + " 的 HP 现在是:" + role2.getCurrentHP(), 1);
    defeated(role2);
  }

  private static void criticalAtk(Role role1, Role role2) {
    Tool.print(role1.getNameAndLV() + " 造成了重击!", 1);
    int damage1 = role1.rollDamage();
    int damage2 = role1.rollDamage();
    int damage = damage1 + damage2;
    role2.hpReduce(damage);
    Tool.print(role1.getNameAndLV() + " 对 " + role2.getNameAndLV() +
            " 造成了 " + damage + "(" + damage1 + "+" + damage2 + ") 点伤害", 1);
    Tool.print(role2.getNameAndLV() + " 的 HP 现在是:" + role2.getCurrentHP(), 1);
    defeated(role2);
  }

  public static void defeated(Role role2) {
    if (role2.getCurrentHP() <= 0) {
      Tool.print(role2.getNameAndLV() + " 被打倒了!", 1);
      role2.setAlive(false);
      role2.recoverHP();
    }
  }

  private static String handleSign(int bonus) {
    if (bonus >= 0) {
      return "+";
    } else {
      return "";
    }
  }

  private static void battleRound(Role role1, Role role2, int distance) {
    if (role1.getAttackType() == 1) {
      role1.recoverAB();
      role1.subRemoteAttack(distance);
    }

    int armorClass = role2.getArmorClass();
    int attackRoll = role1.rollAttack();
    int attackBonus = role1.getCurrentAB();
    String sign = handleSign(attackBonus);
    int originalRoll = attackRoll - attackBonus;

    Tool.print(role1.getNameAndLV() + " 的攻击检定为:" +
            attackRoll + "(" + originalRoll + sign + attackBonus + ")", 1);

    if (originalRoll == 1) {
      Tool.print(role1.getNameAndLV() + " 的攻击失手了!", 1);
    } else if (originalRoll == 20) {
      Tool.print(role1.getNameAndLV() + " 造成了重击威胁!", 1);
      attackRoll = role1.rollAttack();
      originalRoll = attackRoll - attackBonus;
      Tool.print(role1.getNameAndLV() + " 的第二次攻击检定为:" +
              attackRoll + "(" + originalRoll + sign + attackBonus + ")", 1);
      if (attackRoll > armorClass) {
        criticalAtk(role1, role2);
      } else {
        Tool.print(role1.getNameAndLV() + " 未造成重击", 1);
        normalAtk(role1, role2);
      }
    } else if (attackRoll > armorClass) {
      normalAtk(role1, role2);
    } else if (attackRoll == armorClass) {
      if (role1.getStrength() > role2.getStrength()) {
        normalAtk(role1, role2);
      } else {
        Tool.print(role1.getNameAndLV() + " 的攻击失手了!", 1);
      }
    } else {
      Tool.print(role1.getNameAndLV() + " 的攻击失手了!", 1);
    }
  }

  private static boolean judgeInitiative(Role role1, Role role2) {
    int initiativeRoll1 = role1.rollInitiative();
    int initiative1 = role1.getInitiative();
    int originalRoll1 = initiativeRoll1 - initiative1;

    int initiativeRoll2 = role2.rollInitiative();
    int initiative2 = role2.getInitiative();
    int originalRoll2 = initiativeRoll2 - initiative2;

    Tool.print(role1.getNameAndLV() + " 的先攻检定为:" +
            initiativeRoll1 + "(" + originalRoll1 + handleSign(initiative1) + initiative1 + ")", 1);
    Tool.print(role2.getNameAndLV() + " 的先攻检定为:" +
            initiativeRoll2 + "(" + originalRoll2 + handleSign(initiative2) + initiative2 + ")", 1);

    if (initiativeRoll1 > initiativeRoll2) {
      Tool.print(role1.getNameAndLV() + " 先攻!", 1);
      return true;
    } else if (initiativeRoll1 < initiativeRoll2) {
      Tool.print(role2.getNameAndLV() + " 先攻!", 1);
    } else {
      if (role1.getDexterity() > role2.getDexterity()) {
        Tool.print(role1.getNameAndLV() + " 先攻!", 1);
        return true;
      } else {
        Tool.print(role2.getNameAndLV() + " 先攻!", 1);
      }
    }
    return false;
  }

  public static void frontalBattle(Role role1, Role role2) {
    Tool.print(role1.getNameAndLV() + " 和 " + role2.getNameAndLV() + " 展开正面战斗", 1);

    boolean isRole1First = judgeInitiative(role1, role2);

    int i = 0;
    while (i++ < 6) {
      if (isRole1First) {
        battleRound(role1, role2, 0);
        if (!role2.isAlive()) {
          break;
        }
      } else {
        battleRound(role2, role1, 0);
        if (!role1.isAlive()) {
          break;
        }
      }
      isRole1First = !isRole1First;
    }
    Tool.print("战斗结束", 1);
    role1.subDefenseBonus();
    role2.subDefenseBonus();
  }

  public static void remoteBattle(Role role1, Role role2, int distance) throws InRiverException {
    if (distance != 0) {
      Tool.print(role1.getNameAndLV() + " 对 " + role2.getNameAndLV() + " 进行远程攻击", 1);
    } else {
      if (role2.isInRiver()) {
        throw new InRiverException();
      }
      Tool.print(role1.getNameAndLV() + " 对 " + role2.getNameAndLV() + " 进行近战攻击", 1);
    }
    battleRound(role1, role2, distance);
    Tool.print("战斗结束", 1);
    role1.subDefenseBonus();
    role2.subDefenseBonus();
  }

  public static void opportunityBattle(Role role1, Role role2) {
    Tool.print(role1.getNameAndLV() + " 对 " + role2.getNameAndLV() + " 进行借机攻击", 1);
    battleRound(role1, role2, 0);
  }
}
