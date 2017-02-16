import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Arrive_Steering extends PApplet{


	Character c;
	GameAI game_ai;
	PShape pointer,head,body;
	int count=0;
	float target_x=-width/2,target_y=-height/2;
	String s_direction="up";
	float goalRotation =0,orientation=0;
	int direction=0;
	ArrayList<Vector2D> breadcrumbs = new ArrayList<>();

	boolean turn = false,first_run=true;

	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){
		background(200);

		pointer = createShape(GROUP);
		head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		body = createShape(ARC, 0,0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255,0,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);
		
		c=new Character(pointer);
		target_x=c.position.x;
		target_y=c.position.y;
		
	}

	public void change_direction(){
	}

	public void update(Character c,int time_elapsed){


		arrive(c,new PVector(target_x,target_y));
		
		//update position
		c.position.add(c.velocity.mult(time_elapsed));
		
		//update orientation
		//c.orientation += c.rotation*time_elapsed;

		//update accelerations
		c.velocity.add(c.acceleration.mult(time_elapsed));
		c.rotation += c.angular_acceleration*time_elapsed;

		
		pushMatrix();
		translate(c.position.x,c.position.y);
		rotate(c.orientation);
		shape(c.pointer);
		popMatrix();
			
		count++;

		if(count==4)
		{
			breadcrumbs.add(new Vector2D((int)c.position.x,(int)c.position.y));
			count=0;
		}
	}

	public void draw(){
		
		background(200);
		//fill(200,10);
		//rect(0,0,width,height);
		
		line(0,height/2,width,height/2);
		line(width/2,0,width/2,height);
		
		if(Timeline.getInstance().rightTime()){
			update(c,1);
			for(Vector2D v:breadcrumbs){
				rectMode(CENTER);
				rect(v.x,v.y,1,1);
			}
		}
		
	}
	public static void main(String argv[]){
		PApplet.main("Arrive_Steering");
	}
	
	//accelerate or decelerate depending on closeness to target
	public void arrive(Character c, PVector target_position){
		c.time_to_target_velocity=0.1;
		
		PVector direction = target_position.sub(c.position);
		float distance = direction.mag();
		float target_speed;
		
		if(distance<c.radius_of_satisfaction){
			c.velocity.mult(0);
			c.acceleration.mult(0);
			System.out.println("inside ros");
		}
		else{
			if(distance>c.radius_of_deceleration){
				target_speed = c.max_velocity;
				System.out.println("outside rod");
			}
			else{
				System.out.println("inside rod");
				target_speed = c.max_velocity*distance/c.radius_of_deceleration;
			}
			direction.normalize();
			direction.mult(target_speed);
			
			c.acceleration = direction.sub(c.velocity);
			c.acceleration.div((float)c.time_to_target_velocity);
			
			if(c.acceleration.mag()>c.max_acceleration){
				c.acceleration.normalize();
				c.acceleration.mult(c.max_acceleration);
			}
			
		}
	}
	
	//output angular velocity to rotate in direction of target
	public void align(Character c, PVector target_position){
		
	}
	
	public void mousePressed(){
		target_x=mouseX;
		target_y=mouseY;
	}
}
