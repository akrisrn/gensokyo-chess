package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

import java.util.ArrayList;

public class MasterSpark extends Spell {
  public MasterSpark(String code) {
    super(code);
  }

  @Override
  public boolean use(Piece piece1) {
    int damage = 3;
    ArrayList<Piece> pieces = null;
    Tool.print("请选择一个方向");
    Tool.removeArrows();
    Tool.drawArrows(piece1, false);

    String input = Tool.input();
    Tool.setActivatedPiece(null);
    Tool.removeArrows();
    if (input.equals(piece1.getCode() + "2") || input.equals("2")) {
      pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 0);
    } else if (input.equals(piece1.getCode() + "8") || input.equals("8")) {
      pieces = Tool.findPieces(false, piece1.getX(), piece1.getY(), 10);
    } else if (input.equals(piece1.getCode() + "4") || input.equals("4")) {
      pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 0);
    } else if (input.equals(piece1.getCode() + "6") || input.equals("6")) {
      pieces = Tool.findPieces(true, piece1.getY(), piece1.getX(), 10);
    } else {
      Tool.print("输入有误");
    }
    if (pieces != null) {
      start(piece1);
      pieces.stream().filter(piece2 -> !piece2.isKing()).forEach(piece2 -> {
        Battle.damage(piece1, piece2, damage);
        piece1.checkAlive(piece2);
      });
      over();
      return true;
    }
    return false;
  }
}
