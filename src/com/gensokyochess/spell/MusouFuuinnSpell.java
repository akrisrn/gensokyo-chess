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
    boolean inputError;
    do {
      Tool.print("请选择一个对方棋子(除国王)");
      Piece piece2 = Tool.findPiece(Tool.input().charAt(0));
      if (piece2 != null && !piece1.getCamp().equals(piece2.getCamp()) && !piece2.isKing()) {
        Tool.print(piece1.getNameAndLV() + " 使用 " + piece1.getSpellName() + " 对 " + piece2.getNameAndLV() +
                " 造成了 " + damage + " 点伤害", 1);
        piece2.hpReduce(damage);
        Tool.print(piece2.getNameAndLV() + " 的 HP 现在是:" + piece2.getCurrentHP(), 1);
        Battle.defeated(piece2);
        piece1.remove(piece2);
        inputError = false;
      } else {
        Tool.print("输入有误");
        inputError = true;
      }
    } while (inputError);
  }
}
