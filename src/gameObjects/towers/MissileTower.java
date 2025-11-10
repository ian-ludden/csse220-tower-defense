package gameObjects.towers;

import gameObjects.Projectile;
import gameObjects.Tower;
import gameObjects.projectiles.Missile;

import java.awt.*;
import java.awt.geom.Point2D;

import gameEngine.Cell;

public class MissileTower extends Tower {
    private static final int BASE_COST_TO_BUILD = 3;
    private static final Color MISSILE_TOWER_COLOR = new Color(128, 0, 0);
    private static final int MISSILE_FIRE_RATE = 12;

    public MissileTower(Cell cell) {
        super(cell);
        this.setCostToBuild(BASE_COST_TO_BUILD);
        this.setColor(MISSILE_TOWER_COLOR);
        this.setFireRate(MISSILE_FIRE_RATE);
    }

    @Override
    protected Projectile createProjectile() {
        Point2D center = new Point2D.Double(this.getCell().getPixelX() + Cell.SQUARE_SIZE / 2.0,
                this.getCell().getPixelY() + Cell.SQUARE_SIZE / 2.0);
        return new Missile(center, this.getLaunchAngleDegrees(), this.getLevel());
    }

    @Override
    protected Shape constructShape() {
        int centerX = this.getCell().getPixelX() + Cell.SQUARE_SIZE / 2;
        int centerY = this.getCell().getPixelY() + Cell.SQUARE_SIZE / 2;
        int size = Math.max(1, (int) (Cell.SQUARE_SIZE * 0.6));
        int half = size / 2;

        int[] xPoints = new int[] { centerX, centerX - half, centerX + half };
        int[] yPoints = new int[] { centerY - half, centerY + half, centerY + half };

        return new Polygon(xPoints, yPoints, 3);
    }
}
