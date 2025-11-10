package gameObjects.projectiles;

import gameObjects.Projectile;

import java.awt.*;
import java.awt.geom.Point2D;

public class Missile extends Projectile {
    private static final Color MISSILE_COLOR = new Color(128, 0, 0);
    private static final int MISSILE_DAMAGE_PER_TOWER_LEVEL = 3;

    public Missile(Point2D startLocation, double launchAngle, int towerLevel) {
        super(startLocation, launchAngle, towerLevel);
        this.setColor(MISSILE_COLOR);
        this.setDamagePerLevel(MISSILE_DAMAGE_PER_TOWER_LEVEL);
        this.setArmorPiercing(true);
    }
    
}
