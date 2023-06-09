package uet.oop.bomberman.entities.movingEntities.enemies;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.movingEntities.enemies.algorithm.AStar;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class Minvo extends Enemy {

    private int prevBombX = 0;
    private int prevBombY = 0;
    private boolean changed = false;
    //lưu lại biến AStar cũ
    int[][] AStarTemp = new int[BombermanGame.HEIGHT][BombermanGame.WIDTH];
    private int prevI = 0;
    private int direction;
    AStar as;
    List<AStar.Node> path;

    public Minvo(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
        setSpeed(2);
        AStarTemp = BombermanGame.mapAStar;
        AStar as = new AStar(AStarTemp, this.x / Sprite.SCALED_SIZE,
                this.y / Sprite.SCALED_SIZE, true);
        List<AStar.Node> path = as.findPathTo(BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE,
                BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE);
        generateDirection();
    }

    public void goLeft() {
        super.goLeft();
        img = Sprite.movingSprite(Sprite.minvo_left1, Sprite.minvo_left2, Sprite.minvo_left3, left++, 18).getFxImage();
    }

    public void goRight() {
        super.goRight();
        img = Sprite.movingSprite(Sprite.minvo_right1, Sprite.minvo_right2, Sprite.minvo_right3, right++, 18).getFxImage();
    }

    public void goUp() {
        super.goUp();
        img = Sprite.movingSprite(Sprite.minvo_left1, Sprite.minvo_left2, Sprite.minvo_left3, up++, 18).getFxImage();
    }

    public void goDown() {
        super.goDown();
        img = Sprite.movingSprite(Sprite.minvo_right1, Sprite.minvo_right2, Sprite.minvo_right3, down++, 18).getFxImage();
    }

    @Override
    public void generateDirection() {
        if (!(BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE == prevBombX &&
                BombermanGame.myBomber.getY() / Sprite.SCALED_SIZE == prevBombY)) {
            as = new AStar(AStarTemp, this.x / Sprite.SCALED_SIZE,
                    this.y / Sprite.SCALED_SIZE, true);
            path = as.findPathTo(BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE,
                    BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE);
            prevBombX = BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE;
            prevBombY = BombermanGame.myBomber.getY() / Sprite.SCALED_SIZE;
            changed = true;
            direction();
        } else {
            changed = false;
            direction();
        }
    }

    public void direction() {
        double xConverted = (double) Math.round(((double) this.x / Sprite.SCALED_SIZE) * 100) / 100;
        double yConverted = (double) Math.round(((double) this.y / Sprite.SCALED_SIZE) * 100) / 100;
        if (BombermanGame.myBomber.isAlive()) {
            if (changed) {
                prevI = 0;
            } else {
                if (path == null) {
                    super.stay();
                    direction = 4;
                } else if (prevI == path.size()) {
                    if ((double) BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE - (double) x / Sprite.SCALED_SIZE < 0)
                        direction = 0;
                    if ((double) BombermanGame.myBomber.getX() / Sprite.SCALED_SIZE - (double) x / Sprite.SCALED_SIZE > 0)
                        direction = 1;
                    if ((double) BombermanGame.myBomber.getY() / Sprite.SCALED_SIZE - (double) y / Sprite.SCALED_SIZE < 0)
                        direction = 2;
                    if ((double) BombermanGame.myBomber.getY() / Sprite.SCALED_SIZE - (double) y / Sprite.SCALED_SIZE > 0)
                        direction = 3;
                    // lỗi không đi được trọn vẹn vào ô kill nhân vật
                } else if (path != null) {
                    double xPath = (double) Math.round((double) path.get(prevI).x * 100) / 100;
                    double yPath = (double) Math.round((double) path.get(prevI).y * 100) / 100;
                    if (xPath - xConverted == 0 && yPath - yConverted > 0) {
                        direction = 3;
                    } else if (xPath - xConverted == 0 && yPath - yConverted < 0) {
                        direction = 2;
                    } else if (xPath - xConverted < 0 && yPath - yConverted == 0) {
                        direction = 0;
                    } else if (xPath - xConverted > 0 && yPath - yConverted == 0) {
                        direction = 1;
                    } else if (xPath - xConverted == 0 && yPath - yConverted == 0) {
                        direction = 4;
                        prevI++;
                    }
                }
            }
        } else {
            restartEnemy();
        }
    }

    @Override
    public void restartEnemy() {
        super.stay();
        this.x = startX * Sprite.SCALED_SIZE;
        this.y = startY * Sprite.SCALED_SIZE;
    }

    @Override
    public void update() {
        generateDirection();
        if (!BombermanGame.myBomber.isAlive()) {
            restartEnemy();
        } else {
            if (direction == 0) goLeft();
            if (direction == 1) goRight();
            if (direction == 2) goUp();
            if (direction == 3) goDown();
            if (direction == 4) super.stay();

            if (isAlive()) {

            } else if (animated < 30) {
                super.stay();
                animated++;
                img = Sprite.minvo_dead.getFxImage();
            } else
                BombermanGame.enemies.remove(this);
        }
    }
}