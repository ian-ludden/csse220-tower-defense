package gameEngine;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import gameObjects.Enemy;
import gameObjects.GridSnappedObject;
import gameObjects.Projectile;
import gameObjects.Tower;
import gameObjects.towers.ArcherTower;
import gameObjects.towers.MissileTower;

/**
 * Manages the state of the game.  
 * This class is responsible for 
 * - tracking the budget remaining
 * - tracking the selected tower type
 * - tracking the current level
 * - tracking the towers, enemies, and projectiles
 * - tracking the grid objects
 * - handling collisions
 * - updating the game state
 * - drawing the game state
 */
public class GameState {
    private static final int ENEMY_SPAWN_DELAY_TICKS = 8;
    private static final int BUDGET_WAVE_NUMBER_MULTIPLIER = 1;
    private static final int DEFAULT_NUM_LIVES = 5;

    private int budgetRemaining;
    private int livesRemaining;
    private String selectedTowerType;

    private Tower selectedTower;

    private Level currentLevel;
    private ArrayList<Tower> towers = new ArrayList<Tower>();
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private GridSnappedObject[][] gridObjects;



    public GameState() {
        this.gridObjects = new GridSnappedObject[Level.NUM_ROWS][Level.NUM_COLS];
        this.selectedTower = null;
        this.selectedTowerType = TowerDefenseMain.DEFAULT_TOWER_TYPE;
        this.currentLevel = new Level("levels/level01.csv");
        this.budgetRemaining = this.currentLevel.getBudget();
        this.livesRemaining = DEFAULT_NUM_LIVES;
    }
    
    private void handleCollisions() {
        for (Enemy enemy : this.enemies) {
            for (Projectile projectile : this.projectiles) {
                if (projectile.intersects(enemy)) {
                    projectile.collideWith(enemy);
                }
            }
        }

        // Clean up removed objects
        this.enemies.removeIf(Enemy::shouldRemove);
        this.projectiles.removeIf(Projectile::shouldRemove);
    }

