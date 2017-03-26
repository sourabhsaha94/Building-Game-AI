import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.*;

class Character {

	PShape pointer;

	PVector position;
	float orientation;
	PVector velocity;
	double rotation;
	PVector acceleration;
	float angular_acceleration = 0;
	float max_velocity = (float) 1.5;
	float max_acceleration = (float) 0.5;

	int radius_of_satisfaction = 5, radius_of_deceleration = 100;
	double time_to_target = 0.25;

	double max_rotation = 0.1;
	double max_angular_acceleration = 0.1;

	public Character(int x, int y) {
		this.position = new PVector(x, y);
		this.velocity = new PVector(0, 0);
		orientation = 0;
		rotation = 0;
		this.acceleration = new PVector(0, 0);
	}
}

public class GridTest extends PApplet {

	float target_x = 40, target_y = 120;
	String s_direction = "up";
	float goalRotation = 0, orientation = 0;
	int direction = 0;
	ArrayList<Vector2D> breadcrumbs = new ArrayList<>();

	float gridMatrix[][];

	Character person;
	boolean personDefined = false, first_run = true, next_pos_flag = false;

	int red, green, blue;

	FileReader finput;
	BufferedReader bufferedReader;

	int grid_width = 80;
	String currentLine;
	String matrixArray[];

	Graph graph;
	Astar astar;

	ArrayList<Vertex> vList = new ArrayList<Vertex>();
	ArrayList<Vertex> tempVList = new ArrayList<Vertex>();

	Vertex current_position, next_position = null, goal;

	int i = 0, count = 0;

	PImage background;

	public void settings() {
		size(800, 800);
	}

