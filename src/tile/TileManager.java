package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/maps/worldTest.txt");
    }

    public void getTileImage() {
        try {
            // Setup tiles through utility tool for pre-scaling
            setup(0, "grass", false);
            setup(1, "wall", true);
            setup(2, "water", true);
            setup(3, "earth", false);
            setup(4, "tree", true);
            setup(5, "sand", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup(int index, String imageName, boolean collision) {
        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/" + imageName + ".png")));
            tile[index].image = UtilityTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                int start = 0;
                col = 0;
                while(col < gp.maxWorldCol) {
                    int end = line.indexOf(' ', start);
                    if (end == -1) end = line.length();
                    int num = Integer.parseInt(line.substring(start, end));
                    mapTileNum[col][row] = num;
                    start = end + 1;
                    col++;
                }
                if(col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        // Spatial Culling: Only loop through the tiles the camera can currently see
        int startCol = Math.max(0, (gp.player.worldX - gp.player.screenX) / gp.tileSize);
        int startRow = Math.max(0, (gp.player.worldY - gp.player.screenY) / gp.tileSize);

        int endCol = Math.min(gp.maxWorldCol, startCol + (gp.screenWidth / gp.tileSize) + 2);
        int endRow = Math.min(gp.maxWorldRow, startRow + (gp.screenHeight / gp.tileSize) + 2);

        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        int playerScreenX = gp.player.screenX;
        int playerScreenY = gp.player.screenY;

        for (int worldCol = startCol; worldCol < endCol; worldCol++) {
            for (int worldRow = startRow; worldRow < endRow; worldRow++) {

                int tileNum = mapTileNum[worldCol][worldRow];
                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - playerWorldX + playerScreenX;
                int screenY = worldY - playerWorldY + playerScreenY;

                // Draw unscaled image
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }
        }
    }
}