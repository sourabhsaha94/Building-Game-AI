import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class KinematicMotion extends PApplet{


	Character c;
	GameAI game_ai;
	PShape pointer,head,body;
	int count=0;
	PVector target = new PVector(600,600);
	
	boolean turn = false,first_run=true;
	
	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){
		background(200);
		
		pointer = createShape(GROUP);

		head = createShape(TRIANGLE, -18, 0, 0, -35, 18, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		body = createShape(ARC, 0, 0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255,0,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);
		
		c=new Character(pointer);
		

	}

	public void update(Character c,int time_elapsed){
		seek(c,new PVector(800,800));first_run=false;
		
		
		//update position
		c.position.add(c.velocity.mult(time_elapsed));
		
		//update orientation
		c.orientation += c.rotation*time_elapsed;
		
		//update accelerations
		c.velocity.add(c.acceleration.mult(time_elapsed));
		c.rotation += c.angular_acceleration*time_elapsed;
		
		
		pushMatrix();
		translate(c.position.x,c.position.y);
		rotate(c.orientation);
		shape(c.pointer);
		popMatrix();

		
		System.out.println(count);
	}

	public void draw(){
		
		background(200);
		
		if(Timeline.getInstance().rightTime())
			update(c,1);
	}
	public static void main(String argv[]){
		PApplet.main("KinematicMotion");
	}
	
	
	public void seek(Character c, PVector target_position){
		
		float goalRotation =0,orientation=0,direction;
		boolean reverse = false;
		
		c.velocity = target_position.sub(c.position);
		
		c.velocity = c.velocity.normalize();
		
		c.velocity = c.velocity.mult(c.max_velocity);
		
		
		if(Math.signum(c.velocity.mag())!=0){
			
		//	System.out.println("CharVelocity: "+c.velocity.x+" "+c.velocity.y);
			
			if(c.velocity.x>=0){
				orientation = (float) (Math.atan(c.velocity.y/c.velocity.x)+Math.PI/2);
			}
				
			else{
				orientation = (float) (Math.atan(c.velocity.y/c.velocity.x)-Math.PI/2);
			}
			
			
			goalRotation = orientation - c.orientation;
			
			direction = Math.signum(goalRotation);
			
			if(goalRotation<c.max_rotation){
				c.rotation = goalRotation;
			}
			else{
				c.rotation = direction*c.max_rotation;
			}
			
		}
		
	}
}
