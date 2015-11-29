package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.exception.KingSpellException;

/**
 * 技能：梦想封印
 * 代码：H1
 * 作用：对一个对方棋子造成 5 点伤害
 */
public class FantasySeal extends Spell {
  /**
   * 实例化技能
   */
  public FantasySeal() {
    super("H1");
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 5;
    Piece piece2 = choicePiece();
    if (piece2 != null) {
      if (piece1.getCamp() == piece2.getCamp()) {
        return error(piece1, 1);
      }
      try {
        start(piece1, piece2, 1);
      } catch (KingSpellException e) {
        return error(piece1, 2);
      }
      Battle.damage(piece1, piece2, damage);
      piece1.checkAlive(piece2);
      return over();
    } else {
      return error(piece1, 0);
    }
  }
}
