package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.CanNotMoveException;
import com.gensokyochess.exception.CanNotPlaceException;
import com.gensokyochess.exception.KingMoveException;

public class BlazingStar extends Spell {
  public BlazingStar() {
    super("K2");
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 5;
    int direction = choiceDirection(piece1, false);
    if (direction != 2 && direction != 4 && direction != 6 && direction !=8) {
      Tool.print("输入有误");
      return false;
    }

    start(piece1, 2);
    while (true) {
      try {
        piece1.moveTo(direction, 0, true);
        Tool.updateChessboard();
        Thread.sleep(800);
      } catch (CanNotPlaceException e) {
        int[] tmp = Tool.handleMove2XY(piece1, direction);
        assert tmp != null;
        Piece piece2 = Tool.findPiece(tmp[0], tmp[1]);
        Battle.damage(piece1, piece2, damage);
        break;
      } catch (CanNotMoveException | KingMoveException e) {
        break;
      } catch (InterruptedException ignored) {
      }
    }
    return over();
  }
}
