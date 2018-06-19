import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.awt.geom.AffineTransform;

/*TODO:
 * -Highscores >DONE
 * -Draw image >DONE
 * -Redo walls maybe >NOT GOING TO DO
 * 
 * Algorithm to load walls inspired from https://github.com/Jaryt/FlappyBirdTutorial/blob/master/flappyBird/FlappyBird.java
 */

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	// BIRD
	public Rectangle bird;
	// WALLS
	int crevace = 150;
	public ArrayList<Rectangle> walls;
	public static final Color PIPE_COLOR = new Color(0, 102, 0); // http://teaching.csse.uwa.edu.au/units/CITS1001/colorinfo.html
	// OTHER
	Random rand = new Random();
	int highScore2;
	int birdX = 200; // birdX is a constant
	static int score = 0;
	static int WIDTH = 800, HEIGHT = 600;
	boolean gameEnd = false;
	boolean start = false;
	public int ticks, yVel;
	private boolean spaceDown = false;
	boolean fall = true;
	BufferedImage bigBoy;

	public GamePanel() {
		// set current size to width and height
		// MUST use preferred size... turns out setSize does not work
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// make it so that GamePanel sends key events to itself
		// since it is both a JPanel (something that gives key events) and a KeyListener
		// (something that responds to key events)
		this.addKeyListener(this);
		this.addMouseListener(this);

		bird = new Rectangle(birdX, HEIGHT / 2, 30, 21);
		walls = new ArrayList<Rectangle>();

		Timer timer = new Timer(20, this);
		timer.start();

		addWalls(true);
		addWalls(true);
		addWalls(true);
		addWalls(true);
	}

	public void step() {

		// tell JPanel to repaint, which eventually calls paintComponent
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int speed = 6;
		if (start) {
			for (int i = 0; i < walls.size(); i++) {
				Rectangle wall = walls.get(i);
				wall.x -= speed;
				if (wall.x + wall.width < 0) {
					walls.remove(wall);
					if (wall.y == 0) {
						addWalls(false);
					}
				}
			}

			ticks++;
			if (ticks % 2 == 0 && yVel < 15) {
				yVel += 2;
			}
			bird.y += yVel;

			for (Rectangle wall : walls) {
				if (wall.y == 0 && wall.x < birdX && wall.x + 4 > birdX) {
					score++;
				} // Making sure wall.y == 0 so we only perform this on the top wall or else we
					// get score + 2.

				/*
				 * if (wall.x < birdX && wall.x + 4 > birdX) { score = score + 0.5; } ----------
				 * ^^ This is other option without wall.y == 0, but we have to make score a
				 * double which looks ugly.
				 */
				if (wall.intersects(bird)) {

					gameEnd = true;
					if (bird.x < wall.x) {
						bird.x = wall.x - bird.width;
					}
					if (bird.y < wall.height && bird.x > wall.x) {
						bird.y = wall.height;
						yVel = -2;
						fall = false;
					} else if (wall.y != 0 && bird.x + bird.width > wall.x) {
						bird.y = wall.y - bird.height;
						yVel += -2;
						fall = false;
					} else {
						fall = true;
					}
				}
			}
			if (bird.y > HEIGHT - 35) {
				gameEnd = true;
			} // If the bird hits grass (birdHeight + grassHeight).

			if (bird.y < 0) {
				bird.y = 0;
			} // Make a wall at the top that bird bounces off of.
		}
		if (gameEnd) {
			highScore2 = HighScore.getScore(score);
			start = false;
			if (bird.y > HEIGHT - 35) {
				bird.y = HEIGHT - 35;
			}
			if (fall) {
				ticks++;
				if (ticks % 2 == 0 && yVel < 15) {
					yVel += 2;
				}
				bird.y += yVel;
			}
		}
		// repaint();
	}

	public void paintComponent(Graphics g) {
		// do whatever JPanel normally does for paintComponent, then we can do our thing
		super.paintComponent(g);

		// not super necessary, but the Graphics g always passed as a parameter is
		// really a Graphics2D (which is a subclass of Graphics), and Graphics2D has
		// more methods
		// https://stackoverflow.com/questions/19344878/difference-between-graphics-and-graphics2d
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(new Font("Verdana", 10, 14));

		// set the color to white and fill the screen with said color to clear the
		// screen (so that you have a fresh panel to draw on)
		g2d.setColor(Color.cyan);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		try {// This block is possible thanks to the the help of Ewan
			BufferedImage bigBoy = ImageIO.read(this.getClass().getResource("bigBoy.png"));
			AffineTransform at = new AffineTransform();
			at.translate(bird.x, bird.y);
			double angle = Math.atan2(yVel, 10);
			at.rotate(angle, bird.width / 2, bird.height / 2);
			g2d.setColor(Color.black);
			// g2d.drawImage(bigBoy,bird.x,bird.y, this);
			g2d.drawImage(bigBoy, at, null);
		} catch (IOException e) {
			System.out.println("IOException caught in drawing image.");
		}

		g2d.setColor(Color.green);
		g2d.fillRect(0, HEIGHT - 10, WIDTH, 10);

		for (Rectangle wall : walls) {
			paintWalls(g, wall);
		}

		g2d.setColor(Color.black);
		g2d.setFont(new Font("Verdana", 1, 100));
		g2d.drawString(Integer.toString(score), 353, 103);

		g2d.setColor(Color.white);
		g2d.drawString(Integer.toString(score), 350, 100);

		g2d.setFont(new Font("Verdana", 1, 75));

		g2d.setColor(Color.gray);
		if (!start && !gameEnd) {
			g2d.drawString("Click to start", WIDTH / 2 - 280, HEIGHT / 2 - 50);
		}

		g2d.setFont(new Font("impact", Font.BOLD, 35));
		if (gameEnd) {
			g2d.setColor(Color.black);
			g2d.fillRect(WIDTH / 2 - 104, HEIGHT / 2 - 94, 208, 208);
			g2d.setColor(Color.orange);
			g2d.fillRect(WIDTH / 2 - 100, HEIGHT / 2 - 90, 200, 200);
			g2d.setColor(Color.black);
			g2d.setFont(new Font("impact", Font.BOLD, 40));
			g2d.drawString("Score", WIDTH / 2 - 54, HEIGHT / 2 - 32);
			g2d.drawString(Integer.toString(score), WIDTH / 2 - 9, HEIGHT / 2 + 8);
			g2d.drawString("Best", WIDTH / 2 - 44, HEIGHT / 2 + 53);
			g2d.drawString(Integer.toString(highScore2), WIDTH / 2 - 16, HEIGHT / 2 + 93);
			g2d.setColor(Color.white);
			g2d.setFont(new Font("impact", Font.BOLD, 40));
			g2d.drawString("Score", WIDTH / 2 - 55, HEIGHT / 2 - 35);
			g2d.drawString(Integer.toString(score), WIDTH / 2 - 10, HEIGHT / 2 + 5);
			g2d.drawString("Best", WIDTH / 2 - 45, HEIGHT / 2 + 50);
			g2d.drawString(Integer.toString(highScore2), WIDTH / 2 - 17, HEIGHT / 2 + 90);
		}

		if (spaceDown) {
			if (start) {
				yVel = -10;
			}
		}
	}

	public void addWalls(boolean old) {

		int width = 70;
		int height = 50 + rand.nextInt(300);

		if (old) {
			walls.add(new Rectangle(WIDTH + width + walls.size() * 300, HEIGHT - height - 10, width, height));
			walls.add(new Rectangle(WIDTH + width + (walls.size() - 1) * 300, 0, width, HEIGHT - height - crevace));
		} else {
			walls.add(new Rectangle(walls.get(walls.size() - 1).x + 600, HEIGHT - height - 10, width, height));
			walls.add(new Rectangle(walls.get(walls.size() - 1).x, 0, width, HEIGHT - height - crevace));
		}

	}

	public void paintWalls(Graphics g, Rectangle wall) {
		g.setColor(PIPE_COLOR);
		g.fillRect(wall.x, wall.y, wall.width, wall.height);
	}

	public static void score() {
		score += 1;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (start) {
				spaceDown = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			spaceDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (gameEnd) {
			walls.clear();
			addWalls(true);
			addWalls(true);
			addWalls(true);
			addWalls(true);
			yVel = -10;
			score = 0;
		}
		if (start) {
			return;
		}
		start = true;
		gameEnd = false;
		bird.y = HEIGHT / 2;
		yVel = -10;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
