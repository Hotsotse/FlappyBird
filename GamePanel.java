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

/*TODO:
 * -Highscores
 * -Draw image
 * -Redo pipes maybe 
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
	private HighScore FlappyScore;
	int highScore2;
	int birdX = 200; // birdX is a constant
	static int score = 0;
	static int WIDTH = 800;
	static int HEIGHT = 600;
	boolean gameEnd = false;
	boolean start = false;
	public int ticks, yVel;
	private boolean spaceDown = false;
	boolean fall = true;

	public GamePanel() {
		// set current size to width and height
		// MUST use preferred size... turns out setSize does not work
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// make it so that GamePanel sends key events to itself
		// since it is both a JPanel (something that gives key events) and a KeyListener
		// (something that responds to key events)
		this.addKeyListener(this);
		this.addMouseListener(this);

		bird = new Rectangle(birdX, HEIGHT / 2, 25, 25);
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
					highScore2 = HighScore.getScore(score);
					gameEnd = true;
					if (bird.x < wall.x) {
						bird.x = wall.x - bird.width;
					}
					if (bird.y < wall.height && bird.x > wall.x) {
						bird.y = wall.height;
						yVel = 0;
						fall = false;
					} else if (wall.y != 0 && bird.x > wall.x) {
						bird.y = wall.y - bird.height;
						yVel = 0;
						fall = false;
					} else {
						fall = true;
					}
				}
			}
			if (bird.y > HEIGHT - 35) {
				bird.y = HEIGHT - 50;
				gameEnd = true;
			} // If the bird hits grass (birdHeight + grassHeight).

			if (bird.y < 0) {
				bird.y = 0;
				yVel += 2;
			} // Make a wall at the top that bird bounces off of.
		}
		if (gameEnd) {
			start = false;
			if (bird.y > HEIGHT - 35) {
				bird.y = HEIGHT - 50;
			}
			if (fall) {
				ticks++;
				if (ticks % 2 == 0 && yVel < 15) {
					yVel += 2;
				}
				bird.y += yVel;
			}
		}
		repaint();
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

		g2d.setColor(Color.red);
		g2d.fillRect(bird.x, bird.y, bird.width, bird.height);

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
		g2d.setColor(Color.red);
		if (gameEnd) {
			g2d.drawString("Game Over!", WIDTH / 2 - 280, HEIGHT / 2 - 50);
		}
		g2d.setColor(Color.gray);
		if (!start && !gameEnd) {
			g2d.drawString("Click to start", WIDTH / 2 - 280, HEIGHT / 2 - 50);
		}

		if (gameEnd) {
			g2d.drawString(Integer.toString(highScore2), WIDTH/2, HEIGHT/2);
		}
			
		
		if (spaceDown) {
			yVel = -10;
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
