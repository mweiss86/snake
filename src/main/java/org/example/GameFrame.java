package org.example;

import javax.swing.*;
import java.awt.*;


public class GameFrame extends JFrame  {

    GamePanel panel = new GamePanel();
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
