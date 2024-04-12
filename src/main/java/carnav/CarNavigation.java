package carnav;

import javax.swing.JFrame;

public class CarNavigation {

	public static void main(String[] args) {
		// build JFrame for game area
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Car Navigation w/ Vector Search");

		NavPanel panel = new NavPanel(1024,1024);
		window.add(panel);
		window.pack();
		window.setVisible(true);

		panel.start();
	}

}
