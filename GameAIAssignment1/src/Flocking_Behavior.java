import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Flocking_Behavior extends PApplet{

	/**************************************Variable declarations********************************************************/
	Character c,d;
	GameAI game_ai;
	PShape pointer,head,body;
	int count=0;
	String s_direction="up";
	float goalRotation =0,orientation=0;
	int direction=0;
	ArrayList<ArrayList<Vector2D>> breadcrumbsList = new ArrayList<>(); 
	ArrayList<Character> boids = new ArrayList<>();
	HashMap<Character,ArrayList<Character>> neighbourList = new HashMap<>();

	boolean turn = false,first_run=true;

	double sW=0.2,cW=0.3,vW=0.5;

	/***************************************Processing functions********************************************************/
	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){

		background(200);

		c = getNewCharacter((int)(Math.random()*width), (int)(Math.random()*height));
		c.max_velocity=(float) (0.1+Math.random()*2);
		c.max_rotation=0.1+Math.random();
		boids.add(c);
		neighbourList.put(c, new ArrayList<>());
		breadcrumbsList.add(new ArrayList<Vector2D>());
	}


	public void draw(){

		background(200);
		//fill(200,10);
		//rect(0,0,width,height);

		line(0,height/2,width,height/2);
		line(width/2,0,width/2,height);

		if(Timeline.getInstance().rightTime()){
			update(1);

		}

		for(int i=0;i<boids.size();i++){
			for(Vector2D v:breadcrumbsList.get(i)){
				rectMode(CENTER);
				rect(v.x,v.y,1,1);
			}
		}
	}


	public void mousePressed(){
		c = getNewCharacter(mouseX,mouseY);
		c.max_velocity=(float) (0.1+Math.random()*2);
		c.max_rotation=0.1+Math.random();
		boids.add(c);
		breadcrumbsList.add(new ArrayList<Vector2D>());
		neighbourList.put(c, new ArrayList<>());
	}

	public static void main(String argv[]){
		PApplet.main("Flocking_Behavior");
	}

	/*************************************************Update********************************************************/
	public void update(int time_elapsed){

		if(boids.size()>1)
			for(int i=0;i<boids.size();i++){

				boids.get(i).acceleration = flock(boids.get(i));
				orientToVelocity(boids.get(i), getCentralVector(boids, true));
				characterUpdate(boids.get(i),1);
				breadcrumbsList.get(i).add(new Vector2D((int)boids.get(i).position.x,(int)boids.get(i).position.y));
			}
		else{
			wander(boids.get(0));
			characterUpdate(boids.get(0),1);
			breadcrumbsList.get(0).add(new Vector2D((int)boids.get(0).position.x,(int)boids.get(0).position.y));
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

	/*************************************************AI Algos********************************************************/

	public PVector flock(Character c){
		PVector final_acceleration=new PVector(0,0);

		PVector separation_acceleration = Separation(c);
		PVector cohesion_acceleration = Cohesion(c);
		PVector velocity_matching = Velocity_Match(c);

		separation_acceleration = separation_acceleration.mult((float) sW);
		cohesion_acceleration = cohesion_acceleration.mult((float) cW);
		velocity_matching = cohesion_acceleration.mult((float) vW);

		final_acceleration.x = separation_acceleration.x + cohesion_acceleration.x + velocity_matching.x;
		final_acceleration.y = separation_acceleration.y + cohesion_acceleration.y + velocity_matching.y;

		return final_acceleration;
	}

	public PVector Velocity_Match(Character c){

		PVector temp_acceleration = getCentralVector(boids, true);

		temp_acceleration = temp_acceleration.sub(c.velocity);

		temp_acceleration = temp_acceleration.div((float) c.time_to_target);
		
		if(temp_acceleration.mag()>c.max_acceleration){
			temp_acceleration.normalize();
			temp_acceleration = temp_acceleration.mult(c.max_acceleration);
		}

		return temp_acceleration;
	}
	public PVector Cohesion(Character c){//flee from center of mass		

		return arrive(c,getCentralVector(boids, false));

	}
	public PVector Separation(Character c){//flee from center of mass		

		//build neighbour list
		return evade(c,getCentralVector(boids, false));

	}

	public PVector wander(Character c){

		PVector temp_velocity = getVectorFromOrientation(c.orientation).mult(c.max_velocity);

		c.rotation = randomBinomial()*c.max_rotation;

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

		return temp_velocity;
	}

	public PVector seek(Character c, PVector target_position){

		PVector temp_velocity = target_position.sub(c.position);

		temp_velocity = temp_velocity.normalize();

		temp_velocity = temp_velocity.mult(c.max_velocity);


		if(Math.signum(temp_velocity.mag())!=0){

			orientation = temp_velocity.heading()+(float)Math.PI/2;

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

		//boundary conditions
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

		return temp_velocity;
	}

	public PVector flee(Character c, PVector target_position){

		PVector temp_velocity = target_position.sub(c.position);

		temp_velocity = temp_velocity.normalize();

		temp_velocity = temp_velocity.mult(-c.max_velocity);


		if(Math.signum(c.velocity.mag())!=0){

			orientation = c.velocity.heading()+(float)Math.PI/2;

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


		//boundary conditions
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

		return temp_velocity;
	}

	//accelerate or decelerate depending on closeness to target
	public PVector arrive(Character c, PVector target_position){

		PVector temp_acceleration = new PVector(0,0);

		c.time_to_target=0.1;

		PVector target_velocity = target_position.sub(c.position);
		float distance = target_velocity.mag();
		float target_speed;

		if(distance<c.radius_of_satisfaction){
			temp_acceleration.mult(0);
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

			temp_acceleration = target_velocity.sub(c.velocity);
			temp_acceleration.div((float)c.time_to_target);

			if(temp_acceleration.mag()>c.max_acceleration){
				temp_acceleration.normalize();
				temp_acceleration.mult(c.max_acceleration);
			}

		}

		return temp_acceleration;

	}

	//accelerate or decelerate depending on closeness to target
	public PVector evade(Character c, PVector target_position){
		
		PVector temp_acceleration = new PVector(0,0);
		
		c.time_to_target=0.1;

		PVector target_velocity = target_position.sub(c.position);
		target_velocity.mult(-1);
		float distance = target_velocity.mag();
		float target_speed;

		if(distance<c.radius_of_satisfaction){
			//c.velocity.mult(0);
			temp_acceleration.mult(0);

		}
		else{
			if(distance>c.radius_of_deceleration){
				target_speed = c.max_velocity;

			}
			else{
				System.out.println("inside rod");
				target_speed = c.max_velocity*distance/c.radius_of_deceleration;
			}
			target_velocity.normalize();
			target_velocity.mult(target_speed);

			temp_acceleration = target_velocity.sub(c.velocity);
			temp_acceleration.div((float)c.time_to_target);

			if(temp_acceleration.mag()>c.max_acceleration){
				temp_acceleration.normalize();
				temp_acceleration.mult(c.max_acceleration);
			}

		}

		return temp_acceleration;
	}

	public void orientToVelocity(Character c, PVector target_position){

		if(c.velocity.mag()!=0){

			if(c.velocity.heading()>0)
				orientation = target_position.sub(c.position).heading()+(float)Math.PI/2;
			else 
				orientation = target_position.sub(c.position).heading()-(float)Math.PI/2;
			
			
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

	/********************************************Helper functions********************************************************/	

	public PVector getCentralVector(ArrayList<Character> cList,boolean type){

		//type false - position, type true - velocity

		PVector centralVector = new PVector(0,0);

		for(Character c:cList){
			if(!type)
				centralVector=centralVector.add(c.position);
			else
				centralVector=centralVector.add(c.velocity);
		}

		centralVector=centralVector.div(cList.size());

		return centralVector;
	}

	public float distance_between(PVector v1, PVector v2){
		return v1.dist(v2);
	}

	public float randomBinomial(){
		return (float) ((Math.random()) - (Math.random()));
	}

	public PVector getVectorFromOrientation(float orientation){

		PVector tempVector = new PVector(0,0);

		tempVector.y = (float) Math.cos(orientation+Math.PI);
		tempVector.x = (float) Math.sin(orientation);
		return tempVector;
	}

	public Character getNewCharacter(int x,int y){		//factory pattern
		pointer = createShape(GROUP);
		PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		PShape body = createShape(ARC, 0,0, 36, 36, 0, PI);
		body.setStroke(false);

		if(boids.size()%2==0)
		{
			head.setFill(color(255,0,0));
			body.setFill(color(255,0,0));
		}else{
			head.setFill(color(0,0,255));
			body.setFill(color(0,0,255));
		}


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

		return new Character(pointer,x,y); 
	}



}
