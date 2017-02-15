
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
	float max_velocity=2;
	int collision_radius=0;
	int circle_x=50,circle_y=750;

	double max_rotation = 0.1;

	public Character(PShape _pointer){
		this.pointer = _pointer;
		this.position=new PVector(50,750);
		orientation=0;
		this.velocity=new PVector(0, 0);
		rotation=0;
		this.acceleration=new PVector(0, 0);
		angular_acceleration=(float)0.1;
	}
}
