package gameObjects.enemies;

import gameObjects.Enemy;
import gameObjects.Projectile;

import java.awt.*;

import gameEngine.Cell;

public class Heavy extends Enemy {
    private static final int DEFAULT_ARMOR_CLASS = 1;

    private int armorClass;

    public Heavy(Cell startCell) {
        super(startCell);
        this.armorClass = DEFAULT_ARMOR_CLASS;
    }

    public void collideWith(Projectile projectile) {
        if (projectile.isArmorPiercing()) {
            this.takeDamage(projectile.getDamage());
            return;
        }

        int effectiveDamage = projectile.getDamage() - this.armorClass;
        if (effectiveDamage > 0) {
            this.takeDamage(effectiveDamage);
        }

        projectile.markToRemove();
        this.markToRemoveIfDead();
    }

    @Override
    public void drawFallbackShape(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        super.drawFallbackShape(g2d);
    }

}
