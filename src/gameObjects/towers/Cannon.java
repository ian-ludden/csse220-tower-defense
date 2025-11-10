package gameObjects.towers;

import gameObjects.Projectile;
import gameObjects.Tower;
import gameObjects.projectiles.CannonBall;

import java.awt.*;
import java.awt.geom.Point2D;

import gameEngine.Cell;

public class Cannon extends Tower {
    private static final int CANNON_FIRE_RATE = 20;
    private static final Color CANNON_COLOR = new Color(64, 64, 64);
    private static final int CANNON_COST = 5;

    public Cannon(Cell cell) {
        super(cell);
        this.setFireRate(CANNON_FIRE_RATE);
        this.setColor(CANNON_COLOR);
        this.setCostToBuild(CANNON_COST);
    }

    @Override
    protected Projectile createProjectile() {
        Point2D center = new Point2D.Double(this.getCell().getPixelX() + Cell.SQUARE_SIZE / 2.0,
                this.getCell().getPixelY() + Cell.SQUARE_SIZE / 2.0);
        return new CannonBall(center, this.getLaunchAngleDegrees(), this.getLevel());
    }
}
