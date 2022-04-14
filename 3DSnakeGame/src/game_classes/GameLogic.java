package game_classes;

import java.awt.Color;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Random;

@SuppressWarnings("serial")
public class GameLogic extends JPanel implements ActionListener {
	
	private static final int SCREEN_WIDTH_X = 300;
	private static final int SCREEN_HEIGHT_Y = 300;
	private static final int SCREEN_HEIGHT_Z = 300;
	private static final int UNIT_SIZE = 25;
	private static final int GAME_UNITS = (SCREEN_WIDTH_X * SCREEN_HEIGHT_Y) / UNIT_SIZE;
	private static final int DELAY = 200;
	
	private static final int SPACING = (2 * UNIT_SIZE);
	
	//Screen_matrix shift for ZX (right of XY plane/ top right corner) make sure to leave 2 units space
	private static final int SCREEN_SHIFT_ZX = SCREEN_WIDTH_X + SPACING;
	
	//Screen_matrix shift for ZY (below of XY plane/ bottom left corner) make sure to leave 2 units space between each plane
	private static final int SCREEN_SHIFT_ZY = SCREEN_HEIGHT_Y + SPACING;


	private final int[] x = new int[GAME_UNITS];
	private final int[] y = new int[GAME_UNITS];
	private final int[] z = new int[GAME_UNITS];

	private int bodyParts;
	private int dotsEaten;

	private int dotX;
	private int dotY;
	private int dotZ;

	private SnakeDirection sd;
	private GameState gs;

	Timer timer;
	Random rand;

