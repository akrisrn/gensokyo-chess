package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

public class MusouFuuinnSpell extends Spell {
  public MusouFuuinnSpell(String code) {
    super(code);
  }

  @Override
  public void use(Piece piece1) {
    int damage = 5;
    boolean inputError = false;
    do {
      Tool.print("请选择一个对方棋子(除国王)");
      Piece piece2 = Tool.findPiece(Tool.input().charAt(0));
      if (piece2 != null && !piece1.getCamp().equals(piece2.getCamp()) && !piece2.isKing()) {
        start(piece1);
        Battle.damage(piece1, piece2, damage);
        piece1.remove(piece2);
        over();
      } else {
        Tool.print("输入有误");
        inputError = true;
      }
    } while (inputError);
  }
}
