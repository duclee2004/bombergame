package uet.oop.bomberman.entities.movingEntities.enemies;

import uet.oop.bomberman.entities.AnimatedEntity;
import javafx.scene.image.Image;

public abstract class Enemy extends AnimatedEntity {
    protected int startX;
    protected int startY;

    public Enemy(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
        setLayer(1);
        startX = xUnit;
        startY = yUnit;
    }

    public abstract void generateDirection();

    public abstract void restartEnemy();
}
