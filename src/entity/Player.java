package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    // Sprite arrays for each direction (2 frames per direction)
    private final BufferedImage[] upSprites = new BufferedImage[2];
    private final BufferedImage[] downSprites = new BufferedImage[2];
    private final BufferedImage[] leftSprites = new BufferedImage[2];
    private final BufferedImage[] rightSprites = new BufferedImage[2];

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
        spriteNum = 0; // 0-indexed (0 or 1)
    }

    public void getPlayerImage() {
        try {
            for (int i = 0; i < 2; i++) {
                int frameNum = i + 1; // Maps index 0 -> "1", index 1 -> "2"
                upSprites[i]    = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_up_" + frameNum + ".png")));
                downSprites[i]  = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_down_" + frameNum + ".png")));
                leftSprites[i]  = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_left_" + frameNum + ".png")));
                rightSprites[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_right_" + frameNum + ".png")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

        if (keyH.upPressed) {
            direction = "up";
            worldY -= speed;
        } else if (keyH.downPressed) {
            direction = "down";
            worldY += speed;
        } else if (keyH.leftPressed) {
            direction = "left";
            worldX -= speed;
        } else if (keyH.rightPressed) {
            direction = "right";
            worldX += speed;
        }

        // Animate only when moving
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum + 1) % 2; // Toggles between index 0 and 1
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; // Reset to standing frame when stationary
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "up"    -> upSprites[spriteNum];
            case "down"  -> downSprites[spriteNum];
            case "left"  -> leftSprites[spriteNum];
            case "right" -> rightSprites[spriteNum];
            default      -> downSprites[0];
        };

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}