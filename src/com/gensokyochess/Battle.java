package com.gensokyochess;

import com.gensokyochess.exception.InRiverException;

/**
 * 战斗类
 */
public class Battle {
  /**
   * 造成一次伤害
   *
   * @param role1  造成伤害的角色
   * @param role2  承受伤害的角色
   * @param damage 伤害值
   */
  public static void damage(Role role1, Role role2, int damage) {
    damage(role1, role2, damage, 0);
  }

  /**
   * 造成两次伤害
   *
   * @param role1   造成伤害的角色
   * @param role2   承受伤害的角色
   * @param damage1 第一个伤害值
   * @param damage2 第二个伤害值
   */
  public static void damage(Role role1, Role role2, int damage1, int damage2) {
    String damage;
    if (damage2 != 0) {
      damage = damage1 + damage2 + "(" + damage1 + "+" + damage2 + ")";
    } else {
      damage = "" + damage1;
    }
    role2.reduceHp(damage1 + damage2);
    Tool.print(role1.getNameAndLv() + " 对 " + role2.getNameAndLv() + " 造成了 " + damage + " 点伤害", 1);
    Tool.print(role2.getNameAndLv() + " 的 HP 现在是:" + role2.getCurrentHP(), 1);
    isDefeated(role2);
  }

  /**
   * 普通攻击
   *
   * @param role1 攻击的角色
   * @param role2 被攻击的角色
   */
  private static void normalAtk(Role role1, Role role2) {
    Tool.print(role1.getNameAndLv() + " 的攻击击中了!", 1);
    int damage = role1.rollDamage();
    damage(role1, role2, damage);
  }

  /**
   * 重击
   *
   * @param role1 攻击的角色
   * @param role2 被攻击的角色
   */
  private static void criticalAtk(Role role1, Role role2) {
    Tool.print(role1.getNameAndLv() + " 造成了重击!", 1);
    int damage1 = role1.rollDamage();
    int damage2 = role1.rollDamage();
    damage(role1, role2, damage1, damage2);
  }

  /**
   * 检查棋子是否被打倒了
   *
   * @param role 检查的棋子
   */
  private static void isDefeated(Role role) {
    if (role.getCurrentHP() <= 0) {
      Tool.print(role.getNameAndLv() + " 被打倒了!", 1);
      role.setAlive(false);
      role.recoverHP();
    }
  }

  /**
   * 根据调整值的正负处理加减号
   *
   * @param bonus 调整值
   * @return 加号或空
   */
  private static String handleSign(int bonus) {
    if (bonus >= 0) {
      return "+";
    } else {
      return "";
    }
  }

  /**
   * 一次战斗回合
   *
   * @param role1    攻击的角色
   * @param role2    被攻击的角色
   * @param distance 之间的距离
   */
  private static void battleRound(Role role1, Role role2, int distance) {
    if (role1.getAttackType() == 1) {
      role1.recoverAB();
      role1.weakenRemote(distance);
    }

    int armorClass = role2.getArmorClass();
    int attackRoll = role1.rollAttack();
    int attackBonus = role1.getCurrentAB();
    String sign = handleSign(attackBonus);
    int originalRoll = attackRoll - attackBonus;

    Tool.print(role1.getNameAndLv() + " 的攻击检定为:" +
            attackRoll + "(" + originalRoll + sign + attackBonus + ")", 1);

    if (originalRoll == 1) {
      Tool.print(role1.getNameAndLv() + " 的攻击失手了!", 1);
    } else if (originalRoll == 20) {
      Tool.print(role1.getNameAndLv() + " 造成了重击威胁!", 1);
      attackRoll = role1.rollAttack();
      originalRoll = attackRoll - attackBonus;
      Tool.print(role1.getNameAndLv() + " 的第二次攻击检定为:" +
              attackRoll + "(" + originalRoll + sign + attackBonus + ")", 1);
      if (attackRoll > armorClass) {
        criticalAtk(role1, role2);
      } else {
        Tool.print(role1.getNameAndLv() + " 未造成重击", 1);
        normalAtk(role1, role2);
      }
    } else if (attackRoll > armorClass) {
      normalAtk(role1, role2);
    } else if (attackRoll == armorClass) {
      if (role1.getStrength() > role2.getStrength()) {
        normalAtk(role1, role2);
      } else {
        Tool.print(role1.getNameAndLv() + " 的攻击失手了!", 1);
      }
    } else {
      Tool.print(role1.getNameAndLv() + " 的攻击失手了!", 1);
    }
  }

