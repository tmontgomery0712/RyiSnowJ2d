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

    // Precalculated diagonal speed for performance
    private int diagonalSpeed;

    // Sprite arrays for each direction (2 frames per direction)
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
        // Precompute diagonal speed (4 / 1.4142 ≈ 2.83 -> rounds to 3)
        diagonalSpeed = (int) Math.round(speed / 1.41421356);
        direction = "down";
        spriteNum = 0;
    }

    public void getPlayerImage() {
        try {
            for (int i = 0; i < 2; i++) {
                int frameNum = i + 1;
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
        boolean movingY = keyH.upPressed || keyH.downPressed;
        boolean movingX = keyH.leftPressed || keyH.rightPressed;

        if (movingY || movingX) {
            // 1. Facing direction (horizontal priority during diagonal move)
            if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            } else if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            }

            // 2. Select movement speed
            int moveSpeed = (movingY && movingX) ? diagonalSpeed : speed;

            // 3. Handle Vertical Movement
            if (keyH.upPressed) {
                gp.collissionChecker.checkTile(this, "up");
                if (!collisionOn) {
                    worldY -= moveSpeed;
                }
            } else if (keyH.downPressed) {
                gp.collissionChecker.checkTile(this, "down");
                if (!collisionOn) {
                    worldY += moveSpeed;
                }
            }

            // 4. Handle Horizontal Movement
            if (keyH.leftPressed) {
                gp.collissionChecker.checkTile(this, "left");
                if (!collisionOn) {
                    worldX -= moveSpeed;
                }
            } else if (keyH.rightPressed) {
                gp.collissionChecker.checkTile(this, "right");
                if (!collisionOn) {
                    worldX += moveSpeed;
                }
            }

            // 5. Update animation frame
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum + 1) % 2;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
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

        // Render player sprite
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // Render solidArea collision box overlay (Red outline)
        g2.setColor(Color.RED);
        g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
    }
}