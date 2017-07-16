import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Brian Schwartz (bjs9yv@virginia.edu)
 * @since 2014-04-15
 * 
 * This class creates a game called Jousty-Bird
 * Two players try to land on each other's heads to score points
 * first to 10 wins
 * add options in the settings window
 */
public class JoustScreen extends KeyAdapter implements ActionListener, ComponentListener, ItemListener {
	
	/**
	 * Window setup
	 */
	private JFrame window;         // the window itself
	private BufferedImage content; // the current game graphics
	private Graphics2D paintbrush; // for drawing things in the window
	private Timer gameTimer;       // for keeping track of time passing
	
	/**
	 * Game play fields 
	 */
	private BufferedImage lavaPit;
	private BufferedImage background;
	private Bird birdyBird;
	private Bird otherBird;
	private Rectangle wall;
	private Integer leftPlayerScore = 0;
	private Integer rightPlayerScore = 0;
	private Rectangle b1;
	private Rectangle b2;
	private int gameTime;
	private Rectangle bottomWall;
	private CollisionBox boxCheck = new CollisionBox();
	private int bottomWalDirection = 1;
	private final int RIGHT = 1;
	private final int LEFT = -1;
	private boolean leftScored;
	private boolean rightScored;
	private int gameTimeAtScore;
	
	/**
	 * JPannel Settings
	 */
	private JPanel card;
	private JPanel cards;
	private JFrame frame;
	private Container pane;
	private JPanel comboBoxPane;
	private JCheckBox lava;
	private JCheckBox coolBackground;
	private JCheckBox movingPlatforms;
	private boolean movingPlatformsOn = false;
	private boolean lavaOn = false;
	private boolean backgroundOn = false;
	private boolean pauseGame = true;
	private boolean gameStart = false;
	
	/**
	 * Create a new JousScreen and let the game run 
	 */
	public static void main(String[] args) {
		new JoustScreen();
	}

