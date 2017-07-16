import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

//Brian Schwartz (bjs9yv)
//Tod Colvin (trc8ed)
public class CollisionBox {
	
	public String checkBirdCollision(Rectangle birdA, Rectangle birdB) {
		
		Rectangle intersection = birdA.intersection(birdB);
		if (birdA.intersects(birdB)) {
			
			if (birdA.y < birdB.y && intersection.width > birdA.width/2) {
				return "birdyBird scores";
			}
			if (birdA.y > birdB.y && intersection.width > birdA.width/2) {
				return "otherBird scores";
			}
		}
		if(birdA.intersects(birdB))
		{
			return "partial collision";
		}
		return "no collision";
	}
	
	public boolean checkForBoxCollision(Rectangle bird, Rectangle barrier) { 
		if (bird.intersects(barrier)) {
			Rectangle intersection = bird.intersection(barrier);
			if(intersection.height > 10){
				return true;
			}

		}
		return false;
	}

}
