import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HighScore extends JFrame {
	private static final long serialVersionUID = 1L;
	public static int highScore;
	JLabel scoreLabel;

	public static void main(String[] args) {
		new HighScore();
	}

	public HighScore() {
		super("Score");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(300, 300, 600, 600);
		JPanel outer = new JPanel(new BorderLayout());

		scoreLabel = new JLabel();
		outer.add(scoreLabel);
		this.setContentPane(outer);
		//loadScore();
	}

	public HighScore(int score) {
		this();

	}
	
	public static int getScore(int score)
	{
		loadScore();
		addScore(score);
		return highScore;
	}

	public static void addScore(int newScore) {

		if (newScore > highScore) {
			highScore = newScore;
		}
		saveScore();
	}

	public static void saveScore() {
		try {
			PrintWriter out = new PrintWriter("highscore.txt");
			out.println(highScore);
			out.close();
		} catch (IOException e) {
			System.out.println("Cannot open file (saveScore).");
		}
	}

	public static void loadScore() {
		String temp;
		try {
			BufferedReader in = new BufferedReader(new FileReader("highscore.txt"));
			temp = in.readLine(); // Read a line
			highScore = Integer.parseInt(temp);
			in.close();
		} catch (IOException e) {
			System.out.println("Cannot open file (highscore.txt), file has been created.");
		}
	}
}
