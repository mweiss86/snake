package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel {

    //TODO maybe Menu, settings, auto speedchange,

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
    Random random = new Random();

    // Game speed control
    static final int UPS = 10; //Updates per second (game logic speed) - higher = faster | lower = slower
    static final int FPS = 60; // Frames per second (rendering speed)
    static final long UPDATE_INTERVAL = 1000 / UPS;        // Time per update in ms
    static final long FRAME_TIME = 1000 / FPS;      // Time per frame in ms
    int fps;
    int frameCount;
    long lastFpsUpdate = System.currentTimeMillis();

    private ScheduledExecutorService executor;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.setDoubleBuffered(true);
    }

    public void startGame() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow(); // Stop any existing game loops
        }

        executor = Executors.newScheduledThreadPool(2);
        reset();
        running = true;

        executor.scheduleAtFixedRate(this::updateGame, 0, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::repaintGame, 0,FRAME_TIME, TimeUnit.MILLISECONDS);
    }

    private synchronized void updateGame() {
        if (!running) return;
        move();
        checkApple();
        checkCollisions();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private synchronized void draw(Graphics g) {
        if (running) {
            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                g.setColor(i == 0 ? Color.green : new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

            // Draw FPS counter
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("FPS: " + fps, 10, 20);

            // Update FPS
            frameCount++;
            if (System.currentTimeMillis() - lastFpsUpdate >= 1000) {
                fps = frameCount;
                frameCount = 0;
                lastFpsUpdate = System.currentTimeMillis();
            }
        } else {
            gameOver(g);
        }
    }

    private synchronized void newApple() {
        boolean validPosition;
        do {
            validPosition = true;
            appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            // check if new apple position overlaps with any part of the snake
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    validPosition = false;  // Apple is on snake, regenerate
                    break;
                }
            }
        } while (!validPosition); // Keep generating until a valid position is found
    }

    private synchronized void move() {
        // Update the new direction at the start of move
        if(direction!=newDirection) {
            direction = newDirection;
        }
        // Move body parts
        System.arraycopy(x, 0, x, 1, bodyParts);
        System.arraycopy(y, 0, y, 1, bodyParts);

        // Move head
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private synchronized void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private synchronized void checkCollisions() {
        // checks if head collide with body
        for (int i = 1; i < bodyParts; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                return;
            }
        }
        // check if head touches left or right or top or bottom border
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
    }

    private synchronized void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over",  (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 30);
        // Score text
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + 70);
        // Restart text
        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Restart with Space", (SCREEN_WIDTH - metrics3.stringWidth("Restart with Space")) / 2, SCREEN_HEIGHT / 2 + 120);
    }

    private void reset() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        newDirection = 'R';
        running = true;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        newApple();
    }

    private synchronized void repaintGame(){
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: {
                    if (direction != 'R') {
                        newDirection = 'L';
                    }
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    if (direction != 'L') {
                        newDirection = 'R';
                    }
                    break;
                }
                case KeyEvent.VK_UP: {
                    if (direction != 'D') {
                        newDirection = 'U';
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (direction != 'U') {
                        newDirection = 'D';
                    }
                    break;
                }
                case KeyEvent.VK_SPACE: {
                    if (!running) {
                        startGame();
                    }
                    break;
                }
            }
        }
    }
}