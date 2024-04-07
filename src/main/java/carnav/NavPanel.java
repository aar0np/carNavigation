package carnav;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class NavPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -830441278268006190L;
	
	private final int fPS = 60; // frames per second
	
	private int panelHeight;
	private int panelWidth;
	private Thread panelThread;
	
	public NavPanel() {
		this(1024,1024);
		// with a pixel size of 1024x1024, that makes 128x128 grid squares,
		// where each square is 8x8
	}
	
	public NavPanel(int width, int height) {
		panelWidth = width;
		panelHeight = height; 
		
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
		this.setBackground(Color.black);
		this.setFocusable(true);
		
		panelThread = Thread.ofVirtual()
				.name("Breakout")
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
		
		// ...
		
		g2.dispose();
	}
	
	private void update() {
		
	}
}
