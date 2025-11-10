package gameObjects.projectiles;

import gameObjects.Projectile;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CannonBall extends Projectile {
    private static final Color CANNONBALL_COLOR = Color.DARK_GRAY;
    private static final int CANNONBALL_DAMAGE_PER_TOWER_LEVEL = 5;
    private static final int CANNONBALL_SIZE = 8;

    public CannonBall(Point2D startLocation, double launchAngle, int towerLevel) {
        super(startLocation, launchAngle, towerLevel);
        this.setColor(CANNONBALL_COLOR);
        this.setDamagePerLevel(CANNONBALL_DAMAGE_PER_TOWER_LEVEL);
    }

    @Override
    public void drawFallbackShape(Graphics2D g2d) {
        // Draw a filled circle for the cannonball
        Ellipse2D circle = new Ellipse2D.Double(getX() - CANNONBALL_SIZE / 2.0, 
                                                 getY() - CANNONBALL_SIZE / 2.0, 
                                                 CANNONBALL_SIZE, 
                                                 CANNONBALL_SIZE);
        g2d.setColor(CANNONBALL_COLOR);
        g2d.fill(circle);
        // Add black outline
        g2d.setColor(Color.BLACK);
        g2d.draw(circle);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D.Double(getX() - CANNONBALL_SIZE / 2.0, 
                                       getY() - CANNONBALL_SIZE / 2.0, 
                                       CANNONBALL_SIZE, 
                                       CANNONBALL_SIZE);
    }
}
