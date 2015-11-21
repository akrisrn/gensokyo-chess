package com.gensokyochess;

import javax.swing.*;
import java.util.Scanner;

public class GuiFrame extends JFrame {
  private JPanel MainPanel;
  private JTextArea Board;
  private JTextField InputArea;
  private JTextArea LogArea;
  private JButton SendButton;
  private Scanner scanner = new Scanner("");

  public GuiFrame() {
    setTitle("Gensokyo & Chess");
    setResizable(false);
    setContentPane(MainPanel);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setVisible(true);

    Chessboard chessboard = new Chessboard();
    Board.setText(String.valueOf(chessboard.getChessboard()));

    getRootPane().setDefaultButton(SendButton);
    SendButton.addActionListener(actionEvent -> {
      String line = InputArea.getText();
      if (!(line.equals(""))) {
        scanner = new Scanner(line);
        appendLog(line, true);
        InputArea.setText("");
      }
    });
  }

  public void updateChessboard(String chessboard) {
    Board.setText(chessboard);
  }

  public void appendLog(String text, boolean isALine) {
    if (isALine) {
      LogArea.append(text + "\n");
    } else {
      LogArea.append(text);
    }
    LogArea.setCaretPosition(LogArea.getText().length());
  }

  public Scanner getScanner() {
    return scanner;
  }
}
