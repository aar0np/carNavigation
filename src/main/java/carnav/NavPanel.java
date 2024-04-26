package carnav;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

public class NavPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -830441278268006190L;
	
	// constants
	final int originalTileSize = 16;
	final int scale = 2;

	// screen settings
	final int tileSize = originalTileSize * scale;  // 48x48 by default
	private final int fPS = 60; // frames per second
	
	// world map settings
	private String mapName;
	private final int maxWorldCol = 32;
	private final int maxWorldRow = 32;
	
	private int panelHeight;
	private int panelWidth;
	
	private Thread panelThread;
	private TileManager tileManager;
	
	private int numberOfCars;
	private List<Car> carList;
	
	private NavServices navSvc;
	
	public NavPanel() {
		this(1024,1024,1,"city_map_2");
		// with a pixel size of 1024x1024, that makes 32x32 grid squares,
		// where each square is 32x32
	}
	
	public NavPanel(int width, int height, int numCars, String map) {
		panelWidth = width;
		panelHeight = height;
		numberOfCars = numCars;
		mapName = map;
		
		tileManager = new TileManager(tileSize, maxWorldCol, maxWorldRow, mapName);
		
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
		
		// instantiate navigation services and database API objects
		navSvc = new NavServices(new AstraDBMethods(), mapName);
		
		// generate cars
		carList = new ArrayList<Car>();
		Random random = new Random();
		
		for (int carIndex = 0; carIndex < numberOfCars; carIndex++) {

			int colorIndex = random.nextInt(8);
			
			while (carColors.get(colorIndex) == null) {
				colorIndex = random.nextInt(8);
			}
			
			Car newCar = new Car(navSvc, tileSize, mapName, carColors.get(colorIndex), carIndex);
			carList.add(newCar);
			
			// remove from car Colors, so no two cars have the same color.
			carColors.remove(colorIndex);
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
		
		// draw cars
		for (Car car : carList) {
			car.draw(g2);
		}
		
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
		// move cars
		for (Car car : carList) {
			car.update();
		}
	}
}
