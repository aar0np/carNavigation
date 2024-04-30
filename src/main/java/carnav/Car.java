package carnav;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.List;

import javax.imageio.ImageIO;

//import com.datastax.astra.client.model.Document;

public class Car {

	private final static int CAR_SPEED = 1;
	
	private BufferedImage up1, down1, left1, right1;
	private Color finishColor;
	
	private String color;
	private String direction;
	private String map;
	private String name;
	
	private boolean atFinish;
	private boolean atStart;
	private boolean recomputed;

	private int speed;
	private int tileSize;
	private int worldCol;
	private int worldRow;
	private int screenX;
	private int screenY;
	private int startCol;
	private int startRow;
	private int finishCol;
	private int finishRow;
	private int finishX;
	private int finishY;
	private int carIndex;
	
	private final static float[] VECTOR_NOT_FOUND = {0,0,0,0};

	private List<float[]> navigationVectors;
	private NavServices navSvc;
	
	public Car(NavServices svc, int tileSize, String map, String color, int startLocationIndex) { 
		this(svc, tileSize, map, color, startLocationIndex, CAR_SPEED);
	}

	public Car(NavServices svc, int tileSize, String map, String color, int startLocationIndex, int speed) { 

		this.name = color;
		this.tileSize = tileSize;
		this.map = map;
		this.color = color;
		this.speed = speed;
		this.carIndex = startLocationIndex;
		
		if (carIndex % 2 == 0) {
			direction = "left";
			startCol = 31;
			finishCol = 0;
		} else {
			direction = "right";
			startCol = 0;
			finishCol = 31;
		}
		
		startRow = (carIndex * 4) + 2;
		finishRow = ((7 - carIndex) * 4) + 4;
		
		if (finishRow >= 30) {
			finishRow = 2;
		}
		
		worldCol = startCol;
		worldRow = startRow;
		finishX = finishCol * tileSize;
		finishY = finishRow * tileSize;
		atFinish = false;
		atStart = true;
		recomputed = false;
		
		// convert grid squares to screen X,Y
		screenX = worldCol * tileSize;
		screenY = worldRow * tileSize;
		
		// define finish color
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
		
		navSvc = svc;
		navigationVectors = computeInitialNavigation();
	}
	
