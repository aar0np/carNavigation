package carnav;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

public class NavPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -830441278268006190L;
	
	final int originalTileSize = 16;
	final int scale = 2;
	final int numberOfCars = 1;

	// screen settings
	final int tileSize = originalTileSize * scale;  // 48x48 by default
	private final int fPS = 60; // frames per second
	
	// world map settings
	private final int maxWorldCol = 32;
	private final int maxWorldRow = 32;
	
	private int panelHeight;
	private int panelWidth;
	private Thread panelThread;
	private TileManager tileManager;
	
	private List<Car> carList;
	
	public NavPanel() {
		this(1024,1024);
		// with a pixel size of 1024x1024, that makes 32x32 grid squares,
		// where each square is 32x32
	}
	
	public NavPanel(int width, int height) {
		panelWidth = width;
		panelHeight = height; 
		
		tileManager = new TileManager(tileSize, maxWorldCol, maxWorldRow, "city_map_1");
		
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
		this.setBackground(Color.black);
		this.setFocusable(true);
		
		// car colors
		//  0 - blue
		//  1 - gray
		//  2 - green
		//  3 - orange
		//  4 - purple
		//  5 - red
		//  6 - white
		//  7 - yellow
		
		Map<Integer,String> carColors = generateCarColors();
		
		// generate cars
		Random random = new Random();
		
		for (int carIndex = 0; carIndex < numberOfCars; carIndex++) {

			int colorIndex = random.nextInt(8);
			Car newCar = new Car(tileSize, carColors.get(colorIndex), colorIndex);
			carList.add(newCar);
			
			// remove from car Colors, so no two cars have the same color.
			carColors.remove(carIndex);
		}
		
		panelThread = Thread.ofVirtual()
				.name("Car Navigation Demo")
				.unstarted(this);
	}
	
	public void start() {
		panelThread.start();		
	}
	
	@Override
	public void run() {
		
		double drawInterval = 1000000000/fPS;
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		while (panelThread.isAlive()) {
			update();
			repaint();
			
			// compute pauses based on frames per second
			try {
				Thread.sleep(1000 / fPS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// draw map
		tileManager.draw(g2);
		
		g2.dispose();
	}
	
	private Map<Integer,String> generateCarColors() {
		Map<Integer,String> carColorMap = new HashMap<Integer,String>();
		carColorMap.put(0,"blue");
		carColorMap.put(1,"gray");
		carColorMap.put(2,"green");
		carColorMap.put(3,"orange");
		carColorMap.put(4,"purple");
		carColorMap.put(5,"red");
		carColorMap.put(6,"white");
		carColorMap.put(7,"yellow");
 
		return carColorMap;
	}
	
	private void update() {
		
	}
}
