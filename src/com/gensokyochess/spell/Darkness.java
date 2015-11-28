package com.gensokyochess.spell;

import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

/**
 * 技能：宵暗
 * 代码：R1
 * 作用：提高自身 5 点 AC，降低 10 点 AB，再使用一次取消效果
 */
public class Darkness extends Spell {
  private static int count = 0;

  /**
   * 实例化技能
   */
  public Darkness() {
    super("R1");
  }

  @Override
  public boolean use(Piece piece1) {
    int increase = 5;
    int decrease = 10;
    start(piece1, 1);
    count++;
    if (count == 1) {
      Tool.print(piece1.getNameAndLv() + " 的防御等级提升了 " + increase + " 点", 1);
      piece1.addArmorClass(increase);
      Tool.print(piece1.getNameAndLv() + " 的攻击加值降低了 " + decrease + " 点", 1);
      piece1.subAttackBonus(decrease);
    } else {
      Tool.print(piece1.getNameAndLv() + " 的防御等级降低了 " + increase + " 点", 1);
      piece1.subArmorClass(increase);
      Tool.print(piece1.getNameAndLv() + " 的攻击加值提升了 " + decrease + " 点", 1);
      piece1.addAttackBonus(decrease);
      count = 0;
    }
    return over();
  }
}
