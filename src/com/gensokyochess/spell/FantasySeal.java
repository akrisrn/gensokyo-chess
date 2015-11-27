package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.exception.KingSpellException;

public class FantasySeal extends Spell {
  public FantasySeal() {
    super("H1");
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 3;
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
