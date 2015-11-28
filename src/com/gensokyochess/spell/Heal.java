package com.gensokyochess.spell;

import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.KingSpellException;

/**
 * 技能：治愈
 * 代码：{1
 * 作用：恢复己方棋子 5 点 HP
 */
public class Heal extends Spell {
  /**
   * 实例化技能
   */
  public Heal() {
    super("{1");
  }

  @Override
  public boolean use(Piece piece1) {
    int recover = 5;
    Piece piece2 = choicePiece();
    if (piece2 != null) {
      if (piece1.getCamp() != piece2.getCamp()) {
        return error(piece1, 3);
      }
      try {
        start(piece1, piece2, 1);
      } catch (KingSpellException e) {
        return error(piece1, 2);
      }
      piece2.addHitPoint(recover);
      Tool.print(piece1.getNameAndLv() + " 恢复了 " + piece2.getNameAndLv() + " " + recover + " 点 HP", 1);
      Tool.print(piece2.getNameAndLv() + " 的 HP 现在是:" + piece2.getCurrentHP(), 1);
      return over();
    } else {
      return error(piece1, 0);
    }
  }
}
