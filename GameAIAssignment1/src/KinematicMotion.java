import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class KinematicMotion extends PApplet{


	Character c;
	GameAI game_ai;
	PShape pointer,head,body;
	int count=0;
	float target_x=720,target_y=20;
	String direction="left";
	
	boolean turn = false,first_run=true;
	
	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){
		background(200);
		
		pointer = createShape(GROUP);
		
		//translate character to 20,20 :to-do
		head = createShape(TRIANGLE, 2, 0, 20, -35, 38, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		body = createShape(ARC, 20, 0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255,0,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);
		
		c=new Character(pointer);
		

	}
	
	public void change_direction(){
		if(direction=="right")
		{target_x=20;
		target_y=720;}
		
		if(direction=="down")
		{target_x=720;
		target_y=720;}
		
		if(direction=="left")
		{target_x=720;
		target_y=20;}
		
		if(direction=="up")
		{target_x=20;
		target_y=20;}
		
		
		if(c.position.x>=719 && c.position.y<=21 && direction=="left")
			{System.out.println(c.velocity.x+" "+c.velocity.y);
			System.out.println(degrees(c.orientation));
			direction="down";
			}
		
		if(c.position.y>=719 && c.position.x>=719 && direction=="down")
			{System.out.println(c.velocity.x+" "+c.velocity.y);
			System.out.println(degrees(c.orientation));
			direction = "right";
			}
		
		if(c.position.x<=21 && c.position.y>=719 && direction=="right"){
			System.out.println(c.velocity.x+" "+c.velocity.y);
			System.out.println(degrees(c.orientation));
			direction = "up";
		}
		if(c.position.y<=21 && c.position.x<=21 && direction=="up")
			{System.out.println(c.velocity.x+" "+c.velocity.y);
			System.out.println(degrees(c.orientation));
			direction="left";
			}
	}

	public void update(Character c,int time_elapsed){
		
		
		seek(c,new PVector(target_x,target_y));
		
		
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
		System.out.println(degrees(c.orientation));
		shape(c.pointer);
		popMatrix();

		
		change_direction();
		
		
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
		
		c.velocity = target_position.sub(c.position);
		
		c.velocity = c.velocity.normalize();
		
		c.velocity = c.velocity.mult(c.max_velocity);
		
		
		if(Math.signum(c.velocity.mag())!=0){
			
			/*if(c.velocity.x>=0){
				orientation = (float) (Math.atan(c.velocity.y/c.velocity.x)+Math.PI/2);
			}
				
			else{
				orientation = (float) (Math.atan(c.velocity.y/c.velocity.x)-Math.PI/2);
			}*/
			orientation = (float) (Math.atan(c.velocity.y/c.velocity.x)+Math.PI/2);
			
			
			
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
