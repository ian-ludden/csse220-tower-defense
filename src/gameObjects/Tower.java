package gameObjects;

import java.awt.*;

import gameEngine.Cell;

/**
 * Represents a tower in the game. 
 * A tower fires projectiles at enemies at a fixed rate. 
 * Towers can be upgraded to increase their fire rate and/or damage. 
 * When a tower is placed, it can be rotated to face a different direction. 
 */
public abstract class Tower extends DrawableObject {
    public static final int ROTATE_DELTA_DEGREES = 15;
    private static final int DEFAULT_COST_TO_BUILD = 1;
    private static final int DEFAULT_COST_TO_UPGRADE = 1;
    private static final int DEFAULT_FIRE_RATE = 5;
    private static final double DEFAULT_LAUNCH_ANGLE_DEGREES = 0;

    private static final Color DEFAULT_COLOR = new Color(120, 80, 50);

    /**
     * The level of the tower. 
     */
    private int level;
    /**
     * The cost to build the tower. 
     */
    private int costToBuild;
    /**
     * The cost to upgrade the tower. 
     */
    private int costToUpgrade;
    /**
     * The fire rate of the tower, in game ticks per new projectile fired. 
     */
    private int fireRate;
    /**
     * The number of ticks remaining before the tower can fire again. 
     */
    private int ticksRemaining;
    /**
     * The launch angle of the tower, in degrees. 
     */
    private double launchAngleDegrees;
    /**
     * The cell that the tower is placed on. 
     */
    private Cell cell;

    /**
     * Constructs a tower at the given cell. 
     * 
     * @param cell
     */
    public Tower(Cell cell) {
        this.cell = cell;
        this.level = 1;
        this.costToBuild = DEFAULT_COST_TO_BUILD;
        this.costToUpgrade = DEFAULT_COST_TO_UPGRADE;
        this.fireRate = DEFAULT_FIRE_RATE;
        this.ticksRemaining = DEFAULT_FIRE_RATE;
        this.launchAngleDegrees = DEFAULT_LAUNCH_ANGLE_DEGREES;
    }

    /**
     * Constructs a tower at the given cell with the given fire rate. 
     * 
     * @param cell
     * @param fireRate
     */
    public Tower(Cell cell, int fireRate) {
        this(cell);
        this.fireRate = fireRate;
        this.ticksRemaining = fireRate;
    }

    public double getLaunchAngleDegrees() {
        return launchAngleDegrees;
    }

    /**
     * Returns a new projectile if the tower is ready to fire, 
     * or null otherwise. 
     * 
     * @return
     */
    public Projectile updateState() {
        this.ticksRemaining--;
        if (this.ticksRemaining > 0) {
            return null;
        }
        this.ticksRemaining = this.fireRate;
        return this.createProjectile();
    }

    /**
     * Returns a new projectile fired by the tower.
     * The characteristics of the projectile depend on the tower type and level.
     * 
     * @return the new projectile
     */
    protected abstract Projectile createProjectile();

    /**
     * Upgrades the tower to the next level, 
     * doubling the cost to upgrade. 
     */
    public void upgrade() {
        this.level++;
        this.costToUpgrade *= 2;
    }

    /**
     * Rotates the tower by a small amount. 
     */
    public void rotate() {
        this.launchAngleDegrees += ROTATE_DELTA_DEGREES;
    }

    /**
     * Returns the cost to upgrade the tower.
     * @return the (nonnegative) cost to upgrade
     */
    public int getCostToUpgrade() {
        return this.costToUpgrade;
    }

    /**
     * Returns the cost to build the tower.
     * @return the (nonnegative) cost to build
     */
    public int getCostToBuild() {
        return this.costToBuild;
    }

    protected void setCostToBuild(int costToBuild) {
        this.costToBuild = costToBuild;
    }

    /**
     * Returns the current level of the tower.
     * @return the tower's level (1, 2, 3, ...)
     */
    protected int getLevel() {
        return this.level;
    }

    public Cell getCell() {
        return this.cell;
    }

    @Override
    public void drawFallbackShape(Graphics2D g2d) {
        // Draw a circle to represent the tower
        g2d.setColor(DEFAULT_COLOR);
        g2d.fillOval(this.getX() + Cell.SQUARE_SIZE / 4, this.getY() + Cell.SQUARE_SIZE / 4, Cell.SQUARE_SIZE / 2, Cell.SQUARE_SIZE / 2);

        // Add a small line to represent the launch angle
        g2d.setColor(Color.BLACK);
        g2d.drawLine(this.getX() + Cell.SQUARE_SIZE / 2, this.getY() + Cell.SQUARE_SIZE / 2, (int)(this.getX() + Cell.SQUARE_SIZE / 2 + Math.cos(Math.toRadians(this.launchAngleDegrees)) * Cell.SQUARE_SIZE / 2), (int)(this.getY() + Cell.SQUARE_SIZE / 2 + Math.sin(Math.toRadians(this.launchAngleDegrees)) * Cell.SQUARE_SIZE / 2));
    }

    /**
     * Draw a thick black border around the cell the tower is on to highlight it.
     * @param g2d
     */
    public void drawHighlightedCell(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        int offset = 2;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawRect(this.cell.getPixelX() + offset / 2, this.cell.getPixelY() + offset / 2, Cell.SQUARE_SIZE - offset, Cell.SQUARE_SIZE - offset);
        g2d.setStroke(oldStroke);
    }

    @Override
    public int getX() {
        return this.cell.getPixelX();
    }

    @Override
    public int getY() {
        return this.cell.getPixelY();
    }
}
