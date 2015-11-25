package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

import java.util.ArrayList;

public class MasterSparkSpell extends Spell {
  public MasterSparkSpell(String code) {
    super(code);
  }

  @Override
  public void use(Piece piece1) {
    int damage = 3;
    ArrayList<Piece> pieces = null;
    boolean inputError = false;
    do {
      Tool.print("请选择一个攻击方向(2/4/6/8)");
      int input = Character.getNumericValue(Tool.input().charAt(0));
      if (input == 2) {
        pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 0);
      } else if (input == 8) {
        pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 10);
      } else if (input == 4) {
        pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 0);
      } else if (input == 6) {
        pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 10);
      } else {
        Tool.print("输入有误");
        inputError = true;
      }
    } while (inputError);

    start(piece1);
    pieces.stream().filter(piece2 -> !piece2.isKing()).forEach(piece2 -> {
      Battle.damage(piece1, piece2, damage);
      piece1.remove(piece2);
    });
    over();
  }
}
