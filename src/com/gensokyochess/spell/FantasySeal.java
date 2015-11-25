package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

public class FantasySeal extends Spell {
  public FantasySeal(String code) {
    super(code);
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 5;
    Tool.print("请选择一个对方棋子(除国王)");
    Tool.removeArrows();

    String input = Tool.input();
    Piece piece2 = null;
    if (input.length() == 3) {
      piece2 = Tool.findPiece(input.charAt(2));
    }
    if (piece2 != null && !piece1.getCamp().equals(piece2.getCamp()) && !piece2.isKing()) {
      start(piece1);
      Battle.damage(piece1, piece2, damage);
      piece1.checkAlive(piece2);
      over();
      return true;
    } else {
      Tool.print("输入有误");
      return false;
    }
  }
}
