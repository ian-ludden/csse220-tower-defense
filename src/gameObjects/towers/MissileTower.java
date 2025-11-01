package gameObjects.towers;

import gameObjects.Projectile;
import gameObjects.Tower;
import gameObjects.projectiles.Missile;

import java.awt.geom.Point2D;

import gameEngine.Cell;

public class MissileTower extends Tower {
    private static final int BASE_COST_TO_BUILD = 5;

    public MissileTower(Cell cell) {
        super(cell);
        this.setCostToBuild(BASE_COST_TO_BUILD);
    }

    @Override
    protected Projectile createProjectile() {
        Point2D center = new Point2D.Double(this.getCell().getPixelX() + Cell.SQUARE_SIZE / 2, this.getCell().getPixelY() + Cell.SQUARE_SIZE / 2);
        return new Missile(center, this.getLaunchAngleDegrees(), this.getLevel());
    }

}
