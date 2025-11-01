package gameObjects.towers;

import gameObjects.Projectile;
import gameObjects.Tower;
import gameObjects.projectiles.Arrow;

import java.awt.geom.Point2D;

import gameEngine.Cell;

public class ArcherTower extends Tower {
    
    public ArcherTower(Cell cell) {
        super(cell);
    }

    @Override
    protected Projectile createProjectile() {
        Point2D center = new Point2D.Double(this.getCell().getPixelX() + Cell.SQUARE_SIZE / 2, this.getCell().getPixelY() + Cell.SQUARE_SIZE / 2);
        return new Arrow(center, this.getLaunchAngleDegrees(), this.getLevel());
    }
}
