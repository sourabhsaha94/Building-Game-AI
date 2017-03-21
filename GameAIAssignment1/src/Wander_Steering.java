import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Wander_Steering extends PApplet{


	Character c,d;
	GameAI game_ai;
	PShape pointer,head,body;
	int count=0;
	float target_x=-width/2,target_y=-height/2;
	String s_direction="up";
	float goalRotation =0,orientation=0;
	int direction=0;
	ArrayList<Vector2D> breadcrumbs1 = new ArrayList<>();
	ArrayList<Vector2D> breadcrumbs2 = new ArrayList<>();

	boolean turn = false,first_run=true;

	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){

		background(200);

		c = getNewCharacter(50, 750);
		
		c.max_velocity=3;
		c.max_rotation=0.4;
		

	}

	public void update(int time_elapsed){

		wander(c);
		characterUpdate(c,time_elapsed);
		count++;

		if(count==4)
		{
			breadcrumbs1.add(new Vector2D((int)c.position.x,(int)c.position.y));
			count=0;
		}
	}

	public void characterUpdate(Character c,int time_elapsed){
		//update position
		c.position.add(c.velocity.mult(time_elapsed));

		//update orientation
		c.orientation += c.rotation*time_elapsed*c.time_to_target;

		//update accelerations
		c.velocity.add(c.acceleration.mult(time_elapsed));
		c.rotation += c.angular_acceleration*time_elapsed;


		pushMatrix();
		translate(c.position.x,c.position.y);
		rotate(c.orientation);
		scale((float) 0.5);
		shape(c.pointer);
		popMatrix();

	}

	public void draw(){

		background(200);
		//fill(200,10);
		//rect(0,0,width,height);

		line(0,height/2,width,height/2);
		line(width/2,0,width/2,height);

		if(Timeline.getInstance().rightTime()){
			update(1);
			for(Vector2D v:breadcrumbs1){
				rectMode(CENTER);
				rect(v.x,v.y,1,1);
			}
		}

	}
	public static void main(String argv[]){
		PApplet.main("Wander_Steering");
	}

		
	public void wander(Character c){
		
		c.velocity = getVectorFromOrientation(c.orientation);
		
		c.velocity.normalize();
		c.velocity.mult(c.max_velocity);
		
		c.rotation = randomBinomial()*c.max_rotation;
		//c.rotation = naturalRandomNumber()*c.max_rotation;
		
		if(c.position.y>height+5){
			c.position.y=0;
		}
		else if(c.position.y<-2){
			c.position.y = height;
		}
		
		if(c.position.x>width+5){
			c.position.x=0;
		}
		else if(c.position.x<-2){
			c.position.x=width;
		}
	}
	
	public float randomBinomial(){	//method 1
		return (float) ((Math.random()) - (Math.random()));
	}
	
	public float naturalRandomNumber(){	//method 2
		return (float)(-1 + Math.random()*2);
	}
	
	public PVector getVectorFromOrientation(float orientation){
		
		PVector tempVector = new PVector(0,0);
		
		tempVector.y = (float) Math.cos(orientation+Math.PI);
		tempVector.x = (float) Math.sin(orientation);
		return tempVector;
	}

	
	public void mousePressed(){
		target_x=mouseX;
		target_y=mouseY;
	}
	
	public Character getNewCharacter(int x,int y){		//factory pattern
		pointer = createShape(GROUP);
		PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		PShape body = createShape(ARC, 0,0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255,0,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

		return new Character(pointer,x,y); 
	}
}
