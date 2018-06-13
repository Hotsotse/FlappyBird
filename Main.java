import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {

		// create a JFrame, which represented a window in java swing
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create our own GamePanel, which is where drawing and logic will occur
		GamePanel panel = new GamePanel();

		// add the GamePanel to the window
		frame.add(panel);
		// make the window just big enough to hold the panel
		frame.pack();

		// request focus so that GamePanel will receive key input
		panel.requestFocus();

		// update until program terminates
		while (true) {
			panel.step();
		}
	}

}