	/**
	 * Constructor 
	 *  >> creates the main game window
	 *  >> creates the settings window
	 *  >> load optional background and features 
	 *  >> create bird objects and platforms 
	 */
	public JoustScreen() {
		
		// MAIN WINDOW
		this.window = new JFrame("Jousty Bird");
		this.content = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
		this.paintbrush = (Graphics2D)this.content.getGraphics();
		this.window.setContentPane(new JLabel(new ImageIcon(this.content)));
		this.window.pack();
		this.window.setVisible(true);
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //changed to EXIT so that the game being exited will also close the settings, but closing the settings will not close the game 
		this.window.addKeyListener(this);

		//SETTINGS WINDOW
		this.lava = new JCheckBox(" Lava Pit ");
    	this.coolBackground = new JCheckBox(" Cool Background ");
    	this.movingPlatforms = new JCheckBox(" Moving Platforms");
        this.comboBoxPane = new JPanel();
        this.card = new JPanel();
        this.card.add(this.coolBackground);
        this.card.add(this.lava);
        this.card.add(this.movingPlatforms);
        this.lava.addItemListener(this);
        this.lava.addComponentListener(this);
        this.coolBackground.addItemListener(this);
        this.coolBackground.addComponentListener(this);
        this.movingPlatforms.addItemListener(this);
        this.movingPlatforms.addComponentListener(this);
        this.cards = new JPanel(new CardLayout());
        this.cards.add(this.card);
        this.frame = new JFrame("Game Settings");
        this.pane = this.frame.getContentPane(); 
        this.pane.add(this.comboBoxPane, BorderLayout.PAGE_START);
        this.pane.add(this.cards, BorderLayout.CENTER);
        this.frame.setLocation(825, 200);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//DISPOSE so that closing the settings window will not close the game window 
        this.frame.pack();
        this.frame.setVisible(true);
        
		//BACKGROUND & LAVA PIT
		try {
			this.lavaPit = ImageIO.read(new File("lava.png"));
			this.background = ImageIO.read(new File("landscape.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		// BIRDS AND PLATFORMS
		this.birdyBird = new Bird("birdr", 0, 100);
		this.otherBird = new Bird("birdg", 0, 700);
		this.otherBird.setFacing(3);
		this.wall = new Rectangle(0, 200, 300, 20);	
		this.bottomWall = new Rectangle (0, 525, 450, 20);
		this.bottomWall.setLocation(this.bottomWall.x, this.bottomWall.y);
		
		this.gameTimer = new Timer(20, this); // tick at 1000/20 fps
		this.gameTimer.start();               // and start ticking now
	}

	/**
	 * Main Game Loop
	 * 
	 * Controls all aspects of the game that change
	 * 	>> GAME OVER LOGIC
	 *  >> MOMENTUM & DRAG
	 *  >> MOVING WALLS (if selected in options window)
	 *  >> LAVA DEATH  (if selected in options window)
	 *  >> PLATFORM COLLISIONS
	 *  >> BIRD COLLISIONS & SCORING 
	 *  >> KEEP BIRDS FACING EACHOTHER
	 *  >> ALLOW GAME TO PAUSE/UNPAUSE
	 *  
	 * After performing these functions the screen is redrawn via the refreshScreen method.
	 * This method is called every time the gameTimer ticks (50 times a second).
	 */
	public void actionPerformed(ActionEvent event) {
		// WINDOW CLOSED
		if (! this.window.isValid()) { 
			this.gameTimer.stop();    
			return;                    
		}                              
		
		// GAME OVER LOGIC
		if(this.rightPlayerScore == 10 || this.leftPlayerScore == 10){//first to 10 points
			this.gameTimer.stop();//ends game
		}
		
		// MOMENTUM, DRAG 
		if(this.gameStart == true){
			this.birdyBird.changePosition();
			this.otherBird.changePosition();
		}
		
		// MOVING WALLS
		if(this.movingPlatformsOn){
			this.updatePlatformPosition();
		}
		
		// LAVA DEATH
		if(this.lavaOn){
			if(this.birdyBird.getYPosition() > 550){
				this.birdyBird.setYPosition(100);
				this.birdyBird.setYVelocity(-10);
				this.rightPlayerScore++;
			}
			if(this.otherBird.getYPosition() > 550){
				this.otherBird.setYPosition(100);
				this.otherBird.setYVelocity(-10);
				this.leftPlayerScore++;
			}
		}
		
		// PLATFORM COLLISIONS
		this.checkBoxCollisions();
		
		// BIRD COLLISIONS AND SCORING
		this.checkBirdCollisions();
		
		// FACING
		if(this.birdyBird.getXPosition() >= this.otherBird.getXPosition()){
			this.birdyBird.setFacing(3);
			this.otherBird.setFacing(0);
		}else{
			this.birdyBird.setFacing(0);
			this.otherBird.setFacing(3);
		}

		// PAUSE GAME
		if(this.gameStart == false){
			this.refreshScreen();
		}
		
		// REDRAW SCREEN
		if(this.pauseGame == false){ //pauseGame is changed by pressing enter at any point during the game
			this.gameStart = true;
			this.gameTime++;
			this.refreshScreen(); // redraws the screen after things move
		}
	}

	/**
	 * Re-draw the screen
	 */
	public void refreshScreen() {
		this.paintbrush.setColor(new Color(150, 210, 255)); // pale blue
		this.paintbrush.fillRect(0, 0, this.content.getWidth(), this.content.getHeight()); // erases the previous frame
		
		//DRAW BIRDS, WALLS, BACKGROUND, LAVA PIT
		if(this.backgroundOn == true){
			this.paintbrush.drawImage(this.background, null, 0, 0);
		}
		if(this.lavaOn == true){
			this.paintbrush.drawImage(this.lavaPit, null, 0, 540);
		}
		this.birdyBird.draw(this.paintbrush);
		this.otherBird.draw(this.paintbrush);
		this.paintbrush.setColor(Color.BLACK);
		this.paintbrush.fill(this.bottomWall);
		this.paintbrush.fill(this.wall);
		
		this.drawMessages();
		
		this.window.repaint();  // displays the frame to the screen
	}
	
	/**
	 * This method gets called whenever a key is pressed
	 * and handles the movement of each character
	 */
	public void keyPressed(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.VK_A && this.gameStart == true) {
			this.birdyBird.changeVelocity(this.LEFT);
			this.birdyBird.setFacing(this.birdyBird.getFacing()+1);
		} 
		if(event.getKeyCode() == KeyEvent.VK_S && this.gameStart == true){
			this.birdyBird.changeVelocity(this.RIGHT);
		}
		if(event.getKeyCode() == KeyEvent.VK_K && this.gameStart == true){
			this.otherBird.changeVelocity(this.LEFT);
		}
		if(event.getKeyCode() == KeyEvent.VK_L && this.gameStart == true){
			this.otherBird.changeVelocity(this.RIGHT);
		}
		if(event.getKeyCode() == KeyEvent.VK_ENTER){
			this.pauseGame = !this.pauseGame;
		}
	}
	
	/**
	 * This method is called whenever a component in the settings window is clicked
	 * Controls the optional background and lava pit
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == this.lava) {

			this.lavaOn = !this.lavaOn;
		}
		if (source == this.coolBackground) {

			this.backgroundOn = !this.backgroundOn;
		}
		if(source == this.movingPlatforms){
			
			this.movingPlatformsOn = !this.movingPlatformsOn;
		}
	}
	
	
	/**
	 * Check for platform collisions and apply gravity 
	 */
	void checkBoxCollisions(){
		//BOX COLLISIONS
		Rectangle mWall = new Rectangle((int)this.wall.getX(), (int)this.wall.getY(), this.wall.width - 30, this.wall.height); //top platform
		Rectangle bWall = new Rectangle((int)this.bottomWall.getX(), (int) this.bottomWall.getY(), this.bottomWall.width - 30, this.bottomWall.height); //bottom platform
		this.b1 = new Rectangle((int)this.birdyBird.getXPosition(), (int)this.birdyBird.getYPosition(), this.birdyBird.getWidth()/2, this.birdyBird.getHeight()-30);
		Rectangle bird1 = new Rectangle((int)this.birdyBird.getXPosition() - this.birdyBird.getWidth()/2, (int)this.birdyBird.getYPosition() - this.birdyBird.getHeight()/2, this.birdyBird.getWidth(), this.birdyBird.getHeight());
		Rectangle bird2 = new Rectangle((int)this.otherBird.getXPosition() - this.otherBird.getWidth()/2, (int)this.otherBird.getYPosition() - this.otherBird.getHeight()/2, this.otherBird.getWidth(), this.otherBird.getHeight());
		this.b2 = new Rectangle((int)this.otherBird.getXPosition(), (int)this.otherBird.getYPosition(), this.otherBird.getWidth()/2, this.otherBird.getHeight()-30);
		if(this.boxCheck.checkForBoxCollision(bird1, mWall)){

			if(this.birdyBird.getYPosition() <= this.wall.getCenterY()){

				this.birdyBird.setYPosition(this.wall.getY()- 1  - this.birdyBird.getHeight()/(4));
			}
			else{
				this.birdyBird.setYVelocity(this.birdyBird.getYVelocity() * -1.25);
			}

		} else if(this.boxCheck.checkForBoxCollision(bird1, bWall)){

			if(this.birdyBird.getYPosition() <= this.bottomWall.getCenterY()){

				this.birdyBird.setYPosition(this.bottomWall.getY()- 1  - this.birdyBird.getHeight()/(4));
			}
			else{
				this.birdyBird.setYVelocity(this.birdyBird.getYVelocity() * -1.25);
			}
		} else{ //GRAVITY
			this.birdyBird.fall();
		}
		if(this.boxCheck.checkForBoxCollision(bird2, mWall)){

			if(this.otherBird.getYPosition() <= this.wall.getCenterY()){

				this.otherBird.setYPosition(this.wall.getY()- 1  - this.otherBird.getHeight()/(4));
			}	
			else{
				this.otherBird.setYVelocity(this.otherBird.getYVelocity() * -1.25);
			}
		} else if(this.boxCheck.checkForBoxCollision(bird2, bWall)){

			if(this.otherBird.getYPosition() <= this.bottomWall.getCenterY()){

				this.otherBird.setYPosition(this.bottomWall.getY()- 1  - this.otherBird.getHeight()/(4));
			}
			else{
				this.otherBird.setYVelocity(this.otherBird.getYVelocity() * -1.25);
			}
		} else{ //GRAVITY
			this.otherBird.fall();
		}
	}
	
	/**
	 * This method will check for two birds colliding.
	 * Respective player's score is updated if a valid collision occurs 
	 */
	void checkBirdCollisions(){
		if(this.boxCheck.checkBirdCollision(this.b1, this.b2).equals("birdyBird scores")){

			if(this.otherBird.getXPosition() >= 400){
				this.otherBird.setXPosition(100);
				this.otherBird.setYPosition(40);
			} else{
				this.otherBird.setXPosition(700);
				this.otherBird.setYPosition(40);
			} 
			this.leftPlayerScore++;
			this.leftScored = true;
			this.gameTimeAtScore = this.gameTime;
		}
		else if(this.boxCheck.checkBirdCollision(this.b1, this.b2).equals("otherBird scores")){

			if(this.birdyBird.getXPosition() >= 400){
				this.birdyBird.setXPosition(100);
				this.birdyBird.setYPosition(40);
			} else{
				this.birdyBird.setXPosition(700);
				this.birdyBird.setYPosition(40);
			}
			this.rightPlayerScore++;
			this.rightScored = true;
			this.gameTimeAtScore = this.gameTime;
		}
		else if(this.boxCheck.checkBirdCollision(this.b1, this.b2).equals("partial collision")){

			if(this.birdyBird.getXVelocity() <= 0 ){
				this.birdyBird.bounce(RIGHT);
			}
			else if(this.birdyBird.getXVelocity() > 0){
				this.birdyBird.bounce(LEFT);
			}
			if(this.otherBird.getXVelocity() <= 0){
				this.otherBird.bounce(RIGHT);
			}
			else if(this.otherBird.getXVelocity() > 0){
				this.otherBird.bounce(LEFT);
			}

		}
		else if(this.gameTimeAtScore + 100 < this.gameTime){
			this.rightScored = false;
			this.leftScored = false;
		}
	}
	
	
	/**
	 * This method will change the x and y position of the two in-game platforms if
	 * moving platforms are selected in the game menu
	 */
	void updatePlatformPosition(){
		if(this.wall.getX() > 0 && this.gameStart == true){
			this.wall.setLocation(this.wall.x - 1, this.wall.y);
		}
		else if(this.wall.getY() > 400 && this.gameStart == true){
			this.wall.setLocation(this.wall.x, 0);
		}
		else if(this.gameStart == true){
			this.wall.setLocation(this.wall.x + 700, this.wall.y + 50);
		}
		else{
			this.wall.x = 650;
		}
	
		if(this.bottomWall.getX() >= 0 && this.bottomWall.getX() < 800 - this.bottomWall.width  && this.gameStart == true){
			this.bottomWall.setLocation(this.bottomWall.x + 2 * bottomWalDirection, this.bottomWall.y);
		}
		else if(this.gameStart == true){
			this.bottomWalDirection *= -1;
			this.bottomWall.setLocation(this.bottomWall.x + 2 * bottomWalDirection, this.bottomWall.y);
		}
		else{
			this.bottomWall.x = 0;
		}
	}
	
	
	/**
	 * This method manages on screen messages
	 *  >> game rules and setup help
	 *  >> "3,2,1, go"
	 *  >> player scoring 
	 *  >> game over message
	 */
	void drawMessages(){
		// text drawing, for scores and other messages
		String endMessage = "";
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 90);
		if(this.rightPlayerScore == 10 || this.leftPlayerScore == 10){// END OF GAME MESSAGES

			if(this.rightPlayerScore == 10){
				endMessage = "GREEN WINS!!!";
			}
			else if(this.leftPlayerScore == 10){
				endMessage = "RED WINS!!!";
			}
			Font ff = new Font(Font.SANS_SERIF, Font.BOLD, 90);
			this.paintbrush.setFont(ff);
			Rectangle2D rr = ff.getStringBounds(endMessage, this.paintbrush.getFontRenderContext());
			this.paintbrush.setFont(ff);
			this.paintbrush.setColor(Color.BLUE);
			this.paintbrush.drawString(endMessage, 400-(int)rr.getWidth()/2, 300);
			f = new Font(Font.SANS_SERIF, Font.BOLD, 20);
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(new Color(127,0,0)); // dark red
			this.paintbrush.drawString(this.leftPlayerScore.toString(), 30, 30);
			this.paintbrush.setColor(new Color(0,127,0)); // dark green
			this.paintbrush.drawString(this.rightPlayerScore.toString(), 760, 30);

		} else if(this.gameStart == false && this.gameTime ==0){

			String msg1 = "1. Select options from the Settings Window";
			String msg2 = "2. Press Enter to Start";
			String msg3 = "3. Press Enter again to pause/unpause";
			String msg4 = "4. First to 10 wins";
			String msg5 = "    Be careful, hitting the lava will kill your bird and gives the other bird a point";
			f = new Font(Font.SANS_SERIF, Font.PLAIN, 40);
			Rectangle2D r = f.getStringBounds(msg1, this.paintbrush.getFontRenderContext());
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(Color.BLACK);
			this.paintbrush.drawString(msg1, 400 - (int)r.getWidth()/2, 300);
			this.paintbrush.drawString(msg2, 400 - (int)r.getWidth()/2, 350);
			this.paintbrush.drawString(msg3, 400 - (int)r.getWidth()/2, 400);
			this.paintbrush.drawString(msg4, 400 - (int)r.getWidth()/2, 450);
			f = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
			this.paintbrush.setFont(f);
			this.paintbrush.drawString(msg5, 400 - (int)r.getWidth()/2, 500);

		}
		else if(this.gameTime < 25){//START OF GAME MESSAGES

			String msg = "READY.";
			f = new Font(Font.SANS_SERIF, Font.BOLD, 90);
			Rectangle2D r = f.getStringBounds(msg, this.paintbrush.getFontRenderContext());
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(Color.BLUE);
			this.paintbrush.drawString(msg, 400-(int)r.getWidth()/2, 300);

		} else if(this.gameTime >= 25 && this.gameTime < 50){

			String msg = "SET.";
			f = new Font(Font.SANS_SERIF, Font.BOLD, 90);
			Rectangle2D r = f.getStringBounds(msg, this.paintbrush.getFontRenderContext());
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(Color.BLUE);
			this.paintbrush.drawString(msg, 400-(int)r.getWidth()/2, 300);

		} else if(this.gameTime >= 50 && this.gameTime < 100){

			String msg = "GO!.";
			Rectangle2D r = f.getStringBounds(msg, this.paintbrush.getFontRenderContext());
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(Color.BLUE);
			this.paintbrush.drawString(msg, 400-(int)r.getWidth()/2, 300);

		}
		else{ // SCORING MESSAGES

			f = new Font(Font.SANS_SERIF, Font.BOLD, 20);
			this.paintbrush.setFont(f);
			this.paintbrush.setColor(new Color(127,0,0)); // dark red
			this.paintbrush.drawString(this.leftPlayerScore.toString(), 30, 30);
			this.paintbrush.setColor(new Color(0,127,0)); // dark green
			this.paintbrush.drawString(this.rightPlayerScore.toString(), 760, 30);
			if(this.rightScored == true){
				this.paintbrush.setColor(new Color(0,127,0)); // dark green
				this.paintbrush.drawString("GREEN SCORED!", 600, 300);
			}
			if(this.leftScored == true){
				this.paintbrush.setColor(new Color(127,0,0)); // dark red
				this.paintbrush.drawString("RED SCORED!", 30, 300);
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub, 
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}
	
}