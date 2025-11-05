package gameEngine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class GameComponent extends JComponent implements MouseListener, KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int WAVE_START_MESSAGE_TICKS = 40;

    private GameState gameState;
    private WaveTimer waveTimer;

    private int waveStartMessageTicksRemaining;

    public GameComponent() {
        this.gameState = new GameState();
        this.waveStartMessageTicksRemaining = 0;
        this.waveTimer = new WaveTimer(this, this.gameState);
        this.addMouseListener(this);
        this.addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Set custom font
        int fontSize = 18;
        String fontFilePath = "resources/fonts/PressStart2P-Regular.ttf";

        try {
            // Load the font from a file
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilePath));
            // Register the font with the GraphicsEnvironment (optional, but good practice)
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            // Derive a new font with the desired size
            g2d.setFont(customFont.deriveFont(Font.PLAIN, fontSize));
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font from " + fontFilePath + ": " + e.getMessage());
            // Handle the error (e.g., return a default font)
            g2d.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        }

        this.gameState.drawAll(g2d);

        this.drawWaveMessage(g2d);
    }

    private void drawWaveMessage(Graphics2D g2d) {
        if (this.waveStartMessageTicksRemaining > 0) {
            int elapsed = WAVE_START_MESSAGE_TICKS - this.waveStartMessageTicksRemaining;
            double angle = 3 * Math.PI * elapsed / WAVE_START_MESSAGE_TICKS; // 0..3pi over duration
            int gray = (int) Math.round((Math.sin(angle) * 0.5 + 0.5) * 255); // 0..255
            g2d.setColor(new Color(gray, gray, gray));
            int xOffset = Cell.SQUARE_SIZE;
            int yOffset = -Cell.SQUARE_SIZE / 2;
            g2d.drawString("Wave " + this.gameState.getCurrentWaveNumber(), this.getWidth() / 2 - xOffset, this.getHeight() / 2 + yOffset);
            this.waveStartMessageTicksRemaining--;
        }
    }

    public void setSelectedTowerType(String towerType) {
        this.gameState.setSelectedTowerType(towerType);
    }

    public void updateGameState() {
        this.gameState.updateState();
    }

    public void mouseClicked(MouseEvent e) {
        this.gameState.placeOrRotateTower(Cell.getCellFromCoordinates(e.getX(), e.getY()), e.getButton() == MouseEvent.BUTTON1);
        this.repaint();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();
            if (button.getText().equals("Start Wave")) {
                if (!this.gameState.isActiveWave()) {
                    this.displayWaveStartMessage();
                    this.waveTimer.start();
                }
            } else if (button.getText().equals("Upgrade Tower")) {
                this.gameState.upgradeSelectedTower();
                this.repaint();
            }
        } else if (e.getSource() instanceof JComboBox) {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            this.gameState.setSelectedTowerType(comboBox.getSelectedItem().toString());
        }
    }

    /**
     * Displays a message to the user that a new wave has started.
     */
    private void displayWaveStartMessage() {
        // Draw the message centered in the screen throughout the next WAVE_START_MESSAGE_TICKS ticks
        this.waveStartMessageTicksRemaining = WAVE_START_MESSAGE_TICKS;
        this.repaint();
    }
}
