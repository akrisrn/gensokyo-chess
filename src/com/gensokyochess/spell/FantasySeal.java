package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;

public class FantasySeal extends Spell {
  public FantasySeal(String code) {
    super(code);
  }

  @Override
  public boolean use(Piece piece1) throws KingSpellException, SameCampException {
    int damage = 3;

    Piece piece2 = choiceAPiece();
    if (piece2 != null) {
      if (piece1.getCamp().equals(piece2.getCamp())) {
        throw new SameCampException();
      }
      if (piece2.isKing()) {
        throw new KingSpellException();
      }
      start(piece1);
      Battle.damage(piece1, piece2, damage);
      piece1.checkAlive(piece2);
      over();
      return true;
    } else {
      return false;
    }
  }
}
