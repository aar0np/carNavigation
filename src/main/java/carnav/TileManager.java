package carnav;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Random;

public class TileManager {

	private int tileSize;
	private int maxWorldCol;
	private int maxWorldRow;
	
	private Tile[] tiles;
	private int mapTileCodes[][];
	
	public TileManager(int tileSize, int maxWorldCol, int maxWorldRow, String map) {
		tiles = new Tile[50];
		this.tileSize = tileSize;
		this.maxWorldCol = maxWorldCol;
		this.maxWorldRow = maxWorldRow;
		
		mapTileCodes = new int[this.maxWorldCol][this.maxWorldRow];
		getTileImages();
		
		loadMap(map + ".txt");
	}
	
	private void getTileImages() {
		// road
		setupTile(0, "0.png", false);
		// grass
		setupTile(1, "1.png", true);
		// buildings
		setupTile(20, "20.png", true);
		setupTile(21, "21.png", true);
		setupTile(22, "22.png", true);
		setupTile(23, "23.png", true);
		setupTile(24, "24.png", true);
		setupTile(25, "25.png", true);
	}
	
	private void setupTile(int tileIndex, String imagePath, boolean collision) {

		try {
			tiles[tileIndex] = new Tile();
			BufferedImage scaledImage = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imagePath));
			scaledImage = GraphicsTools.scaleTile(scaledImage, tileSize, tileSize);
			tiles[tileIndex].setImage(scaledImage);
			tiles[tileIndex].setCollision(collision);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadMap(String mapFile) {
		Random random = new Random();
		
		try {
			InputStream inputStream = getClass().getResourceAsStream("/map_files/" + mapFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			
			int col = 0;
			int row = 0;
			
			while (col < maxWorldCol && row < maxWorldRow) {
				
				String inputLine = br.readLine();
				
				while (col < maxWorldCol) {
					String tileCodes[] = inputLine.split(" ");
					
					int tileCode = Integer.parseInt(tileCodes[col]);
					
					if (tileCode >= 2) {
						// randomly pick one of the #2 tiles
						mapTileCodes[col][row] = random.nextInt(20,26);
					} else {
						mapTileCodes[col][row] = tileCode;
					}
					
					col++;
				}
				
				if (col >= maxWorldCol) {
					col = 0;
					row++;
				}
			}
			
			br.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2) {
		
		int worldCol = 0;
		int worldRow = 0;

		
		while (worldCol < maxWorldCol && worldRow < maxWorldRow) {

			int tileNum = mapTileCodes[worldCol][worldRow];
			int screenX = worldCol * tileSize;
			int screenY = worldRow * tileSize;			
			
			g2.drawImage(tiles[tileNum].getImage(), screenX, screenY, null);
			
			worldCol++;
			
			if (worldCol >= maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
	
	public int[][] getMapTileCodes() {
		return this.mapTileCodes;
	}
}
