package uet.oop.bomberman;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import uet.oop.bomberman.audio.Music;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.stillEntities.bomb.Bomb;
import uet.oop.bomberman.entities.stillEntities.bomb.Flame;
import uet.oop.bomberman.entities.movingEntities.enemies.Doll;
import uet.oop.bomberman.entities.movingEntities.enemies.Oneal;
import uet.oop.bomberman.entities.movingEntities.enemies.Minvo;
import uet.oop.bomberman.entities.stillEntities.bomb.objects.Brick;
import uet.oop.bomberman.entities.stillEntities.bomb.objects.Grass;
import uet.oop.bomberman.entities.stillEntities.bomb.objects.Portal;
import uet.oop.bomberman.entities.stillEntities.bomb.objects.Wall;
import uet.oop.bomberman.entities.movingEntities.Bomber;
import uet.oop.bomberman.entities.movingEntities.enemies.Balloon;
import uet.oop.bomberman.entities.movingEntities.enemies.Enemy;
import uet.oop.bomberman.entities.stillEntities.bomb.items.*;
import uet.oop.bomberman.graphics.Sprite;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class BombermanGame extends Application {

    public static int WIDTH = 31;
    public static int HEIGHT = 13;
    public static int level = 1;
    public static GraphicsContext gc;
    private boolean paused = false;
    private boolean muted = false;
    private Canvas canvas;
    private Scanner scanner;
    private int xStart;
    private int yStart;
    public static final List<Enemy> enemies = new ArrayList<>();
    public static final List<Entity> stillObjects = new ArrayList<>();
    public static final List<Flame> flameList = new ArrayList<>();
    public int startBomb = 1;
    public int startSpeed = 1;
    public int startFlame  = 1;
    public static Bomber myBomber;
    public static int[][] mapAStar = new int[HEIGHT][WIDTH];
    public static Music musicPlayer = new Music(Music.BACKGROUND_MUSIC);;

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {

        musicPlayer.play();
        load(level);
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tao root container
        Group root = new Group();
        root.getChildren().add(canvas);

        // Tao scene
        Scene scene = new Scene(root);

        // Them scene vao stage
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (!paused) {
                    render();
                    update();
                }
                if(muted) {
                    musicPlayer.stop();
                } else {
                    musicPlayer.loop();
                }
            }
        };
        timer.start();
        scene.setOnKeyPressed(event -> {
            myBomber.handleKeyPressedEvent(event.getCode());
            if(event.getCode() == KeyCode.P) {
                paused = !paused;
            }
            if(event.getCode() == KeyCode.M) {
                muted = !muted;
            }
        });
        scene.setOnKeyReleased(event -> myBomber.handleKeyReleasedEvent(event.getCode()));
    }

    public void update() {
        // không sửa thành for each trong game không sẽ bị lỗi
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        for (Flame flame : flameList) {
            flame.update();
        }

        myBomber.update();
        List<Bomb> bombs = myBomber.getBombs();
        for(Bomb bomb : bombs) {
            bomb.update();
        }

        for (Entity stillObject : stillObjects) {
            stillObject.update();
        }
        handleCollisions();
        checkCollisionFlame();
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = stillObjects.size() - 1; i >= 0; i--) {
            stillObjects.get(i).render(gc);
        }
        enemies.forEach(g -> g.render(gc));
        List<Bomb> bombs = myBomber.getBombs();
        for(Bomb bomb : bombs) {
            bomb.render(gc);
        }
        myBomber.render(gc);
        flameList.forEach(g -> g.render(gc));
    }

    public void load(int _level) {
        try {
            scanner = new Scanner(new FileReader("res/levels/level" + _level + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.nextInt();
        HEIGHT = scanner.nextInt();
        WIDTH = scanner.nextInt();
        enemies.removeAll(enemies);
        stillObjects.removeAll(stillObjects);
        flameList.removeAll(flameList);
        scanner.nextLine();

        createMap();
    }

    public void createMap() {
        createMatrixCoordinates();
        for (int i = 0; i < HEIGHT; i++) {
            String r = scanner.nextLine();
            for (int j = 0; j < WIDTH; j++) {
                if (r.charAt(j) == '#') {
                    stillObjects.add(new Wall(j, i, Sprite.wall.getFxImage()));
                    mapAStar[i][j] = -1;
                } else {
                    stillObjects.add(new Grass(j, i, Sprite.grass.getFxImage()));
                    if (r.charAt(j) == '*') {
                        stillObjects.add(new Brick(j, i, Sprite.brick.getFxImage()));
                        mapAStar[i][j] = -1;
                    }
                    if (r.charAt(j) == 'x') {
                        stillObjects.add(new Portal(j, i, Sprite.portal.getFxImage()));
                        stillObjects.add(new Brick(j, i, Sprite.brick.getFxImage()));
                        mapAStar[i][j] = -1;
                    }
                    if (r.charAt(j) == '1') {
                        enemies.add(new Balloon(j, i, Sprite.balloom_left1.getFxImage()));
                    }
                    if (r.charAt(j) == '2') {
                        enemies.add(new Oneal(j, i, Sprite.oneal_left1.getFxImage()));
                    }
                    if (r.charAt(j) == '3') {
                        enemies.add(new Minvo(j, i, Sprite.minvo_left1.getFxImage()));
                    }
                    if (r.charAt(j) == '4') {
                        enemies.add(new Doll(j, i, Sprite.doll_left1.getFxImage()));
                    }
                    if (r.charAt(j) == 'b') {
                        stillObjects.add(new BombItem(j, i, Sprite.powerup_bombs.getFxImage()));
                        stillObjects.add(new Brick(j, i, Sprite.brick.getFxImage()));
                        mapAStar[i][j] = -1;
                    }
                    if (r.charAt(j) == 'f') {
                        stillObjects.add(new FlameItem(j, i, Sprite.powerup_flames.getFxImage()));
                        stillObjects.add(new Brick(j, i, Sprite.brick.getFxImage()));
                        mapAStar[i][j] = -1;
                    }
                    if (r.charAt(j) == 's') {
                        stillObjects.add(new SpeedItem(j, i, Sprite.powerup_speed.getFxImage()));
                        stillObjects.add(new Brick(j, i, Sprite.brick.getFxImage()));
                        mapAStar[i][j] = -1;
                    }
                    if (r.charAt(j) == 'p') {
                        myBomber = new Bomber(j, i, Sprite.player_right.getFxImage());
                        xStart = j;
                        yStart = i;
                        mapAStar[i][j] = 0;
                    }
                }
            }
        }
        stillObjects.sort(new Layer());
    }

    public void createMatrixCoordinates() {
        for(int i = 0; i < HEIGHT; i ++) {
            for(int j = 0; j < WIDTH; j ++) {
                mapAStar[i][j] = 0;
            }
        }
    }

    public void handleCollisions() {
        List<Bomb> bombs = myBomber.getBombs();
        Rectangle r1 = myBomber.getBounds();
        //Bomber vs StillObjects
        for (Entity stillObject : stillObjects) {
            Rectangle r2 = stillObject.getBounds();
            if (r1.intersects(r2)) {
                if (myBomber.getLayer() == stillObject.getLayer() && stillObject instanceof Item) {
                    if (stillObject instanceof BombItem) {
                        startBomb ++;
                        myBomber.setBombRemain(startBomb);
                        stillObjects.remove(stillObject);

                        Music powerUpAudio = new Music(Music.POWER_UP);
                        powerUpAudio.play();
                        mapAStar[myBomber.getY() / Sprite.SCALED_SIZE][myBomber.getX() / Sprite.SCALED_SIZE] = 0;
                    } else if (stillObject instanceof SpeedItem) {
                        startSpeed += 2;
                        myBomber.setSpeed(startSpeed);
                        stillObjects.remove(stillObject);

                        Music powerUpAudio = new Music(Music.POWER_UP);
                        powerUpAudio.play();
                        mapAStar[myBomber.getY() / Sprite.SCALED_SIZE][myBomber.getX() / Sprite.SCALED_SIZE] = 0;
                    } else if (stillObject instanceof FlameItem) {
                        startFlame ++;
                        myBomber.setRadius(startFlame);
                        stillObjects.remove(stillObject);

                        Music powerUpAudio = new Music(Music.POWER_UP);
                        powerUpAudio.play();
                        mapAStar[myBomber.getY() / Sprite.SCALED_SIZE][myBomber.getX() / Sprite.SCALED_SIZE] = 0;
                    }
                    myBomber.stay();
                } else if(myBomber.getLayer() == stillObject.getLayer() && stillObject instanceof Portal) {
                    if(enemies.size() == 0) {

                        load(++ level);

                        Music powerUpAudio = new Music(Music.POWER_UP);
                        powerUpAudio.play();
                    }
                } else if(myBomber.getLayer() >= stillObject.getLayer()) {
                    myBomber.move();
                }
                else {
                    myBomber.stay();
                }
                break;
            }
        }
        //Bomber vs Enemies
        for (Enemy enemy : enemies) {
            Rectangle r2 = enemy.getBounds();
            if (r1.intersects(r2)) {
                myBomber.setAlive(false);
                startBomb = 1;
                startFlame = 1;
                startSpeed = 1;
                if(!myBomber.isAlive()) {
                    Timer count = new Timer();
                    count.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            myBomber = new Bomber(xStart,yStart , Sprite.player_right.getFxImage());
                            count.cancel();
                        }
                    }, 500,1);
                    Music powerUpAudio = new Music(Music.BOMBER_DEAD);
                    powerUpAudio.play();

                }
            }
        }
        //Enemies vs Bombs
        for (Enemy enemy : enemies) {
            Rectangle r2 = enemy.getBounds();
            for (Bomb bomb : bombs) {
                Rectangle r3 = bomb.getBounds();
                if (!bomb.isAllowedToPassThrough(enemy) && r2.intersects(r3)) {
                    enemy.stay();
                    break;
                }
            }
        }
        //Enemies vs StillObjects
        for (Enemy enemy : enemies) {
            Rectangle r2 = enemy.getBounds();
            for (Entity stillObject : stillObjects) {
                Rectangle r3 = stillObject.getBounds();
                if (r2.intersects(r3)) {
                    if (enemy.getLayer() >= stillObject.getLayer()) {
                        enemy.move();
                    } else {
                        enemy.stay();
                    }
                    break;
                }
            }
        }
    }

    public void checkCollisionFlame() {
        for (Flame flame : flameList) {
            Rectangle r1 = flame.getBounds();
            for (Entity stillObject : stillObjects) {
                Rectangle r2 = stillObject.getBounds();
                if (r1.intersects(r2) && !(stillObject instanceof Item)) {
                    stillObject.setAlive(false);
                    mapAStar[stillObject.getY() / Sprite.SCALED_SIZE][stillObject.getX() / Sprite.SCALED_SIZE] = 0;
                }
            }
            for (Enemy enemy : enemies) {
                Rectangle r2 = enemy.getBounds();
                if (r1.intersects(r2)) {
                    enemy.setAlive(false);
                    Music powerUpAudio = new Music(Music.ENEMY_DEAD);
                    powerUpAudio.play();
                }
            }
            Rectangle r2 = myBomber.getBounds();
            if (r1.intersects(r2)) {
                myBomber.setAlive(false);
                startBomb = 1;
                startFlame = 1;
                startSpeed = 1;
                if (!myBomber.isAlive()) {
                    Timer count = new Timer();
                    count.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            myBomber = new Bomber(xStart, yStart, Sprite.player_right.getFxImage());
                            count.cancel();
                        }
                    }, 500, 1);
                    Music powerUpAudio = new Music(Music.BOMBER_DEAD);
                    powerUpAudio.play();

                }
            }
        }
    }
}