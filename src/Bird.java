import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Brian Schwartz (bjs9yv)
//Tod Colvin (trc8ed)

public class Bird {
	
	/// imgs: default storage for the pictures of the bird
	private BufferedImage[] imgs;
	
	// TODO: add your own fields here
	private double xPosition;//arbitrary starting position
	private double yPosition;
	private double xVelocity = 0.0;
	private double yVelocity = 0.0;
	private int facing = 0;//birds current i value
	
	/**
	 * Creates a bird object with the given image set 
	 * @param basename should be "birdg" or "birdr" (assuming you use the provided images)
	 */
	public Bird(String basename, int facing, int xPos) {
		// You may change this method if you wish, including adding 
		// parameters if you want; however, the existing image code works as is.
		this.imgs = new BufferedImage[6];
		try {
			// 0-2: right-facing (folded, back, and forward wings)
			this.imgs[0] = ImageIO.read(new File(basename+".png"));  
			this.imgs[1] = ImageIO.read(new File(basename+"f.png"));
			this.imgs[2] = ImageIO.read(new File(basename+"b.png"));
			// 3-5: left-facing (folded, back, and forward wings)
			this.imgs[3] = Bird.makeFlipped(this.imgs[0]);
			this.imgs[4] = Bird.makeFlipped(this.imgs[1]);
			this.imgs[5] = Bird.makeFlipped(this.imgs[2]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.facing = facing;
		this.xPosition = xPos;
		this.yPosition = 150.0;
		
	}
	
	/**
	 * Gravity implementation
	 */
	public void fall(){
		if(this.yPosition > 599 - this.imgs[0].getWidth()/4){
			this.yPosition = 599 - this.imgs[0].getWidth()/4;
			this.yVelocity *= -.1;
		}
		else{
			this.yVelocity += .5;		
		}
	}
	
	public void changePosition(){
		//drag
		this.xVelocity *= .96;
		this.xPosition += this.xVelocity;
		this.yPosition += this.yVelocity;
		
		//check if inside bounds of screen
		if(this.xPosition < 0 + this.imgs[0].getWidth()/4){//left
			this.xPosition = 0 + this.imgs[0].getWidth()/4;
			this.xVelocity = this.xVelocity *-.5;
		}
		if(this.xPosition > 800 - this.imgs[0].getWidth()/4){//right wall 
			this.xPosition = 800 - this.imgs[0].getWidth()/4;
			this.xVelocity = this.xVelocity *-.5;
		}
		if(this.yPosition < 0 + this.imgs[0].getWidth()/4){//ceiling 
			this.yPosition = 0 + this.imgs[0].getWidth()/4;
			this.yVelocity = this.yVelocity*-0.5;
		}
	}
	
	public void changeVelocity(int direction){
		//change to handle momentum and direction changes
		this.xVelocity = 9 * direction;
		this.yVelocity = -9;
		
	}
	
	public void bounce(int direction){
		this.xVelocity += 20 * direction;
		
	}
	
	/**
	 * A helper method for flipping in image left-to-right into a mirror image.
	 * There is no reason to change this method.
	 * 
	 * @param original The image to flip
	 * @return A left-right mirrored copy of the original image
	 */
	private static BufferedImage makeFlipped(BufferedImage original) {
		AffineTransform af = AffineTransform.getScaleInstance(-1, 1);
		af.translate(-original.getWidth(), 0);
		BufferedImage ans = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
		Graphics2D g = (Graphics2D)ans.getGraphics();
		g.drawImage(original, af, null);
		return ans;
	}
	
	/**
	 * Draws this bird
	 * @param g the paintbrush to use for the drawing
	 */
	public void draw(Graphics g) {
		
		int i = this.facing; // between 0 and 6, depending on facing and wing state
		double x = this.xPosition; // where to center the picture 
		double y = this.yPosition;
		// TODO: find the right x, y, and i instead of the examples given here
		
		g.drawImage(this.imgs[i], (int)x - this.imgs[i].getWidth()/2, (int)y - this.imgs[i].getHeight()/2, null);
	}
	
	/**
	 * get methods
	 */
	public int getHeight(){
		
		return this.imgs[0].getHeight();
		
	}
	
	public int getWidth(){
		
		return this.imgs[this.facing].getWidth();
	}
	
	public double getYPosition(){
		return yPosition;
	}
	
	public double getXPosition(){
		return xPosition;
	}
	
	public int getFacing(){
		return this.facing;
	}
	
	public double getXVelocity(){
		return this.xVelocity;
	}
	
	public double getYVelocity(){
		return this.yVelocity;
	}
	/**
	 * set methods
	 */
	public void setFacing(int i){
		this.facing = i;
	}
	
	public void setXVelocity(double xVel){
		this.xVelocity = xVel;
	}
	
	public void setYVelocity(double yVel){
		this.yVelocity = yVel;
	}
	
	public void setXPosition(double xPos){
		this.xPosition = xPos;
	}
	
	public void setYPosition(double yPos){
		this.yPosition = yPos;
	}
}