package gameEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class WaveTimer implements ActionListener {
    private static final int DELAY_MS = 100;

    private Timer timer;
    private GameState gameState;
    private GameComponent gameComponent;

    public WaveTimer(GameComponent gameComponent, GameState gameState) {
        this.gameComponent = gameComponent;
        this.gameState = gameState;
        this.timer = new Timer(DELAY_MS, this);
    }

    public void start() {
        this.gameState.startWave();
        this.timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.gameState.isActiveWave()) {
            this.timer.stop();
            this.gameState.endWave();
            this.gameComponent.repaint();
            return;
        }

        this.gameComponent.updateGameState();
        this.gameComponent.repaint();
    }
}
