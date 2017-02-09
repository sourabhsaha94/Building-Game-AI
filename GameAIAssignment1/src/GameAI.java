import processing.core.PVector;

public class GameAI{
	
	public void seek(Character c, PVector target_position){
		float goalRotation =0,orientation=0,direction;
		
		c.velocity = target_position.sub(c.position);
		
		c.velocity = c.velocity.normalize();
		
		c.velocity = c.velocity.mult(c.max_velocity);
		
		
		if(Math.signum(c.velocity.mag())!=0){
			orientation = (float) Math.atan(c.velocity.x/c.velocity.y);
			goalRotation = orientation - c.orientation;
			
			direction = Math.signum(goalRotation);
			
			if(Math.abs(goalRotation)<c.max_rotation){
				c.rotation = goalRotation;
			}
			else{
				c.rotation = direction*c.max_rotation;
			}
			
		}
		
	}
}
