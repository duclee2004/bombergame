package uet.oop.bomberman.entities.movingEntities.enemies;

import javafx.scene.image.Image;

import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.movingEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

import java.awt.*;

public class Oneal extends Enemy {
    private int direction;
    public Oneal(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
        setSpeed(1);
        generateDirection();
    }

    public void goLeft() {
        super.goLeft();
        img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, left++, 18).getFxImage();
    }

    public void goRight() {
        super.goRight();
        img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, right++, 28).getFxImage();
    }

    public void goUp() {
        super.goUp();
        img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, up++, 18).getFxImage();
    }

    public void goDown() {
        super.goDown();
        img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, down++, 18).getFxImage();
    }

    @Override
    public void stay() {
        super.stay();
        generateDirection();
    }

    @Override
    public void update() {
        generateDirection();
        if (direction == 0) goLeft();
        if (direction == 1) goRight();
        if (direction == 2) goUp();
        if (direction == 3) goDown();
        if(! BombermanGame.myBomber.isAlive()) {
            restartEnemy();
        }
        if (!isAlive()) {
            if (animated < 30) {
                super.stay();
                animated++;
                img = Sprite.oneal_dead.getFxImage();
            } else {
                BombermanGame.enemies.remove(this);
            }
        }
    }

    @Override
    public void generateDirection() {
        Rectangle re = BombermanGame.myBomber.getBounds();
        Bomber bomber = BombermanGame.myBomber;
        if (bomber.getX() / Sprite.SCALED_SIZE - x / Sprite.SCALED_SIZE < 0)
            direction = 0;
        if (bomber.getX() / Sprite.SCALED_SIZE - x / Sprite.SCALED_SIZE > 0)
            direction = 1;
        if (bomber.getY() / Sprite.SCALED_SIZE - y / Sprite.SCALED_SIZE < 0)
            direction = 2;
        if (bomber.getY() / Sprite.SCALED_SIZE - y / Sprite.SCALED_SIZE > 0)
            direction = 3;
    }

    @Override
    public void restartEnemy() {
        super.stay();
        this.x = startX * Sprite.SCALED_SIZE;
        this.y = startY * Sprite.SCALED_SIZE;
    }
}