	public void setup() {

		background = loadImage("layout.jpg");

		background(background);

		gridMatrix = new float[Math.floorDiv(height, grid_width)][Math.floorDiv(width, grid_width)];

		try {
			this.finput = new FileReader("gridMatrix.txt");
			this.bufferedReader = new BufferedReader(finput);
			int j = 0;

			while ((currentLine = this.bufferedReader.readLine()) != null) {
				matrixArray = currentLine.split(" ");
				for (int i = 0; i < matrixArray.length; i++) {
					gridMatrix[i][j] = Float.parseFloat(matrixArray[i]);
				}
				j++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread timeline = new Thread(Timeline.getInstance());
		timeline.start();

		createGraph();

		person = getNewBoid(40, 760);

	}

	public void createGraph() {

		ArrayList<Vertex> vList = new ArrayList<Vertex>();

		FileReader finput, finput2;
		BufferedReader bufferedReader, bufferedReader2;

		String line, splitLine[];

		try {
			finput = new FileReader("RoomTileGraphVertexList.txt");
			bufferedReader = new BufferedReader(finput);

			while ((line = bufferedReader.readLine()) != null) {
				splitLine = line.split(" ");
				Vertex v = new Vertex(splitLine[0]);
				v.lat = Double.valueOf(splitLine[1]);
				v.lon = Double.valueOf(splitLine[2]);
				vList.add(v);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		graph = new Graph(vList);

		System.out.println("Creating Graph..");

		try {
			finput2 = new FileReader("RoomTileGraphEdgeList.txt");
			bufferedReader2 = new BufferedReader(finput2);

			while ((line = bufferedReader2.readLine()) != null) {
				splitLine = line.split(",");
				graph.addEdge(splitLine[0], splitLine[1], Integer.parseInt(splitLine[2]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void draw() {

		background(background);

		noStroke();

		for (int i = 0; i < Math.floorDiv(height, grid_width); i++) {
			for (int j = 0; j < Math.floorDiv(height, grid_width); j++) {

				if (gridMatrix[i][j] == 0) {
					red = 255;
					green = 255;
					blue = 255;
				} else if (gridMatrix[i][j] == 1) {// wall red
					red = 255;
					green = 0;
					blue = 0;
				} else if (gridMatrix[i][j] == 2) {// obstacle green
					red = 0;
					green = 255;
					blue = 0;
				}
				fill(red, green, blue, 0);
				rect(i * grid_width, j * grid_width, grid_width, grid_width);
			}
		}

		if (person != null) {
			if (Timeline.getInstance().rightTime()) {
				update(person, 1);
				for (Vector2D v : breadcrumbs) {
					rectMode(CENTER);
					fill(0, 0, 0, 200);
					rect(v.x, v.y, 2, 2);
					rectMode(CORNER);
				}
			}
		}
		
		//displayGrid();

	}

	public void mousePressed() {

		Vertex start = graph.quantizeOnGraph(person.position.x, person.position.y);
		Vertex goal = graph.quantizeOnGraph(mouseX, mouseY);
		astar = new Astar(graph, start.label, goal.label);
		astar.executeAlgo();
		target_x = mouseX;
		target_y = mouseY;
		personDefined = true;

	}

	public static void main(String args[]) {
		PApplet.main("GridTest");
	}

	public Character getNewBoid(int x, int y) {

		Character c = new Character(x, y);

		PShape pointer = createShape(GROUP);
		PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255, 0, 0));
		head.setStroke(false);
		PShape body = createShape(ARC, 0, 0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255, 0, 0));

		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

		c.pointer = pointer;
		return c;
	}

	public void update(Character c, int time_elapsed) {

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

		if (personDefined)
			seek(c, pathFollow(target_x, target_y));

		count++;

		if (count == 10) {
			breadcrumbs.add(new Vector2D((int) c.position.x, (int) c.position.y));
			count = 0;
		}
	}

	public void seek(Character c, PVector target_position) {

		if (target_position.x == 999 && target_position.y == 999) {

			c.max_velocity = 0;

		} else {
			c.max_velocity = (float)1.5;
		}

		c.velocity = target_position.sub(c.position);

		c.velocity = c.velocity.normalize();

		c.velocity = c.velocity.mult(c.max_velocity);

		if (Math.signum(c.velocity.mag()) != 0) {

			orientation = c.velocity.heading() + (float) Math.PI / 2;

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

	public PVector getVectorFromOrientation(float orientation) { // get velocity
		// from
		// orientation

		PVector tempVector = new PVector(0, 0);

		tempVector.y = (float) Math.cos(orientation + Math.PI);
		tempVector.x = (float) Math.sin(orientation);
		return tempVector;
	}

	public PVector pathFollow(float target_x, float target_y) {

		if (!personDefined) {
			return new PVector(person.position.x, person.position.y);
		} else {
			goal = graph.quantizeOnGraph(target_x, target_y);
			current_position = astar.quantizeOnPath(person.position.x, person.position.y);

			if (graph.calculateDistance(goal.lat, goal.lon, current_position.lat, current_position.lon) == 0) {

				if (graph.calculateDistance(current_position.lat, current_position.lon, person.position.x,
						person.position.y) < 2) {
					return new PVector(999, 999);
				}

				return new PVector(current_position.lat.floatValue(), current_position.lon.floatValue());
			}

			if (first_run) {
				int index = astar.getIndexOfVertexInPath(current_position);

				next_position = astar.finalPath.get(index - 1);
				first_run = false;
			}

			if (astar.calculateDistance(person.position.x, person.position.y, next_position.lat,
					next_position.lon) > 2) {
				// do nothing keep moving to center of tile
			} else {
				int index = astar.getIndexOfVertexInPath(current_position);
				next_position = astar.finalPath.get(index - 1);

			}

			return new PVector(next_position.lat.floatValue(), next_position.lon.floatValue());
		}
	}
	
	public void displayGrid(){
		for(Vertex v:graph.Vlist){
			ellipseMode(CENTER);
			fill(100);
			ellipse(v.lat.floatValue(),v.lon.floatValue(),5,5);
		}
	}

}