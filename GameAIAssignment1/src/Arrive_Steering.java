import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Arrive_Steering extends PApplet{


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

		c=new Character(pointer,50,750);

		pointer = createShape(GROUP);
		head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(0,255,0));
		head.setStroke(false);
		body = createShape(ARC, 0,0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(0,255,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

		d=new Character(pointer,750,750);
		d.max_velocity=1;

		target_x=400;
		target_y=50;

	}

	public void update(Character c,int time_elapsed){


		arrive(c,new PVector(target_x,target_y));

		orientToVelocity(c,new PVector(target_x,target_y));

		arrive(d,new PVector(c.position.x,c.position.y));

		orientToVelocity(d,new PVector(c.position.x,c.position.y));
		
		characterUpdate(c,time_elapsed);
		characterUpdate(d,time_elapsed);
		
		count++;

		if(count==4)
		{
			breadcrumbs1.add(new Vector2D((int)c.position.x,(int)c.position.y));
			breadcrumbs2.add(new Vector2D((int)d.position.x,(int)d.position.y));
			count=0;
		}
	}

	public void characterUpdate(Character c,int time_elapsed){
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

	}

	public void draw(){

		background(200);
		//fill(200,10);
		//rect(0,0,width,height);

		line(0,height/2,width,height/2);
		line(width/2,0,width/2,height);

		if(Timeline.getInstance().rightTime()){
			update(c,1);
			for(Vector2D v:breadcrumbs1){
				rectMode(CENTER);
				rect(v.x,v.y,1,1);
			}
			for(Vector2D v:breadcrumbs2){
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
		c.time_to_target=0.1;

		PVector target_velocity = target_position.sub(c.position);
		float distance = target_velocity.mag();
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
			target_velocity.normalize();
			target_velocity.mult(target_speed);

			c.acceleration = target_velocity.sub(c.velocity);
			c.acceleration.div((float)c.time_to_target);

			if(c.acceleration.mag()>c.max_acceleration){
				c.acceleration.normalize();
				c.acceleration.mult(c.max_acceleration);
			}

		}


	}

	public void orientToVelocity(Character c, PVector target_position){

		if(Math.signum(c.velocity.mag())!=0){

			orientation = target_position.sub(c.position).heading()+(float)Math.PI/2;

			goalRotation = orientation - c.orientation;

			if(goalRotation<0){
				direction=-1;
			}
			else{
				direction=1;
			}

			if(goalRotation<c.max_rotation){
				c.rotation = goalRotation;
			}
			else{

				c.rotation = direction*c.max_rotation;
			}

		}
		else{
			c.rotation = 0;
		}
	}

	//output angular velocity to rotate in direction of target
	public void align(Character c, PVector target_position, int ros, int rod){

		float target_orientation = c.velocity.heading();

		float temp_rotation = target_orientation - c.orientation;

		temp_rotation = mapToRange(temp_rotation);
		float rotationSize = Math.abs(temp_rotation);
		float target_rotation;

		if(rotationSize < ros){
			c.rotation=0;
			c.angular_acceleration=0;
		}else{
			if(rotationSize > rod){
				target_rotation = (float) c.max_rotation;
			}
			else{
				target_rotation = (float) (c.max_rotation*rotationSize/rod);
			}

			target_rotation*=Math.signum(temp_rotation);

			c.angular_acceleration = (float) (target_rotation - c.rotation);
			c.angular_acceleration/=c.time_to_target;

			if(Math.abs(c.angular_acceleration)>c.max_angular_acceleration){
				c.angular_acceleration/=Math.abs(c.angular_acceleration);
				c.angular_acceleration*=c.max_angular_acceleration;
			}
		}

	}

	public float mapToRange(float rotation){
		float r = (float) (rotation%(2*Math.PI));
		if(Math.abs(r)<=Math.PI){
			return r;
		}
		else{
			if(r>Math.PI){
				return (float) (r-2*Math.PI);
			}
			else{
				return (float) (r+2*Math.PI);
			}
		}
	}

	public void mousePressed(){
		target_x=mouseX;
		target_y=mouseY;
	}
}