	public void update() {
		
		if (!atStart && hasMovedToACompleteWorldSquare()) {
			// determine next move
			worldCol = screenX / tileSize;
			worldRow = screenY / tileSize;
			
			// are we at the finish?
			if (worldCol == finishCol && worldRow == finishRow) {
				atFinish = true;
			} else {
				// are we currently on one of our vectors?
				float[] currentVector = onValidVector();
				if (currentVector != VECTOR_NOT_FOUND) {
					// on a valid vector!
					// are we at the end of the vector?
					if (atEndOfVector(currentVector)) {
						// remove currentVector from list
						navigationVectors.remove(currentVector);
						// move toward nearest vector
						direction = getDirectionOfNearestVector();
					} else {
					// beginning or middle of the vector?
					// unless we're super-close to the finish, follow it!
						if (!isCloseToFinish(3)) {
							if (currentVector[0] == currentVector[2]) {
								// up/down
								if (currentVector[1] > currentVector[3]) {
									direction = "up";
								} else {
									direction = "down";
								}
								
							} else {
								// currentVector[1] == currentVector[3]
								// left/right
								if (currentVector[0] > currentVector[2]) {
									direction = "left";
								} else {
									direction = "right";
								}
							}
						} else {
							direction = getDirectionToFinish();
						}
					//    just keep swimming...
					}
				} else {
					// not currently on a valid vector, so recompute (only once)
					// and if we're not close to the finish, and then
					// move toward nearest vector
					if (!recomputed && navigationVectors.size() == 0 && !isCloseToFinish(6)) {
						recomputed = true;
						navigationVectors = augmentNavigation();
					}
					
					if (!isCloseToFinish(3)) {
						direction = getDirectionOfNearestVector();
					} else {
						direction = getDirectionToFinish();
					}
				}
			}
		
		} else {
			atStart = false;
		}
		
		if (!atFinish) {
			// if we're not at the finish, move forward in the current direction
			switch(direction) {
			case "up":
				screenY -= speed;
				break;
			
			case "down":
				screenY += speed;
				break;
				
			case "left":
				screenX -= speed;
				break;
				
			case "right":
				screenX += speed;
				break;
			}
		}
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
	
	private List<float[]> computeInitialNavigation() {
		
		// initial search vector
		float[] searchVector = {startCol, startRow, finishCol, finishRow};
		
		// initiate vector search
		List<float[]> streets = navSvc.vectorSearch(map, searchVector);
		
		// process the blue car's streets to the console;
		//if (name.equals("blue")) {
		if (carIndex == 5) {
			System.out.println(carIndex);
			System.out.print(startCol + ",");
			System.out.print(startRow + " - ");
			System.out.print(finishCol + ",");
			System.out.print(finishRow);
			System.out.println();
			
			for (float[] street : streets) {
				for (float point : street) {
					System.out.printf("%1.1f,", point);
				}
				System.out.println();
			}
		}
		
		return streets;
	}
	
	private List<float[]> augmentNavigation() {
		
		// current search vector
		float[] searchVector = {worldCol, worldRow, finishCol, finishRow};
		List<float[]> streets = navSvc.vectorSearch(map, searchVector);
		
		// process the blue car's streets to the console;
		//if (name.equals("blue")) {
		if (carIndex == 5) {
			System.out.print(startCol + ",");
			System.out.print(startRow + " - ");
			System.out.print(finishCol + ",");
			System.out.print(finishRow);
			System.out.println();
			
			for (float[] street : streets) {
				for (float point : street) {
					System.out.printf("%1.1f,", point);
				}
				System.out.println();
			}
		}
		
		return streets;
	}
	
	private boolean hasMovedToACompleteWorldSquare() {
		
		int modX = screenX % tileSize;
		int modY = screenY % tileSize;
		
		if (modX == 0 && modY == 0) {
			return true;
		} else {
			return false;
		}		
	}
	
	private float[] onValidVector() {
		
		float[] found = VECTOR_NOT_FOUND;
		
		for (float[] vector : navigationVectors) {
			// x1,y1 vector[0],vector[1]
			// x2,y2 vector[2],vector[3]
			
			if (worldCol == vector[0] && worldCol == vector[2]) {
				if (worldRow >= vector[1] && worldRow <= vector[3] ||
						worldRow >= vector[3] && worldRow <= vector[1]) {
					found = vector;
					break;
				}
			} else if (worldRow == vector[1] && worldRow == vector[3]) {
				if (worldCol >= vector[0] && worldCol <= vector[2] ||
						worldCol >= vector[2] && worldCol <= vector[0]) {
					found = vector;
					break;
				}
			}
		}
		
		return found;
	}
	
	private String getDirectionOfNearestVector() {
		
		float[] closestEndpoint = {99,99};
		double distance = 999;
		
		if (navigationVectors.size() > 0) {
			// compute distance to each endpoint
			for (float[] vector : navigationVectors) {
				
				double endpoint1Distance = getEuclideanDistance(vector[0], vector[1], worldCol, worldRow);
				double endpoint2Distance = getEuclideanDistance(vector[2], vector[3], worldCol, worldRow);
				double shortestEndpointDistance = 0;
				float endpoint[] = new float[2];
				
				if (endpoint1Distance < endpoint2Distance) {
					shortestEndpointDistance = endpoint1Distance;
					endpoint[0] = vector[0];
					endpoint[1] = vector[1];
				} else {
					shortestEndpointDistance = endpoint2Distance;
					endpoint[0] = vector[2];
					endpoint[1] = vector[3];
				}
				
				if (shortestEndpointDistance < distance) {
					distance = shortestEndpointDistance;
					closestEndpoint = endpoint;
				}
			}
		} else {
			// if there are no vectors remaining, use finish endpoint
			closestEndpoint[0] = finishCol;
			closestEndpoint[1] = finishRow;
		}
		
		float colDistance = worldCol - closestEndpoint[0];
		float rowDistance = worldRow - closestEndpoint[1];
		
		if (colDistance == 0) {
			if (rowDistance < 0) {
				return("down");
			} else {
				return("up");
			}
		} else if (rowDistance == 0) {
			if (colDistance < 0) {
				return("right");
			} else {
				return("left");
			}
		} else if (Math.abs(rowDistance) < Math.abs(colDistance)) {
			if (rowDistance < 0) {
				return("down");
			} else {
				return("up");
			}
		} else {
			// Math.abs(rowDistance) >= Math.abs(colDistance)
			if (colDistance < 0) {
				return("right");
			} else {
				return("left");
			}
		}
	}
	
	private boolean atEndOfVector(float[] vector) {
		boolean atEnd = false;
		boolean atEndpoint = false;
		boolean firstPair = false;
		String direction;
		
		if (vector[0] == worldCol && vector[1] == worldRow) {
			atEndpoint = true;
			firstPair = true;
		} else if (vector[2] == worldCol && vector[3] == worldRow) {
			atEndpoint = true;
		}
		
		if (atEndpoint) {
			if (vector[1] == vector[3]) {
				// is left/right
				if (vector[0] - vector[2] < 0) {
					direction = "right";
				} else {
					direction = "left";
				}
				
				if (direction.equals("left") && firstPair) {
					atEnd = false;
				} else {
					atEnd = true;
				}
			} else {
				// is up/down
				if (vector[1] - vector[3] < 0) {
					direction = "down";
				} else {
					direction = "up";
				}
				
				if (direction.equals("up") && firstPair) {
					atEnd = false;
				} else {
					atEnd = true;
				}
			}
		}
		
		return atEnd;
	}
	
	private boolean isCloseToFinish(int range) {
		boolean returnVal = false;
		double distance = getEuclideanDistance(finishCol, finishRow, worldCol, worldRow);
		if (distance < range) {
			returnVal = true;
		}
		
		return returnVal;
	}
	
	private String getDirectionToFinish() {
		float colDistance = worldCol - finishCol;
		float rowDistance = worldRow - finishRow;
		
		if (colDistance == 0) {
			if (rowDistance < 0) {
				return "down";
			} else {
				return "up";
			}
		} else {
			// rowDistance == 0 {
			if (colDistance < 0) {
				return "right";
			} else {
				return "left";
			}
		}
	}
	
	private double getEuclideanDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
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
}
