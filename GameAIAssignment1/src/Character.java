
import processing.core.PShape;
import processing.core.PVector;

public class Character{

	PShape pointer;

	PVector position;
	float orientation;
	PVector velocity;
	double rotation;
	PVector acceleration;
	float angular_acceleration;
	float max_velocity=1;

	double max_rotation = 0.01;

	public Character(PShape _pointer){
		this.pointer = _pointer;
		this.position=new PVector(0,0);
		orientation=0;
		this.velocity=new PVector(0, 0);
		rotation=0;
		this.acceleration=new PVector(0, 0);
		angular_acceleration=0;
	}
}
