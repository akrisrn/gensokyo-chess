package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.CanNotMoveException;
import com.gensokyochess.exception.CanNotPlaceException;
import com.gensokyochess.exception.KingMoveException;

/**
 * 技能：彗星
 * 代码：K2
 * 作用：在直线距离上进行移动，如果撞上一个棋子，则对其造成等同于移动的格子数的伤害，然后停止，否则直到移动到棋盘边缘才停止
 */
public class BlazingStar extends Spell {
  /**
   * 实例化技能
   */
  public BlazingStar() {
    super("K2");
  }

  @Override
  public boolean use(Piece piece1) {
    int direction = choiceDirection(piece1, false);
    if (direction != 2 && direction != 4 && direction != 6 && direction !=8) {
      return error(piece1, 0);
    }

    int damage = 0;
    start(piece1, 2);
    while (true) {
      try {
        piece1.moveTo(direction, 0, true, true);
        Tool.updateChessboard();
        Thread.sleep(800 - 100 * damage);
      } catch (CanNotPlaceException e) {
        int[] tmp = Tool.convertMove2XY(piece1.getX(), piece1.getY(), direction);
        assert tmp != null;
        Piece piece2 = Tool.findPiece(tmp[0], tmp[1]);
        if (!piece2.isKing()) {
          Battle.damage(piece1, piece2, damage);
        }
        break;
      } catch (CanNotMoveException | KingMoveException e) {
        break;
      } catch (InterruptedException ignored) {
      }
      damage++;
    }
    return over();
  }
}
