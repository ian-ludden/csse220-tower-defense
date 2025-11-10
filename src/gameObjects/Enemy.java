package gameObjects;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import gameEngine.Cell;
import gameEngine.Level;

public abstract class Enemy extends DrawableObject {
    private static final int DEFAULT_WIDTH = 20;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int DEFAULT_PACE = 20;
    private static final int DEFAULT_MAX_HIT_POINTS = 2;
    
    /**
     * The movement pace of the enemy, in ticks per cell.
     */
    private int pace;
    
    /**
     * The number of ticks since the last move.
     */
    private int ticksSinceLastMove;

    /**
     * The maximum hit points of the enemy.
     */
    protected int maxHitPoints;
    
    /**
     * The current hit points of the enemy.
     */
    protected int currentHitPoints;
    
    /**
     * The previous cell of the enemy.
     */
    protected Cell previousCell;
    
    /**
     * The current cell of the enemy.
     */
    protected Cell currentCell;

    /**
     * Whether the enemy should be removed from the game.
     */
    private boolean shouldRemove;

    /**
     * The amount of jitter in the x direction.
     */
    private int jitterX;
    
    /**
     * The amount of jitter in the y direction.
     */
    private int jitterY;

    private int width;
    private int height;

    public Enemy() {
        this(null);
    }

    public Enemy(Cell startingCell) {
        this.pace = DEFAULT_PACE;
        this.ticksSinceLastMove = 0;
        this.maxHitPoints = DEFAULT_MAX_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
        this.previousCell = null;
        this.currentCell = startingCell;
        this.shouldRemove = false;

        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        // Randomly add jitter so enemies aren't stacked on top of each other
        this.jitterX = (int) (Math.random() * DEFAULT_WIDTH) - width / 2;
        this.jitterY = (int) (Math.random() * height) - height / 2;
    }

    public void setStartingCell(Cell startingCell) {
        this.previousCell = null;
        this.currentCell = startingCell;
    }

    protected void scaleSize(double scaleFactor) {
        this.width = (int) (DEFAULT_WIDTH * scaleFactor);
        this.height = (int) (DEFAULT_HEIGHT * scaleFactor);
    }
    
    /**
     * Advances the enemy to the next cell in its path. 
     * If the enemy has reached the end of its path 
     * (i.e., has gone off the screen), returns false. 
     * Otherwise, returns true. 
     * 
     * @return true if the enemy has **not** reached the end of its path, false otherwise
     */
    public boolean advance(Level level) {
        this.ticksSinceLastMove++;

        if (this.ticksSinceLastMove < this.pace) {
            return true;
        }

        this.ticksSinceLastMove = 0;

        if (this.currentCell == null) {
            return false;
        }

        // Candidate neighbor cells: up, down, left, right
        Cell up = new Cell(this.currentCell.getRow() - 1, this.currentCell.getColumn());
        Cell down = new Cell(this.currentCell.getRow() + 1, this.currentCell.getColumn());
        Cell left = new Cell(this.currentCell.getRow(), this.currentCell.getColumn() - 1);
        Cell right = new Cell(this.currentCell.getRow(), this.currentCell.getColumn() + 1);

        Cell[] candidates = new Cell[] { up, right, down, left };

        for (Cell next : candidates) {
            if (this.previousCell != null && next.equals(this.previousCell)) {
                continue;
            }
            if (level.isPathCell(next)) {
                this.previousCell = this.currentCell;
                this.currentCell = next;
                return true;
            }
        }

        // No forward path found: we've reached the end
        this.markToRemove();
        return false;
    }

    /**
     * Collides the enemy with a projectile. 
     * 
     * @param projectile the projectile that collides with the enemy
     */
    public void collideWith(Projectile projectile) {
        this.takeDamage(projectile.getDamage());
        projectile.markToRemove();
        this.markToRemoveIfDead();
    }

    protected void markToRemoveIfDead() {
        if (this.isDead()) {
            this.markToRemove();
        }
    }

    /**
     * Returns true if the enemy is dead, false otherwise. 
     * 
     * @return true if the enemy is dead, false otherwise
     */
    public boolean isDead() {
        return this.currentHitPoints <= 0;
    }

    /**
     * Marks the enemy to be removed from the game.
     */
    public void markToRemove() {
        this.shouldRemove = true;
    }
    
    /**
     * Returns true if the enemy should be removed from the game, false otherwise.
     * 
     * @return true if the enemy should be removed from the game, false otherwise
     */
    public boolean shouldRemove() {
        return this.shouldRemove;
    }

    /**
     * Applies a spawn delay in ticks before the enemy makes its first move.
     * A larger delay means the enemy will start moving later.
     */
    public void setSpawnDelayTicks(int delayTicks) {
        if (delayTicks <= 0) {
            return;
        }
        // Advance requires ticksSinceLastMove to reach pace; start negative to delay.
        this.ticksSinceLastMove = -delayTicks;
    }

    protected void takeDamage(int damage) {
        this.currentHitPoints -= damage;
    }

    @Override
    public void drawFallbackShape(Graphics2D g2d) {
        // Draw a simple circle for the enemy
        g2d.fillOval(getX() + jitterX, getY() + jitterY, width, height);
        Color incomingColor = g2d.getColor();
        g2d.setColor(Color.BLACK);
        g2d.drawOval(getX() + jitterX, getY() + jitterY, width, height);
        g2d.setColor(incomingColor);

        int barWidth = width;
        int barHeight = 4;
        int barX = getX() + jitterX;
        int barY = getY() + jitterY - barHeight - 2;
        if (barY < 0) {
            barY = getY() + jitterY + height + 2;
        }

        RoundRectangle2D barBg = new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, 6, 6);
        g2d.setColor(Color.RED);
        g2d.fill(barBg);

        double damageRatio = (this.maxHitPoints - Math.max(0, this.currentHitPoints)) / (double) this.maxHitPoints;
        int dmgWidth = (int) Math.round(barWidth * damageRatio);
        if (dmgWidth > 0) {
            RoundRectangle2D dmgRect = new RoundRectangle2D.Float(barX + (barWidth - dmgWidth), barY, dmgWidth, barHeight, 6, 6);
            g2d.setColor(new Color(60, 60, 60));
            g2d.fill(dmgRect);
        }

        g2d.setColor(Color.BLACK);
        g2d.draw(barBg);
        g2d.setColor(incomingColor);
    }

    @Override
    protected int getX() {
        return currentCell.getPixelX() + Cell.SQUARE_SIZE / 2 - width / 2;
    }

    @Override
    protected int getY() {
        return currentCell.getPixelY() + Cell.SQUARE_SIZE / 2 - height / 2;
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D.Double(getX() + jitterX, getY() + jitterY, width, height);
    }
}
