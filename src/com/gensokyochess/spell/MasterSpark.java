package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

import java.util.ArrayList;

public class MasterSpark extends Spell {
  public MasterSpark() {
    super("K1");
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 3;
    ArrayList<Piece> pieces = null;
    int direction = choiceDirection(piece1, false);
    if (direction == 2) {
      pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 0);
    } else if (direction == 8) {
      pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 10);
    } else if (direction == 4) {
      pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 0);
    } else if (direction == 6) {
      pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 10);
    }
    if (pieces != null) {
      start(piece1, 1);
      pieces.stream().filter(piece2 -> !piece2.isKing()).forEach(piece2 -> {
        Battle.damage(piece1, piece2, damage);
        piece1.checkAlive(piece2);
      });
      return over();
    } else {
      return error(piece1, 0);
    }
  }
}
