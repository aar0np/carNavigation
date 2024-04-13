package carnav;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Car {

	private BufferedImage up1, down1, left1, right1;

	private Color finishColor;
	
	private String direction;
	private String name;
	private String color;
	
	private int speed;
	private int tileSize;
	private int worldCol;
	private int worldRow;
	private int startCol;
	private int startRow;
	private int finishCol;
	private int finishRow;
	private int finishX;
	private int finishY;
	
	private boolean atFinish;
	
	public Car(int tileSize, String color, int startLocationIndex) { 
		this(tileSize, color, startLocationIndex, 4);
	}

	public Car(int tileSize, String color, int startLocationIndex, int speed) { 

		this.tileSize = tileSize;
		this.name = color;
		this.color = color;
		this.speed = speed;
		
		if (startLocationIndex % 2 == 0) {
			direction = "left";
			startCol = 31;
			finishCol = 0;
		} else {
			direction = "right";
			startCol = 0;
			finishCol = 31;
		}
		
		startRow = (startLocationIndex * 4) + 2;
		finishRow = ((7 - startLocationIndex) * 4) + 4;
		
		if (finishRow >= 30) {
			finishRow = 2;
		}
		
		worldCol = startCol;
		worldRow = startRow;
		finishX = finishCol * tileSize;
		finishY = finishRow * tileSize;
		atFinish = false;
		
		switch(color) {
		case "blue":
			finishColor = Color.BLUE;
			break;
		case "gray":
			finishColor = Color.GRAY;
			break;
		case "green":
			finishColor = Color.GREEN;
			break;
		case "orange":
			finishColor = Color.ORANGE;
			break;
		case "purple":
			finishColor = new Color(128, 0, 128);
			break;
		case "red":
			finishColor = Color.RED;
			break;
		case "white":
			finishColor = Color.WHITE;
			break;
		case "yellow":
			finishColor = Color.YELLOW;
			break;
		default:
			finishColor = Color.DARK_GRAY;
		}
		
		setupCarImages();
	}
	
	public void update() {
		
	}
	
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			image = up1;
			break;
			
		case "down":
			image = down1;
			break;
			
		case "left":
			image = left1;
			break;
			
		case "right":
			image = right1;
			break;
		}
		
		// convert grid squares to screen X,Y
		int screenX = worldCol * tileSize;
		int screenY = worldRow * tileSize;
		
		g2.drawImage(image, screenX, screenY, null);
		
		// finish square
		if (!atFinish) {
			g2.setColor(finishColor);
			g2.fillRect(finishX, finishY,tileSize,tileSize);
		}
	}
	
	private void setupCarImages() {

		up1 = setupImage("/cars/" + color + "_up.png", tileSize, tileSize);
		right1 = setupImage("/cars/" + color + "_rt.png", tileSize, tileSize);
		down1 = setupImage("/cars/" + color + "_dn.png", tileSize, tileSize);
		left1 = setupImage("/cars/" + color + "_lt.png", tileSize, tileSize);
	}
	
	private BufferedImage setupImage(String imagePath, int width, int height) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath));
			// scale player tile
			image = GraphicsTools.scaleTile(image, width, height);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return image;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public BufferedImage getUp1() {
		return up1;
	}
	
	public void setUp1(BufferedImage up1) {
		this.up1 = up1;
	}
	
	public BufferedImage getDown1() {
		return down1;
	}
	
	public void setDown1(BufferedImage down1) {
		this.down1 = down1;
	}
	
	public BufferedImage getLeft1() {
		return left1;
	}
	
	public void setLeft1(BufferedImage left1) {
		this.left1 = left1;
	}
	
	public BufferedImage getRight1() {
		return right1;
	}
	
	public void setRight1(BufferedImage right1) {
		this.right1 = right1;
	}
	
	public int getWorldCol() {
		return worldCol;
	}
	
	public void setWorldCol(int worldCol) {
		this.worldCol = worldCol;
	}
	
	public int getWorldRow() {
		return worldRow;
	}
	
	public void setWorldRow(int worldRow) {
		this.worldRow = worldRow;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getFinishCol() {
		return finishCol;
	}

	public void setFinishCol(int finishRow) {
		this.finishCol = finishRow;
	}

	public int getFinishRow() {
		return finishRow;
	}

	public void setFinishRow(int finishRow) {
		this.finishRow = finishRow;
	}
}
