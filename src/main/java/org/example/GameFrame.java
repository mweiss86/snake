package org.example;

import javax.swing.*;



public class GameFrame extends JFrame  {

    public GamePanel panel = new GamePanel();
    GameFrame() {
        this.add(panel);
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        panel.startGame();
    }
}
