package com.gensokyochess.spell;

import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

/**
 * 技能：猛虎内劲
 * 代码：_1
 * 作用：提高自身 5 点的伤害加值，降低 5 点的攻击加值
 */
public class FierceTigerEnergyRelease extends Spell {
  private static int count = 0;

  /**
   * 实例化技能
   */
  public FierceTigerEnergyRelease() {
    super("_1");
  }

  @Override
  public boolean use(Piece piece1) {
    int increase = 5;
    int decrease = 5;
    start(piece1, 1);
    count++;
    if (count == 1) {
      Tool.print(piece1.getNameAndLv() + " 的伤害加值提升了 " + increase + " 点", 1);
      piece1.addDamageBonus(increase);
      Tool.print(piece1.getNameAndLv() + " 的攻击加值降低了 " + decrease + " 点", 1);
      piece1.subAttackBonus(decrease);
    } else {
      Tool.print(piece1.getNameAndLv() + " 的伤害加值降低了 " + increase + " 点", 1);
      piece1.subDamageBonus(increase);
      Tool.print(piece1.getNameAndLv() + " 的攻击加值提升了 " + decrease + " 点", 1);
      piece1.addAttackBonus(decrease);
      count = 0;
    }
    return over();
  }
}
