package entity;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public int screenX;
    public int screenY;

    private int diagonalSpeed;

    private final BufferedImage[] upSprites = new BufferedImage[2];
    private final BufferedImage[] downSprites = new BufferedImage[2];
    private final BufferedImage[] leftSprites = new BufferedImage[2];
    private final BufferedImage[] rightSprites = new BufferedImage[2];

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
        solidArea = new Rectangle(10, 16, 28, 32);
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        diagonalSpeed = (int) Math.round(speed / 1.41421356);
        direction = DirectionEnum.DOWN;
        spriteNum = 0;
    }

    public void getPlayerImage() {
        for (int i = 0; i < 2; i++) {
            int frameNum = i + 1;
            upSprites[i] = setupSprite("/player/boy_up_" + frameNum + ".png");
            downSprites[i] = setupSprite("/player/boy_down_" + frameNum + ".png");
            leftSprites[i] = setupSprite("/player/boy_left_" + frameNum + ".png");
            rightSprites[i] = setupSprite("/player/boy_right_" + frameNum + ".png");
        }
    }

    private BufferedImage setupSprite(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            image = UtilityTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void update() {
        boolean movingY = keyH.upPressed || keyH.downPressed;
        boolean movingX = keyH.leftPressed || keyH.rightPressed;

        if (movingY || movingX) {
            if (keyH.leftPressed) {
                direction = DirectionEnum.LEFT;
            } else if (keyH.rightPressed) {
                direction = DirectionEnum.RIGHT;
            } else if (keyH.upPressed) {
                direction = DirectionEnum.UP;
            } else if (keyH.downPressed) {
                direction = DirectionEnum.DOWN;
            }

            // Check Y axis
            boolean clearY = true;
            if (keyH.upPressed) {
                gp.collisionChecker.checkTile(this, DirectionEnum.UP);
                if (collisionOn) clearY = false;
            } else if (keyH.downPressed) {
                gp.collisionChecker.checkTile(this, DirectionEnum.DOWN);
                if (collisionOn) clearY = false;
            }

            // Check X axis
            boolean clearX = true;
            if (keyH.leftPressed) {
                gp.collisionChecker.checkTile(this, DirectionEnum.LEFT);
                if (collisionOn) clearX = false;
            } else if (keyH.rightPressed) {
                gp.collisionChecker.checkTile(this, DirectionEnum.RIGHT);
                if (collisionOn) clearX = false;
            }

            // Apply Y movement
            if (clearY) {
                int ySpeed = (movingY && movingX && clearX) ? diagonalSpeed : speed;
                if (keyH.upPressed) worldY -= ySpeed;
                else if (keyH.downPressed) worldY += ySpeed;
            }

            // Apply X movement
            if (clearX) {
                int xSpeed = (movingY && movingX && clearY) ? diagonalSpeed : speed;
                if (keyH.leftPressed) worldX -= xSpeed;
                else if (keyH.rightPressed) worldX += xSpeed;
            }

            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum + 1) % 2;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
        }

        // --- CAMERA BOUNDARY LOGIC ---
        // 1. Default to keeping the player in the center of the screen
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // 2. Lock camera if approaching the Left or Right edge of the map
        if (worldX < screenX) {
            screenX = worldX;
        } else if (gp.screenWidth - screenX > gp.worldWidth - worldX) {
            screenX = gp.screenWidth - (gp.worldWidth - worldX);
        }

        // 3. Lock camera if approaching the Top or Bottom edge of the map
        if (worldY < screenY) {
            screenY = worldY;
        } else if (gp.screenHeight - screenY > gp.worldHeight - worldY) {
            screenY = gp.screenHeight - (gp.worldHeight - worldY);
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case UP -> upSprites[spriteNum];
            case DOWN -> downSprites[spriteNum];
            case LEFT -> leftSprites[spriteNum];
            case RIGHT -> rightSprites[spriteNum];
        };

        // Draw unscaled image
        g2.drawImage(image, screenX, screenY, null);

        g2.setColor(Color.RED);
        g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
    }
}