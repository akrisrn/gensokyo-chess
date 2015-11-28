package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

import java.util.ArrayList;

/**
 * 技能：完美冻结
 * 代码：91
 * 作用：以棋子威胁区域内的空白格为原点，每一个格子生成一个随机的方向，对这些方向上遇到的第一个棋子造成 3 点伤害
 */
public class PerfectFreeze extends Spell {
  /**
   * 实例化技能
   */
  public PerfectFreeze() {
    super("91");
  }

  @Override
  public boolean use(Piece piece1) {
    start(piece1, 1);
    ArrayList<Integer> directions = Tool.markDirection(piece1.getX(), piece1.getY());
    for (int direction : directions) {
      int[] tmp = Tool.convertMove2XY(piece1.getX(), piece1.getY(), direction);
      assert tmp != null;
      int x = tmp[0];
      int y = tmp[1];
      int randDirection = (int) (Math.random() * 9 + 1);
      switch (randDirection) {
        case 1:
          for (; x > 0 && y > 0; x--, y--) damage(x, y, piece1);
          break;
        case 2:
          for (; y > 0; y--) damage(x, y, piece1);
          break;
        case 3:
          for (; x < 10 && y > 0; x++, y--) damage(x, y, piece1);
          break;
        case 4:
          for (; x > 0; x--) damage(x, y, piece1);
          break;
        case 6:
          for (; x < 10; x++) damage(x, y, piece1);
          break;
        case 7:
          for (; x > 0 && y < 10; x--, y++) damage(x, y, piece1);
          break;
        case 8:
          for (; y < 10; y++) damage(x, y, piece1);
          break;
        case 9:
          for (; x < 10 && y < 10; x++, y++) damage(x, y, piece1);
          break;
      }
    }
    return over();
  }

  private void damage(int x, int y, Piece piece1) {
    int damage = 3;
    Piece piece2 = Tool.findPiece(x, y);
    if (piece2 != null && !piece2.equals(piece1) && !piece2.isKing()) {
      Battle.damage(piece1, piece2, damage);
      piece1.checkAlive(piece2);
      Tool.updateChessboard();
    }
  }
}
