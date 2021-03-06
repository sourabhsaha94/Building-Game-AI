import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Basic_motion extends PApplet {

	Character c;
	GameAI game_ai;
	PShape pointer, head, body;
	int count = 0;
	float target_x = -width / 2, target_y = -height / 2;
	String s_direction = "up";
	float goalRotation = 0, orientation = 0;
	int direction = 0;
	ArrayList<Vector2D> breadcrumbs = new ArrayList<>();

	boolean turn = false, first_run = true;

	public void settings() {
		size(800, 800);
		Thread t = new Thread(Timeline.getInstance());
		t.start();
	}

	public void setup() {
		background(200);

		pointer = createShape(GROUP);
		head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255, 0, 0));
		head.setStroke(false);
		body = createShape(ARC, 0, 0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255, 0, 0));

		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

		c = new Character(pointer, 50, 750);

	}

	public void update(Character c, int time_elapsed) {

		if (s_direction == "up") {
			target_x = 50;
			target_y = 50;
		} else if (s_direction == "right") {
			target_x = 750;
			target_y = 50;
		} else if (s_direction == "down") {
			target_x = 750;
			target_y = 750;
		} else {
			target_x = 50;
			target_y = 750;
		}

		if (s_direction == "up" && (c.position.y <= 52 && c.position.y >= 48)) {
			s_direction = "right";
		} else if (s_direction == "right" && (c.position.x <= 752 && c.position.x >= 748)) {
			s_direction = "down";
		} else if (s_direction == "down" && (c.position.y <= 752 && c.position.y >= 748)) {
			s_direction = "left";
		} else if (s_direction == "left" && (c.position.x <= 52 && c.position.x >= 48)) {
			s_direction = "up";
		}

		// update position
		c.position.add(c.velocity.mult(time_elapsed));

		// update orientation
		c.orientation += c.rotation * time_elapsed;

		// update accelerations
		c.velocity.add(c.acceleration.mult(time_elapsed));
		c.rotation += c.angular_acceleration * time_elapsed;

		pushMatrix();
		translate(c.position.x, c.position.y);
		rotate(c.orientation);
		scale((float) 0.5);
		shape(c.pointer);
		popMatrix();

		seek(c, new PVector(target_x, target_y));

		count++;

		if (count == 4) {
			breadcrumbs.add(new Vector2D((int) c.position.x, (int) c.position.y));
			count = 0;
		}
	}

	public void draw() {

		background(200);
		// fill(200,10);
		// rect(0,0,width,height);

		line(0, height / 2, width, height / 2);
		line(width / 2, 0, width / 2, height);

		if (Timeline.getInstance().rightTime()) {
			update(c, 1);
			for (Vector2D v : breadcrumbs) {
				rectMode(CENTER);
				rect(v.x, v.y, 1, 1);
			}
		}

	}

	public static void main(String argv[]) {
		PApplet.main("Basic_motion");
	}

	public void seek(Character c, PVector target_position) {

		c.velocity = target_position.sub(c.position);

		c.velocity = c.velocity.normalize();

		c.velocity = c.velocity.mult(c.max_velocity);

		if (Math.signum(c.velocity.mag()) != 0) {

			orientation = c.velocity.heading() + (float) Math.PI / 2;// getting
																		// orientation
																		// from
																		// velocity

			goalRotation = orientation - c.orientation;

			if (goalRotation < 0) {
				direction = -1;
			} else {
				direction = 1;
			}

			if (goalRotation < c.max_rotation) {
				c.rotation = goalRotation;
			} else {

				c.rotation = direction * c.max_rotation;
			}

		} else {
			c.rotation = 0;
		}

	}

	public PVector getVectorFromOrientation(float orientation) {	//get velocity from orientation

		PVector tempVector = new PVector(0, 0);

		tempVector.y = (float) Math.cos(orientation + Math.PI);
		tempVector.x = (float) Math.sin(orientation);
		return tempVector;
	}

}
