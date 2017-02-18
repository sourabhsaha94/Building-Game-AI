import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

class BoidChar{

	PShape pointer;

	PVector position;
	float orientation;
	PVector velocity;
	double rotation;
	PVector acceleration;
	float angular_acceleration=0;
	float max_velocity=1;
	float max_acceleration=(float)1;

	int radius_of_satisfaction=5,radius_of_deceleration=100;
	double time_to_target=0.25;

	double max_rotation = 0.1;
	double max_angular_acceleration = 0.1;

	public BoidChar(int x,int y){
		this.position=new PVector(x,y);
		this.velocity=new PVector((float)0.1,(float)0.1);
		orientation=0;
		rotation=0;
		this.acceleration=new PVector(0,0);
	}
}

public class Flocking_Behavior extends PApplet{

	ArrayList<BoidChar> boidChars = new ArrayList<>();
	float x,y;

	public void settings(){
		size(800,800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup(){

		background(200);

		for(int i=0;i<15;i++){
			BoidChar c = new BoidChar((int)random(width),(int)random(height));
			boidChars.add(c);
			System.out.println(boidChars.get(i).position);
		}
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
	}


	public void mousePressed(){
		BoidChar c = new BoidChar(mouseX,mouseY);
		boidChars.add(c);
	}

	public static void main(String argv[]){
		PApplet.main("Flocking_Behavior");
	}
	public void update(int time_elapsed){

		for(int i=0;i<boidChars.size();i++){
			boidChars.get(i).acceleration = flock(boidChars.get(i));
			characterUpdate(boidChars.get(i),time_elapsed);
		}

	}

	public void characterUpdate(BoidChar c,int time_elapsed){
		//update position
		c.position.add(c.velocity.mult(time_elapsed));

		//update orientation
		c.orientation += c.rotation*time_elapsed;

		//update accelerations
		c.velocity.add(c.acceleration.mult(time_elapsed));
		c.rotation += c.angular_acceleration*time_elapsed;
		
		if(c.velocity.mag()>c.max_velocity){
			c.velocity.normalize();
			c.velocity.mult(c.max_velocity);
		}

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

		
		pushMatrix();
		translate(c.position.x,c.position.y);
		rotate(c.orientation);
		rectMode(CENTER);
		rect(0,0,30,30);
		popMatrix();

	}

	public PVector flock(BoidChar c){

		PVector final_velocity = new PVector();

		PVector cohesion = new PVector();
		PVector separation = new PVector();
		PVector alignment = new PVector();

		cohesion= Cohesion(c);
		separation= Separation(c);
		alignment = Alignment(c);

		final_velocity.set(cohesion);
		final_velocity.add(alignment);
		final_velocity.sub(separation);

		if(final_velocity.mag()>c.max_acceleration){
			final_velocity.normalize();
			final_velocity.mult(c.max_acceleration);
		}

		return final_velocity;
	}

	public PVector Cohesion(BoidChar c){
		PVector cohesion = new PVector();
		cohesion.set(getCOMPosition(boidChars));
		cohesion.sub(c.position);
		return cohesion.normalize().mult((float) 0.7);
	}

	public PVector Separation(BoidChar c){
		PVector separation = new PVector();
		separation.set(getCOMPosition(boidChars));

		if((separation.sub(c.position)).mag() > 20)
			return separation.mult(0);

		separation.sub(c.position);

		return separation.normalize().mult((float) 0.2);
	}

	public PVector Alignment(BoidChar c){
		PVector alignment = new PVector(0,0);

		alignment.set(getCOMVelocity(boidChars));
		
		return alignment.normalize();

	}

	public PVector getCOMPosition(ArrayList<BoidChar> cList){

		PVector com = new PVector(0,0);

		for(int i=0;i<boidChars.size();i++){
			com.add(boidChars.get(i).position);
		}

		com.div(boidChars.size());

		return com;
	}

	public PVector getCOMVelocity(ArrayList<BoidChar> cList){

		PVector com = new PVector(0,0);

		for(int i=0;i<boidChars.size();i++){
			com.add(boidChars.get(i).velocity);
		}

		com.div(boidChars.size());
		
		return com;
	}
	
	public PVector checkboundary(PVector position){
		
		PVector wind = new PVector(0,0);
		
		if(position.y>height){
			return wind.add(0,-10);
		}
		else if(position.y<0){
			return wind.add(0,10);
		}

		if(position.x>width){
			return  wind.add(-2,0);
		}
		else if(position.x<0){
			return  wind.add(2,0);
		}
		
		return wind.add(0,0);
	}
}
