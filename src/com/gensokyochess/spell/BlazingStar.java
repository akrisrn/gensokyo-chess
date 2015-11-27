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
        int[] tmp = Tool.handleMove2XY(piece1, direction);
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
