package com.gensokyochess.spell;

import com.gensokyochess.Battle;
import com.gensokyochess.Chessboard;
import com.gensokyochess.Piece;
import com.gensokyochess.Tool;

import java.util.ArrayList;

public class MusouFuuinnSpell extends Spell {
  public MusouFuuinnSpell(String code) {
    super(code);
  }

  @Override
  public void use(Piece piece1, Chessboard chessboard, ArrayList<Piece> pieces) {
    boolean inputError;
    do {
      Tool.print("请输入一个对方棋子(除国王):", false);
      Piece piece2 = Tool.findPiece(Tool.input().charAt(0), pieces);
      if (piece2 != null && !piece1.getCamp().equals(piece2.getCamp()) && !piece2.isKing()) {
        Tool.print(piece1.getNameAndLV() + " 使用 " + piece1.getSpellName() + " 对 " + piece2.getNameAndLV() +
                " 造成了 3 点伤害", 1);
        piece2.hpReduce(3);
        Tool.print(piece2.getNameAndLV() + " 的 HP 现在是:" + piece2.getCurrentHP(), 1);
        Battle.defeated(piece2);
        piece1.removeLoser(piece2, chessboard.getChessboard());
        inputError = false;
      } else {
        Tool.print("输入有误");
        inputError = true;
      }
    } while (inputError);
  }
}
