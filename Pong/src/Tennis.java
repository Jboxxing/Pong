// https://www.youtube.com/watch?v=xIqeK2hzx1I
// Features added myself: score keeping/score board, play again after a round is over (when a player scores a point), game over changed to after
// a player scores 5 points in total, 2 player mode (in addition to the 1 player mode with an AI)

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Tennis extends Applet implements Runnable, KeyListener
{
	public final int WIDTH = 700, HEIGHT = 500;
	private Thread thread;
	private HumanPaddle p1;
	private HumanPaddle p2H;
	private AIPaddle p2;
	private Ball b1;
	private boolean gameStarted, gameOver, roundOver, scoreUpdated;
	private int modeChosen;
	private Graphics gfx;
	private Image img;
	public final String SCORE = "SCORE: ";
	private int p1Score = 0, p2Score = 0;
	
	public void init()
	{
		this.resize(WIDTH, HEIGHT);
		this.addKeyListener(this);
		gameStarted = false;
		gameOver = false;
		roundOver = false;
		scoreUpdated = false;
		modeChosen = 0;
		p1 = new HumanPaddle(1);
		b1 = new Ball();
		// p2 = new AIPaddle(2, b1);
		// p2H = new HumanPaddle(2);
		img = createImage(WIDTH, HEIGHT);
		gfx = img.getGraphics();
		thread = new Thread(this);
		thread.start();
	}
	
	public void paint(Graphics g)
	{
		// The applet is filled with a black background color.
		// The scores of p1 and p2 are displayed in the gray color at the top near their respective sides.
		gfx.setColor(Color.black);
		gfx.fillRect(0, 0, WIDTH, HEIGHT);
		gfx.setColor(Color.gray);
		gfx.drawString(SCORE + p1Score, 200, 30);
		gfx.drawString(SCORE + p2Score, 400, 30);
		// The intro menu is shown until a user indicates being ready to play by pressing the ENTER key.
		// After the ENTER key is pressed, they must decide if they want to play with another human player or
		// against the AI. The ball and paddles for p1 and p2 are then displayed, and the game begins.
		// p1 controls the movement of their paddle by pressing the UP and DOWN keys in 1 player mode.
		// In 2 player mode, p1 moves their paddle using the W and S keys and p2 moves their paddle using the UP and
		// DOWN keys.
		// If the ball goes out of the left boundary, p2 (on the right side) earns a point.
		// If the ball goes out of the right boundary, p1 (on the left side) earns a point.
		// A full game is played until a player reaches 5 points.
		if (b1.getX() < -10 || b1.getX() > 710)
		{
			roundOver = true;
			if (p1Score == 5 || p2Score == 5)
			{
				gameOver = true;
				gfx.setColor(Color.red);
				gfx.drawString("Game Over, press SPACE to play again", 350, 250);
			}
			else if (b1.getX() < -10)
			{
				if (!scoreUpdated)
				{
					p2Score++;
					scoreUpdated = true;
				}
				gfx.setColor(Color.red);
				gfx.drawString("Round Over, press SPACE to play the next round", 350, 250);
			}
			else if (b1.getX() > 710)
			{
				if (!scoreUpdated)
				{
					p1Score++;
					scoreUpdated = true;
				}
				gfx.setColor(Color.red);
				gfx.drawString("Round Over, press SPACE to play the next round", 350, 250);
			}
		}
		else
		{
			// The ball and paddles are displayed throughout the game (once it starts), and replaced with an
			// appropriate message whenever a "Game Over" or "Round Over" event occurs.
			// After one of these events, if a user indicates they want to play another round or game, a ball and two
			// paddles are drawn again.
			p1.draw(gfx);
			b1.draw(gfx);
			if (modeChosen == 1)
				p2.draw(gfx);
			else if (modeChosen == 2)
				p2H.draw(gfx);
		}
		
		if (!gameStarted)
		{
			gfx.setColor(Color.white);
			gfx.drawString("Tennis", 340, 100);
			gfx.drawString("Press ENTER to begin...", 310, 130);
		}
		else if (modeChosen == 0)
		{
			gfx.setColor(Color.white);
			gfx.drawString("Press 1 for 1 player mode or press 2 for 2 player mode", 340, 100);
		}
		g.drawImage(img, 0, 0, this);
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}
	
	public void run()
	{
		for (;;)
		{
			if (gameStarted && !roundOver && !gameOver && modeChosen != 0)
			{
				b1.move();
				p1.move();
				if (modeChosen == 1)
				{
					p2.move();
					b1.checkPaddleCollision(p1, p2);
				}
				else if (modeChosen == 2)
				{
					p2H.move();
					b1.checkPaddleCollision(p1, p2H);
				}
			}
			repaint();
			
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (modeChosen == 1)
		{
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				p1.setUpAccel(true);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				p1.setDownAccel(true);
			}
		}
		else if (modeChosen == 2)
		{
			if (e.getKeyCode() == KeyEvent.VK_W)
			{
				p1.setUpAccel(true);
			}
			else if (e.getKeyCode() == KeyEvent.VK_S)
			{
				p1.setDownAccel(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				p2H.setUpAccel(true);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				p2H.setDownAccel(true);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			gameStarted = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_1 && modeChosen == 0)
		{
			p2 = new AIPaddle(2, b1);
			modeChosen = 1;
		}
		else if (e.getKeyCode() == KeyEvent.VK_2 && modeChosen == 0)
		{
			p2H = new HumanPaddle(2);
			modeChosen = 2;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			// First attempts to recognizes a "Game Over" event and sets the appropriate booleans to false (necessary
			// to redirect to the intro menu).
			// The scores for p1 and p2 are set back to zero.
			// A new ball and two new paddles are created (p1 in this code block and p2 in another after a user specification).
			// If the game is not over, a "Round Over" event is checked for.
			// When a round is over, the appropriate booleans are set to false so that they can be triggered again
			// when another round ends.
			// A new ball and two new paddles are created.
			if (gameOver)
			{
				gameOver = false;
				gameStarted = false;
				roundOver = false;
				modeChosen = 0;
				p1Score = 0;
				p2Score = 0;
				p1 = new HumanPaddle(1);
				b1 = new Ball();
			}
			else if (roundOver)
			{
				gameStarted = false;
				roundOver = false;
				scoreUpdated = false;
				p1 = new HumanPaddle(1);
				b1 = new Ball();
				if (modeChosen == 1)
					p2 = new AIPaddle(2, b1);
				else if (modeChosen == 2)
					p2H = new HumanPaddle(2);
			}
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		if (modeChosen == 1)
		{
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				p1.setUpAccel(false);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				p1.setDownAccel(false);
			}
		}
		else if (modeChosen == 2)
		{
			if (e.getKeyCode() == KeyEvent.VK_W)
			{
				p1.setUpAccel(false);
			}
			else if (e.getKeyCode() == KeyEvent.VK_S)
			{
				p1.setDownAccel(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				p2H.setUpAccel(false);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				p2H.setDownAccel(false);
			}
		}
	}
	
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
