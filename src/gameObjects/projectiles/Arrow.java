package gameObjects.projectiles;

import gameObjects.Projectile;

import java.awt.*;
import java.awt.geom.Point2D;

public class Arrow extends Projectile {

    public Arrow(Point2D startLocation, double launchAngle, int towerLevel) {
        super(startLocation, launchAngle, towerLevel);
        this.setColor(Color.LIGHT_GRAY);
    }

}