	GameLogic() {
		rand = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2), 
				SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 2)));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new SnakeKeyAdapter());
		this.gs = GameState.START_SCREEN;
	}

	public void startGame() {
		// start game variables
		bodyParts = 4;
		dotsEaten = 0;
		x[0] = 0;
		y[0] = 0;
		z[0] = 0;
		sd = SnakeDirection.EAST;
		this.newDot();
		gs = GameState.RUNNING;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void newDot() {

		// this is created so the dot is always created where the snakes body does not
		// exits
		boolean collidesWithSnake = false;
		do {

			collidesWithSnake = false;
			this.dotX = rand.nextInt((int) (SCREEN_WIDTH_X / UNIT_SIZE)) * UNIT_SIZE;
			this.dotY = rand.nextInt((int) (SCREEN_HEIGHT_Y / UNIT_SIZE)) * UNIT_SIZE;
			this.dotZ = rand.nextInt((int) (SCREEN_HEIGHT_Z / UNIT_SIZE)) * UNIT_SIZE;

			for (int i = 0; i < bodyParts; i++) {
				if ((x[i] == this.dotX) && (y[i] == this.dotY) && (z[i] == this.dotZ)) {
					collidesWithSnake = true;
					break;
				}
			}

		} while (collidesWithSnake);
	}

	public void moveSnake() {

		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
			z[i] = z[i - 1];
		}

		switch (sd) {
		case NORTH:
			y[0] = y[0] - UNIT_SIZE;
			break;
		case SOUTH:
			y[0] = y[0] + UNIT_SIZE;
			break;
		case WEST:
			x[0] = x[0] - UNIT_SIZE;
			break;
		case EAST:
			x[0] = x[0] + UNIT_SIZE;
			break;
		case UP:
			z[0] = z[0] - UNIT_SIZE;
			break;
		case DOWN:
			z[0] = z[0] + UNIT_SIZE;
			break;

		default:
			break;
		}

	}

	public void checkDotCollision() {
		if ((x[0] == dotX) && (y[0] == dotY) && (z[0] == dotZ)) {
			this.bodyParts = this.bodyParts + 1;
			this.dotsEaten = this.dotsEaten + 1;
			this.newDot();
		}
	}

	public void checkSnakeCollision() {
		// body collides with itself
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i]) && (z[0] == z[i])) {
				gs = GameState.GAME_OVER;
			}
		}

		// checks all borders
		if (x[0] < 0 || x[0] >= SCREEN_WIDTH_X 
				|| y[0] < 0 || y[0] >= SCREEN_HEIGHT_Y 
					|| z[0] < 0 || z[0] >= SCREEN_HEIGHT_Z) {
			gs = GameState.GAME_OVER;
		}
		
		if (gs == GameState.GAME_OVER) {
			this.timer.stop();
			for(int i = 0; i < bodyParts; i++) {
				x[i] = 0;
				y[i] = 0;
				z[i] = 0;
			}
		}
	}
	
	

	/**
	 * The <code> SnakeDirection </code> Represents all possible directions snake
	 * can move
	 * </p>
	 * 
	 * <p>
	 * NORTH on xy plane <br>
	 * SOUTH on xy Plane <br>
	 * EAST on xy Plane <br>
	 * WEST on xy plane <br>
	 * UP on Z axis <br>
	 * DOWN on Z axis
	 * </p>
	 * 
	 * @author Param
	 */
	enum SnakeDirection {
		NORTH, 
		SOUTH, 
		EAST, 
		WEST, 
		UP, 
		DOWN
	}

	/**
	 * The <code> GameState </code> represents all the possible game states
	 * 
	 * <p>
	 * RUNNING: Game is running, snake is moving <br>
	 * GAME_OVER: Player loses and goes to end screen <br>
	 * START_SCREEN: Player is at start screen <br>
	 * HOW_TO_PLAY: Player is at start screen <br>
	 * </p>
	 * 
	 * @author Param
	 */
	enum GameState {
		RUNNING, 
		GAME_OVER, 
		START_SCREEN, // to be implemented
		HOW_TO_PLAY // to be implemented
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (gs == GameState.RUNNING) {
			this.moveSnake();
			this.checkDotCollision();
			this.checkSnakeCollision();
		}
		
		repaint();
	}
	
	public class SnakeKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (gs == GameState.RUNNING) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_A:
					if(sd != SnakeDirection.EAST) {
						sd = SnakeDirection.WEST;
					}
					break;
					
				case KeyEvent.VK_D:
					if(sd != SnakeDirection.WEST) {
						sd = SnakeDirection.EAST;
					}
					break;
					
				case KeyEvent.VK_W:
					if(sd != SnakeDirection.SOUTH) {
						sd = SnakeDirection.NORTH;
					}
					break;
					
				case KeyEvent.VK_S:
					if(sd != SnakeDirection.NORTH) {
						sd = SnakeDirection.SOUTH;
					}
					break;
					
				case KeyEvent.VK_UP:
					if(sd != SnakeDirection.DOWN) {
						sd = SnakeDirection.UP;
					}
					break;
					
				case KeyEvent.VK_DOWN:
					if(sd != SnakeDirection.UP) {
						sd = SnakeDirection.DOWN;
					}
					break;

				default:
					break;
				}
			}
			
			else if (gs == GameState.START_SCREEN) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					startGame();
				}
			}
			
			else if (gs == GameState.GAME_OVER) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					startGame();
				}
			}
			
			
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(gs == GameState.RUNNING) {
			this.drawXY(g);
			this.drawXZ(g);
			this.drawYZ(g);
			this.drawScore(g);
			this.drawAxis(g);
		}
		else if(gs == GameState.START_SCREEN) {
			this.startScreen(g);
		}
		
		else if(gs == GameState.GAME_OVER) {
			this.gameOverScreen(g);
		}
		
		else if(gs == GameState.HOW_TO_PLAY) {
			//needs to be implemented
		}
	}
	
	public void startScreen(Graphics g) {
		g.setColor(Color.green);
		g.setFont(new Font("Agency FB", Font.BOLD, 100));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("3D Snake Game", 
				((SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2) - metrics.stringWidth("3D Snake Game")) / 2), 
				((SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 2)) / 2));
		
		g.setColor(Color.white);
		g.setFont(new Font("Agency FB", Font.BOLD, 35));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("By Param", 
				((SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2) - metrics2.stringWidth("By Param")) / 2), 
				((SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 4)) / 2));
		
		g.setColor(Color.red);
		g.setFont(new Font("Agency FB", Font.BOLD, 35));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press ENTER to play", 
				((SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2) - metrics3.stringWidth("Press ENTER to play")) / 2), 
				((SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 8)) / 2));
	}
	
	public void gameOverScreen(Graphics g) {
		g.setColor(Color.red);
		g.setFont(new Font("Agency FB", Font.BOLD, 100));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", 
				((SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2) - metrics.stringWidth("GAME OVER")) / 2), 
				((SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 2)) / 2));
		
		g.setColor(Color.green);
		g.setFont(new Font("Agency FB", Font.BOLD, 35));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Press ENTER to Restart the game", 
				((SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + (SPACING * 2) - metrics2.stringWidth("Press ENTER to Restart the game")) / 2), 
				((SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + (SPACING * 6)) / 2));
	}
	
	//Draw XY
	public void drawXY(Graphics g) {
		if(gs == GameState.RUNNING) {
			for(int i = 0 ; i <= SCREEN_WIDTH_X / UNIT_SIZE; i++) {
				g.setColor(Color.white);
				g.drawLine(i * UNIT_SIZE + SPACING, SPACING, 
						i * UNIT_SIZE + SPACING, SCREEN_HEIGHT_Y + SPACING);
				g.drawLine(SPACING, i * UNIT_SIZE + SPACING, 
						SCREEN_WIDTH_X + SPACING, i * UNIT_SIZE + SPACING);
			}
			
			g.setColor(Color.red);
			g.fillRect(dotX + SPACING, dotY + SPACING, UNIT_SIZE, UNIT_SIZE);
			for(int i = 0; i < bodyParts; i++) {
				g.setColor(Color.white);
				g.fillRect(x[i] + SPACING, y[i] + SPACING, UNIT_SIZE, UNIT_SIZE);
			}
		}
	}
	
	public void drawXZ(Graphics g) {
		if(gs == GameState.RUNNING) {
			for(int i = 0 ; i <= SCREEN_WIDTH_X / UNIT_SIZE; i++) {
				g.setColor(Color.white);
				g.drawLine((i * UNIT_SIZE) + (SCREEN_SHIFT_ZX) + SPACING, SPACING, 
						(i * UNIT_SIZE) + (SCREEN_SHIFT_ZX) + SPACING, SCREEN_HEIGHT_Y + SPACING);
				g.drawLine(SCREEN_SHIFT_ZX + SPACING, (i * UNIT_SIZE) + SPACING, 
						SCREEN_WIDTH_X + SCREEN_SHIFT_ZX + SPACING, (i * UNIT_SIZE) + SPACING);
			}
			
			g.setColor(Color.red);
			g.fillRect(dotX + SCREEN_SHIFT_ZX + SPACING, dotZ + SPACING, UNIT_SIZE, UNIT_SIZE);
			
			for(int i = 0; i < bodyParts; i++) {
				g.setColor(Color.white);
				g.fillRect(x[i] + SCREEN_SHIFT_ZX + SPACING, z[i] + SPACING, UNIT_SIZE, UNIT_SIZE);
			}
		}
 	}
	
	public void drawYZ(Graphics g) {
		if(gs == GameState.RUNNING) {
			for(int i = 0 ; i <= SCREEN_WIDTH_X / UNIT_SIZE; i++) {
				g.setColor(Color.white);
				g.drawLine((i * UNIT_SIZE) + SPACING, SCREEN_SHIFT_ZY + SPACING, 
						(i * UNIT_SIZE) + SPACING, SCREEN_HEIGHT_Y + SCREEN_SHIFT_ZY + SPACING);
				g.drawLine(SPACING, (i * UNIT_SIZE) + SCREEN_SHIFT_ZY + SPACING, 
						SCREEN_WIDTH_X + SPACING, (i * UNIT_SIZE) + SCREEN_SHIFT_ZY + SPACING);
			}
			
			g.setColor(Color.red);
			g.fillRect(dotY + SPACING, dotZ + SCREEN_SHIFT_ZY + SPACING, UNIT_SIZE, UNIT_SIZE);
			
			for(int i = 0; i < bodyParts; i++) {
				g.setColor(Color.white);
				g.fillRect(y[i] + SPACING, z[i] + SCREEN_SHIFT_ZY + SPACING, UNIT_SIZE, UNIT_SIZE);
			}
		}
 	}
	
	public void drawScore(Graphics g) {
		g.setColor(Color.green);
		g.setFont(new Font("Agency FB", Font.BOLD, 50));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score : " + this.dotsEaten, 
				(SPACING * 2) + SCREEN_WIDTH_X + (SCREEN_SHIFT_ZX / 2) 
				- (metrics.stringWidth("Score : " + this.dotsEaten) / 2) - UNIT_SIZE,
				(SPACING * 2) + SCREEN_HEIGHT_Y + ((SCREEN_SHIFT_ZY) / 2));
	}
	
	public void drawAxis(Graphics g) {
		g.setColor(Color.green);
		g.setFont(new Font("Agency FB", Font.BOLD, 20));
		g.drawString("X", SPACING, SPACING + SCREEN_HEIGHT_Y + UNIT_SIZE);
		g.drawString("Y", SPACING - (UNIT_SIZE /2), SPACING + SCREEN_HEIGHT_Y);
		g.drawString("X", (SPACING * 2) + SCREEN_WIDTH_X, SPACING + SCREEN_HEIGHT_Y + UNIT_SIZE);
		g.drawString("Z", (SPACING * 2) + SCREEN_WIDTH_X - (UNIT_SIZE /2), SPACING + SCREEN_HEIGHT_Y);
		g.drawString("Y", SPACING, (SPACING * 2) + (SCREEN_HEIGHT_Y *2) + UNIT_SIZE);
		g.drawString("Z", SPACING - (UNIT_SIZE /2), (SPACING * 2) + (SCREEN_HEIGHT_Y * 2));
	}
}
