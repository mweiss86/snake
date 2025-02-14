package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;


public class GamePanel extends JPanel {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    char newDirection = 'R';
    boolean running = false;
    Random random;
    static final int UPS = 10;         //Updates per second (game logic speed) - higher = faster | lower = slower
    static final int FPS = 60;  // Frames per second (rendering speed)
    static final long UPDATE_INTERVAL = 1000 / UPS;        // Time per update in ms
    static final long FRAME_TIME = 1000 / FPS;      // Time per frame in ms

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.setDoubleBuffered(true);
    }

    public void startGame() {
        newApple();
        running = true;

        long lastUpdate = System.currentTimeMillis();
        long lastFrame = System.currentTimeMillis();

        while (true) {
            long currentTime = System.currentTimeMillis();

            // Update Game Logic at UPS rate
            if (currentTime - lastUpdate >= UPDATE_INTERVAL) {
                move();
                checkApple();
                checkCollisions();
                lastUpdate = currentTime;
            }

            // Render the Game at FPS rate
            if (currentTime - lastFrame >= FRAME_TIME) {
                repaint();
                lastFrame = currentTime;
            }

            // Sleep tp reduce CPU usage
            try {
                Thread.sleep(1);            // Small sleep to prevent 100% CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        boolean validPosition;
        do{
            validPosition = true;
            appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            //check if new apple position overlaps with any part of the snake
            for ( int i = 0; i < bodyParts; i++){
                if ( x[i] == appleX && y[i] == appleY){
                    validPosition = false;  // Apple is on the snake, regenerate
                    break;
                }
            }
        } while (!validPosition);   // Keep generating until a valid position is found
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (direction != newDirection) {
            direction = newDirection;
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // checks if head collide with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + 70);
        // GameOver text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 30);
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Restart with Space", (SCREEN_WIDTH - metrics3.stringWidth("Restart with Space")) / 2, SCREEN_HEIGHT / 2 + 120);
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        newDirection = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        newDirection = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        newDirection = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        newDirection = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        reset();
                    }
                    break;
            }
        }

        public void reset() {
            bodyParts = 6;
            applesEaten = 0;
            direction = 'R';
            newDirection = 'R';
            random = new Random();
            x = new int[GAME_UNITS];
            y = new int[GAME_UNITS];
            running = true;
        }
    }
}
