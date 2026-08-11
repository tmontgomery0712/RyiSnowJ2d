package main;

import entity.DirectionEnum;
import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity, DirectionEnum dir) {
        entity.collisionOn = false;

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (dir) {
            case UP -> entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
            case DOWN -> entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
            case LEFT -> entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
            case RIGHT -> entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
        }

        // Boundary safety check: Prevent ArrayIndexOutOfBounds if walking off map
        if (entityLeftCol >= 0 && entityRightCol < gp.maxWorldCol &&
                entityTopRow >= 0 && entityBottomRow < gp.maxWorldRow) {

            switch (dir) {
                case UP -> {
                    tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                    tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                    if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                        entity.collisionOn = true;
                    }
                }
                case DOWN -> {
                    tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                    tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                    if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                        entity.collisionOn = true;
                    }
                }
                case LEFT -> {
                    tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                    tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                    if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                        entity.collisionOn = true;
                    }
                }
                case RIGHT -> {
                    tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                    tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                    if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                        entity.collisionOn = true;
                    }
                }
            }
        } else {
            // Treat the edge of the world as a solid wall
            entity.collisionOn = true;
        }
    }
}