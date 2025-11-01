package gameObjects.projectiles;

import gameObjects.Enemy;
import gameObjects.Projectile;

import java.awt.*;
import java.awt.geom.Point2D;

public class Missile extends Projectile {

    public Missile(Point2D startLocation, double launchAngle, int towerLevel) {
        super(startLocation, launchAngle, towerLevel);
    }
    
}