  /**
   * 对抗先攻检定
   *
   * @param role1 攻击的角色
   * @param role2 被攻击的角色
   * @return 是否是攻击的角色先攻
   */
  private static boolean rivalInitiative(Role role1, Role role2) {
    int initiativeRoll1 = role1.rollInitiative();
    int initiative1 = role1.getInitiative();
    int originalRoll1 = initiativeRoll1 - initiative1;

    int initiativeRoll2 = role2.rollInitiative();
    int initiative2 = role2.getInitiative();
    int originalRoll2 = initiativeRoll2 - initiative2;

    Tool.print(role1.getNameAndLv() + " 的先攻检定为:" +
            initiativeRoll1 + "(" + originalRoll1 + handleSign(initiative1) + initiative1 + ")", 1);
    Tool.print(role2.getNameAndLv() + " 的先攻检定为:" +
            initiativeRoll2 + "(" + originalRoll2 + handleSign(initiative2) + initiative2 + ")", 1);

    if (initiativeRoll1 > initiativeRoll2) {
      Tool.print(role1.getNameAndLv() + " 先攻!", 1);
      return true;
    } else if (initiativeRoll1 < initiativeRoll2) {
      Tool.print(role2.getNameAndLv() + " 先攻!", 1);
    } else {
      if (role1.getDexterity() > role2.getDexterity()) {
        Tool.print(role1.getNameAndLv() + " 先攻!", 1);
        return true;
      } else {
        Tool.print(role2.getNameAndLv() + " 先攻!", 1);
      }
    }
    return false;
  }

  /**
   * 正面战斗
   *
   * @param role1 攻击的角色
   * @param role2 被攻击的角色
   * @throws InRiverException 如果有其中一个角色在河中，则抛出异常
   */
  public static void frontalBattle(Role role1, Role role2) throws InRiverException {
    start(1, role1, role2, 0);
    boolean isRole1First = rivalInitiative(role1, role2);
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
    over();
    role1.clearDefenseBonus();
    role2.clearDefenseBonus();
  }

  /**
   * 远程战斗
   *
   * @param role1    攻击的角色
   * @param role2    被攻击的角色
   * @param distance 之间的距离
   * @throws InRiverException 有角色在河中
   */
  public static void remoteBattle(Role role1, Role role2, int distance) throws InRiverException {
    start(2, role1, role2, distance);
    battleRound(role1, role2, distance);
    over();
    role1.clearDefenseBonus();
    role2.clearDefenseBonus();
  }

  /**
   * 借机攻击
   *
   * @param role1 攻击的角色
   * @param role2 被攻击的角色
   * @throws InRiverException 角色在河中
   */
  public static void opportunityBattle(Role role1, Role role2) throws InRiverException {
    start(0, role1, role2, 0);
    battleRound(role1, role2, 0);
    over();
  }

  /**
   * 战斗开始
   *
   * @param battleType 战斗的类型（0：借机，1：正面，2：远程）
   * @param role1      攻击的角色
   * @param role2      被攻击的角色
   * @param distance   之间的距离
   * @throws InRiverException 有角色在河中
   */
  private static void start(int battleType, Role role1, Role role2, int distance) throws InRiverException {
    Tool.locked();
    if (distance == 0 && (role1.isInRiver() || role2.isInRiver())) {
      Tool.unlock();
      throw new InRiverException();
    }
    if (battleType == 1) {
      Tool.print(role1.getNameAndLv() + " 和 " + role2.getNameAndLv() + " 展开正面战斗", 1);
    } else if (battleType == 2) {
      if (distance != 0) {
        Tool.print(role1.getNameAndLv() + " 对 " + role2.getNameAndLv() + " 进行远程攻击", 1);
      } else {
        Tool.print(role1.getNameAndLv() + " 对 " + role2.getNameAndLv() + " 进行近战攻击", 1);
      }
    } else {
      Tool.print(role1.getNameAndLv() + " 对 " + role2.getNameAndLv() + " 进行借机攻击", 1);
    }
  }

  /**
   * 战斗结束
   */
  private static void over() {
    Tool.print("战斗结束", 1);
    Tool.unlock();
  }
}
