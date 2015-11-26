package com.gensokyochess.spell;

import com.gensokyochess.Piece;
import com.gensokyochess.Tool;
import com.gensokyochess.exception.KingSpellException;
import com.gensokyochess.exception.SameCampException;

public class EvilSealingCircle extends Spell {
  public EvilSealingCircle() {
    super("H2");
  }

  @Override
  public boolean use(Piece piece1) throws KingSpellException, SameCampException {
    Piece piece2 = choicePiece();
    if (piece2 != null) {
      if (piece1.getCamp() == piece2.getCamp()) {
        throw new SameCampException();
      }
      start(piece1, piece2, 2);
      Tool.print(piece2.getNameAndLv() + " 下一行动中无法移动", 1);
      piece2.setCanNotMove(1);
      return over();
    } else {
      return false;
    }
  }
}
