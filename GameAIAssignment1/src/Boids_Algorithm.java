import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

class Boid{
	PVector location;
	PVector velocity;
	PVector acceleration;
	float width =(float) 12.0;
	float height = (float) 8.0;
	
	float max_velocity = (float)3.0;
	float max_acceleration = (float)1.0;
	
	float perceptionDistance = 30, perceptionAngle = (float) Math.PI, perceptionMinDistance = 5;
	
	float alignmentWeight=(float)1.0,separationWeight=(float)0.5,cohesionWeight=(float)0.7;
	
	Boid(int x,int y){
		location = new PVector((float)Math.random()*x,(float)Math.random()*y);
		velocity = new PVector((float)(-2.5+Math.random()*2.5),(float)(-2.5+Math.random()*2.5));
		acceleration = new PVector();
	}
	
	
	public ArrayList<Boid> getNeighbours(ArrayList<Boid> bList){
		ArrayList<Boid> neighbours = new ArrayList<>();
		
		PVector locationDifference = new PVector();
		
		for(int i=0;i<bList.size();i++){
			
			if(bList.get(i).equals(this)) continue;
			
			locationDifference.set(bList.get(i).location);
			locationDifference.sub(location);
			
			if(locationDifference.mag()>perceptionDistance) continue;
			
			if(PVector.angleBetween(velocity, locationDifference)>perceptionAngle) continue;
			
			neighbours.add(bList.get(i));
			
		}
		
		return neighbours;
	}
	
	
	public void update(ArrayList<Boid> neighbours){
		
		PVector locationDifference = new PVector();
		PVector velocityDifference = new PVector();
		
		PVector alignment = new PVector();
		PVector cohesion = new PVector();
		PVector separation = new PVector();
		
		
		for(int i=0;i<neighbours.size();i++){
			Boid b = (Boid)neighbours.get(i);
			
			locationDifference.set(b.location);
			locationDifference.sub(location);
			
			velocityDifference.set(b.velocity);
			velocityDifference.sub(velocity);
			
			alignment.add(velocityDifference);
			cohesion.add(locationDifference);
			
			if(locationDifference.mag()<perceptionMinDistance){
				separation.add(locationDifference);
			}
		}
		
		if(alignment.mag()>0) alignment.normalize();
		if(cohesion.mag()>0) cohesion.normalize();
		if(separation.mag()>0) separation.normalize();
		
		alignment.mult(alignmentWeight);
		cohesion.mult(cohesionWeight);
		separation.mult(separationWeight);
		
		acceleration.set(alignment);
		acceleration.set(cohesion);
		acceleration.sub(separation);		
		
		if(acceleration.mag()>max_acceleration){
			acceleration.normalize();
			acceleration.mult(max_acceleration);
		}
		
		velocity.add(acceleration);
		
		if(velocity.mag()>max_velocity){
			velocity.normalize();
			velocity.mult(max_velocity);
		}
		
		location.add(velocity);
	}
}

public class Boids_Algorithm extends PApplet{

	ArrayList<Boid> boids = new ArrayList<>();
	
	public void settings(){

		size(800,800);
		
	}
	
	public void setup(){
		for(int i=0;i<30;i++){
			Boid b = new Boid(width,height);
			boids.add(b);
		}
	}
	
	void render(Boid b){
		pushMatrix();
		translate(b.location.x,b.location.y);
		rotate(-(float)atan2(-b.velocity.y,b.velocity.x));
		triangle(-18, 0, 0, -30, 18, 0);
		popMatrix();
	}
	
	public void draw(){
		background(200);
		for(int i=0;i<boids.size();i++){
			boids.get(i).update(boids.get(i).getNeighbours(boids));
			if(boids.get(i).location.y>height+5){
				boids.get(i).location.y=0;
			}
			else if(boids.get(i).location.y<-2){
				boids.get(i).location.y = height;
			}

			if(boids.get(i).location.x>width+5){
				boids.get(i).location.x=0;
			}
			else if(boids.get(i).location.x<-2){
				boids.get(i).location.x=width;
			}		
			render(boids.get(i));
		}
	}
	
	
	public static void main(String args[]){
		PApplet.main("Boids_Algorithm");
	}
}
