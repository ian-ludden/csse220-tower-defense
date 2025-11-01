package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import gameEngine.TowerDefenseMain;

public abstract class Projectile extends DrawableObject {

    private static final int DEFAULT_WIDTH = 3;
    private static final int DEFAULT_LENGTH = 10;
    private static final Color DEFAULT_COLOR = Color.RED;

    private static final int DEFAULT_DAMAGE = 1;
    private static final double DEFAULT_SPEED = 10;
    
    protected Point2D location;
    protected double launchAngleDegrees;
    private int towerLevel;
    protected double speed;
    protected int damagePoints;
    protected boolean isArmorPiercing;
    private boolean shouldRemove;

    public Projectile(Point2D startLocation, double launchAngleDegrees, int towerLevel) {
        this.location = startLocation;
        this.launchAngleDegrees = launchAngleDegrees;
        this.towerLevel = towerLevel;
        this.speed = DEFAULT_SPEED;
        this.damagePoints = DEFAULT_DAMAGE * towerLevel;
        this.isArmorPiercing = false;
        this.shouldRemove = false;
    }

    public boolean isArmorPiercing() {
        return isArmorPiercing;
    }

    public int getDamage() {
        return damagePoints;
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public void markToRemove() {
        this.shouldRemove = true;
    }

    /**
     * Move the projectile forward.
     * 
     * @return true if the projectile has left the visible window, false otherwise
     */
    public boolean fly() {
        this.location.setLocation(this.location.getX() + Math.cos(Math.toRadians(this.launchAngleDegrees)) * this.speed, this.location.getY() + Math.sin(Math.toRadians(this.launchAngleDegrees)) * this.speed);
        return isOutOfBounds();
    }

    private boolean isOutOfBounds() {
        return this.location.getX() < 0 
            || this.location.getX() > TowerDefenseMain.GAME_WINDOW_SIZE.width 
            || this.location.getY() < 0 
            || this.location.getY() > TowerDefenseMain.GAME_WINDOW_SIZE.height;
    }

    public void collideWith(Enemy enemy) {
        enemy.collideWith(this);
        this.markToRemove();
    }

    @Override
    public void drawFallbackShape(Graphics2D g2d) {
        // Draw a simple rectangle for the projectile, rotated by launchAngle
        Rectangle2D rect = new Rectangle2D.Double(getX(), getY(), DEFAULT_LENGTH, DEFAULT_WIDTH);
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.rotate(Math.toRadians(launchAngleDegrees), rect.getCenterX(), rect.getCenterY());
        g2.setColor(DEFAULT_COLOR);
        g2.fill(rect);
        g2.dispose();
    }

    @Override
    public int getX() {
        return (int) location.getX();
    }

    @Override
    public int getY() {
        return (int) location.getY();
    }

    public boolean intersects(Enemy enemy) {
        Rectangle2D enemyRect = enemy.getBoundingBox();
        Rectangle2D projectileRect = this.getBoundingBox();
        return enemyRect.intersects(projectileRect);
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D.Double(getX(), getY(), DEFAULT_WIDTH, DEFAULT_LENGTH);
    }
}