    /**
     * Places a tower at the given cell if possible, 
     * or rotates the tower at the given cell if it is already placed. 
     * 
     * @param cell
     */
    public void placeOrRotateTower(Cell cell, boolean isLeftClick) {
        if (this.selectedTowerType == null) {
            return;
        }

        // Deny if invalid tower location
        if (!this.currentLevel.isValidTowerLocation(cell)) {
            JOptionPane.showMessageDialog(null, "Invalid tower location!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Rotate if tower already exists at that location and is selected
        for (Tower tower : this.towers) {
            if (!tower.getCell().equals(cell)) {
                continue;
            }
            if (tower == this.selectedTower) {
                tower.rotate(isLeftClick);
                return;
            } else {
                this.selectedTower = tower;
                return;
            }
        }

        // Place new tower
        Tower tower = this.constructNewTower(this.selectedTowerType, cell);

        if (this.budgetRemaining < tower.getCostToBuild()) {
            JOptionPane.showMessageDialog(null, "Not enough budget to build selected tower!");
            return;
        }
        this.budgetRemaining -= tower.getCostToBuild();
        this.towers.add(tower);
        this.selectedTower = tower;
    }

    /**
     * Upgrades the currently selected tower if the budget allows.
     */
    public void upgradeSelectedTower() {
        if (this.selectedTower == null) {
            JOptionPane.showMessageDialog(null, "No tower selected for upgrade!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int upgradeCost = this.selectedTower.getCostToUpgrade();
        if (this.budgetRemaining < upgradeCost) {
            JOptionPane.showMessageDialog(null, "Not enough budget to upgrade selected tower!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.budgetRemaining -= upgradeCost;
        this.selectedTower.upgrade();
    }

    private Tower constructNewTower(String towerType, Cell cell) {
        if (towerType.equals("Archer")) {
            return new ArcherTower(cell);
        } else if (towerType.equals("Missile")) {
            return new MissileTower(cell);
        } else {//if (towerType.equals("Laser")) {
            // return new LaserTower();
            throw new IllegalArgumentException("Unsupported tower type: " + towerType);
        }
    }

    /**
     * Draws the remaining lives in the top left corner.
     * @param g2d
     */
    private void drawLivesRemaining(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.drawString("Lives: " + this.livesRemaining, Cell.SQUARE_SIZE / 4, Cell.SQUARE_SIZE / 2);
    }

    public boolean isActiveWave() {
        return !this.enemies.isEmpty();
    }

    public void endWave() {
        this.projectiles.clear();
        this.budgetRemaining += BUDGET_WAVE_NUMBER_MULTIPLIER * this.getCurrentWaveNumber() * this.currentLevel.getBudget();

        if (this.getCurrentWaveNumber() >= this.currentLevel.getTotalWaves()) {
            JOptionPane.showMessageDialog(null, "Level Complete! Advancing to next level.");
            boolean hasNextLevel = this.advanceLevel();
            if (!hasNextLevel) {
                JOptionPane.showMessageDialog(null, "Congratulations! You have completed all levels!");
                System.exit(0);
            }
        }
    }

    /**
     * Creates the next wave of enemies. 
     */
    public void startWave() {
        if (this.isActiveWave()) {
            return;
        }

        this.loadNewEnemyWave();
    }

    public int getCurrentWaveNumber() {
        return this.currentLevel.getWaveNumber();
    }

    private void loadNewEnemyWave() {
        ArrayList<Enemy> wave = this.currentLevel.getNextWave();
        // Apply staggered spawn delays so enemies begin moving at different ticks
        for (int i = 0; i < wave.size(); i++) {
            Enemy e = wave.get(i);
            e.setSpawnDelayTicks(i * ENEMY_SPAWN_DELAY_TICKS);
        }
        this.enemies.addAll(wave);
    }

    public void updateState() {
        for (Enemy enemy : this.enemies) {
            boolean didNotReachEnd = enemy.advance(this.currentLevel);
            if (!didNotReachEnd) {
                this.livesRemaining--;
                if (this.livesRemaining <= 0) {
                    JOptionPane.showMessageDialog(null, "Game Over! You have run out of lives.");
                    System.exit(0);
                }
            }
        }
        this.projectiles.forEach(Projectile::fly);

        for (Tower tower : this.towers) {
            Projectile newProjectile = tower.updateState();
            if (newProjectile != null) {
                this.projectiles.add(newProjectile);
            }
        }

        this.handleCollisions();
    }

    public boolean advanceLevel() {
        this.currentLevel = this.currentLevel.getNextLevel();
        this.towers.clear();
        return currentLevel != null;
    }

    public void drawAll(Graphics2D g2d) {
//        int fontSize = 24;
//        Font textFont = new Font("Comic Sans MS", Font.BOLD, fontSize);

        if (this.currentLevel != null) {
            this.currentLevel.drawOn(g2d);
        }
        
        for (Tower tower : this.towers) {
            tower.drawOn(g2d);
        }

        if (selectedTower != null) {
            selectedTower.drawHighlightedCell(g2d);
        }

        g2d.setColor(Color.PINK);
        for (Enemy enemy : this.enemies) {
            enemy.drawOn(g2d);
        }
        for (Projectile projectile : this.projectiles) {
            projectile.drawOn(g2d);
        }

        this.drawHUD(g2d);
    }

    public void drawHUD(Graphics2D g2d) {
        // Draw remaining budget in top right corner
        String budgetText = "Budget: $" + this.budgetRemaining;
        g2d.setColor(Color.BLACK);
        g2d.drawString(budgetText, TowerDefenseMain.GAME_WINDOW_SIZE.width - Cell.SQUARE_SIZE * 3,  Cell.SQUARE_SIZE / 2);

        // Draw remaining lives in top left corner
        this.drawRemainingLives(g2d);
    }

    /**
     * Draws the remaining lives in the top left corner.
     * @param g2d
     */
    private void drawRemainingLives(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.drawString("Lives: " + this.livesRemaining, Cell.SQUARE_SIZE / 4, Cell.SQUARE_SIZE / 2);
    }

    public void setSelectedTowerType(String towerType) {
        this.selectedTowerType = towerType;
    }
}
