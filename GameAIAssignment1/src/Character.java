
import processing.core.PShape;
import processing.core.PVector;

public class Character{

	PShape pointer;

	PVector position;
	float orientation;
	PVector velocity;
	double rotation;
	PVector acceleration;
	float angular_acceleration=0;
	float max_velocity=3;
	float max_acceleration=(float)1;
	
	int radius_of_satisfaction=10,radius_of_deceleration=100;
	double time_to_target=0.25;

	double max_rotation = 0.1;
	double max_angular_acceleration = 0.1;
	
	public Character(PShape _pointer,int x,int y){
		this.pointer = _pointer;
		this.position=new PVector(x,y);
		this.velocity=new PVector(0,0);
		orientation=0;
		rotation=0;
		this.acceleration=new PVector(0,0);
	}
}
