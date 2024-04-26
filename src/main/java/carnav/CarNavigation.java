package carnav;

import javax.swing.JFrame;

public class CarNavigation {
	
	private final static int NUMBER_OF_CARS = 4;
	private final static String MAP = "city_map_2";
	
	public static void main(String[] args) {
		// build JFrame for game area
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Car Navigation w/ Vector Search");

		NavPanel panel = new NavPanel(1024, 1024, NUMBER_OF_CARS, MAP);
		window.add(panel);
		window.pack();
		window.setVisible(true);

		panel.start();
	}

}
