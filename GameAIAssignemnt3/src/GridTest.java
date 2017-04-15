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
	
	String current_room = "";
	PVector target = new PVector(0,0);
	boolean wander = false;
}

class ActionNode implements TreeNode {

	TreeNode leftChild;
	TreeNode rightChild;
	float x,y;
		
	public ActionNode(float _x,float _y) {
		leftChild = null;
		rightChild = null;
		x=_x;
		y=_y;
	}

	@Override
	public void evaluate(Character person) {
		person.target.x = x;
		person.target.y = y;
	}

}

class ConditionNode implements TreeNode {

	TreeNode leftChild;
	TreeNode rightChild;
	String room = "";//1 - A, 2 - B, 3 - C, 4 - D, 5 - E
		
	public ConditionNode(TreeNode _lchild, TreeNode _rchild, String _room) {
		leftChild = _lchild;
		rightChild = _rchild;
		room = _room;
	}

	@Override
	public void evaluate(Character person) {
		
		if(person.current_room.equalsIgnoreCase(room)){
			leftChild.evaluate(person);
		}
		else{
			rightChild.evaluate(person);
		}
		
	}

}

class DecisionTree{
	
	ConditionNode root;
	Character person;
	Graph graph;
	ArrayList<ConditionNode> ConditionList = new ArrayList<>();
	ArrayList<ActionNode> ActionList = new ArrayList<>();
	
	DecisionTree(Character _person,Graph _graph){
		
		person = _person;
		graph = _graph;
		
		ActionNode goToRoomA = new ActionNode(40,120);
		ActionNode goToRoomB = new ActionNode(40,760);
		ActionNode goToRoomC = new ActionNode(760,760);
		ActionNode goToRoomD = new ActionNode(760,280);
		ActionNode goToRoomE = new ActionNode(760,120);
		
		ConditionNode isRoomD = new ConditionNode(goToRoomB,goToRoomA,"4");
		ConditionNode isRoomC = new ConditionNode(goToRoomD,isRoomD,"3");
		ConditionNode isRoomB = new ConditionNode(goToRoomE,isRoomC,"2");
		root = new ConditionNode(goToRoomC,isRoomB,"1");
	}
	
	public void evaluate(){
		root.evaluate(person);
	}
	
}


class Selector implements BehaviorTreeTask{

	ArrayList<BehaviorTreeTask> children = new ArrayList<>();
	
	
	@Override
	public boolean run() {
		for(BehaviorTreeTask c:children){
			if(c.run()){
				return true;
			}
		}
		return false;
	}
	
}

class Sequence implements BehaviorTreeTask{

	ArrayList<BehaviorTreeTask> children = new ArrayList<>();
	
	
	@Override
	public boolean run() {
		for(BehaviorTreeTask c:children){
			if(!c.run()){
				return false;
			}
		}
		return true;
	}
	
}


class RandomSelector implements BehaviorTreeTask{

	ArrayList<BehaviorTreeTask> children = new ArrayList<>();
	Character m;
	Random r;
	
	RandomSelector(Character _m){
		this.m =_m;
		r = new Random();
	}
	
	
	@Override
	public boolean run() {
		
		if(!m.wander){
		switch(r.nextInt(5)){
		case 0:children.get(0).run();
		break;
		case 1:children.get(1).run();
		break;
		case 2:children.get(2).run();
		break;
		case 3:children.get(3).run();
		break;
		case 4:children.get(4).run();
		break;
		}
		}
		return true;
	}
	
}

class Decorator implements BehaviorTreeTask{

	BehaviorTreeTask child;
	boolean result;
	
	@Override
	public boolean run() {
		
		while(true){
			result = child.run();
			if(!result)
				break;
		}
		
		return true;
	}
	
}

class GoToRoomA implements BehaviorTreeTask{

	Character m;
	
	GoToRoomA(Character _m){
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		m.target.x=40;m.target.y=120;
		m.wander = true;
		return true;
	}
	
}

class GoToRoomB implements BehaviorTreeTask{

	Character m;
	
	GoToRoomB(Character _m){
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		m.target.x=40;m.target.y=760;
		m.wander = true;
		return true;
	}
	
}

class GoToRoomC implements BehaviorTreeTask{

	Character m;
	
	GoToRoomC(Character _m){
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		m.target.x=760;m.target.y=760;
		m.wander = true;
		return true;
	}
	
}

class GoToRoomD implements BehaviorTreeTask{

	Character m;
	
	GoToRoomD(Character _m){
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		m.target.x=760;m.target.y=280;
		m.wander = true;
		return true;
	}
	
}

class GoToRoomE implements BehaviorTreeTask{

	Character m;
	
	GoToRoomE(Character _m){
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		m.target.x=760;m.target.y=120;
		m.wander = true;
		return true;
	}
	
}

class SmellTask implements BehaviorTreeTask{

	Character c;
	Character m;
	
	SmellTask(Character _c, Character _m){
		this.c = _c;
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		
		if(calculateDistance(c.position,m.position)<=40)
			return true;
		
		return false;
	}
	
	double calculateDistance(PVector p1, PVector p2){
		return Math.pow(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2),0.5);
	}
}

class NotEatenTask implements BehaviorTreeTask{

	Character c;
	Character m;
	
	NotEatenTask(Character _c, Character _m){
		this.c = _c;
		this.m = _m;
	}
	
	@Override
	public boolean run() {
		
		if(calculateDistance(c.position,m.position)<=2)
			return false;
		
		return true;
	}
	
	double calculateDistance(PVector p1, PVector p2){
		return Math.pow(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2),0.5);
	}
}

class FollowTask implements BehaviorTreeTask{

	Character c;
	Character m;
	
	FollowTask(Character _c, Character _m){
		this.c = _c;
		this.m = _m;
	}
	@Override
	public boolean run() {
		
		m.target.x = c.position.x;
		m.target.y = c.position.y;		
		return true;
	}
	
}


class BehaviorTree{
	Character c,m;
	BehaviorTreeTask root;
	
	BehaviorTree(Character _c, Character _m){
		this.c= _c;
		this.m = _m;
		
		NotEatenTask notEatenTask = new NotEatenTask(c, m);
		FollowTask followTask = new FollowTask(c, m);
		SmellTask smellTask = new SmellTask(c, m);
		GoToRoomA a = new GoToRoomA(m);
		GoToRoomB b = new GoToRoomB(m);
		GoToRoomC c = new GoToRoomC(m);
		GoToRoomD d = new GoToRoomD(m);
		GoToRoomE e = new GoToRoomE(m);
		
		Sequence sequence1 = new Sequence();
		sequence1.children.add(notEatenTask);
		sequence1.children.add(followTask);
		
		Decorator decorator = new Decorator();
		decorator.child = sequence1;
		
		Sequence sequence2 = new Sequence();
		sequence2.children.add(smellTask);
		sequence2.children.add(decorator);
		
		RandomSelector randomSelector = new RandomSelector(m);
		randomSelector.children.add(a);
		randomSelector.children.add(b);
		randomSelector.children.add(c);
		randomSelector.children.add(d);
		randomSelector.children.add(e);
		
		Selector selector = new Selector();
		selector.children.add(sequence2);
		selector.children.add(randomSelector);
		
		root = selector;
	}
	
	public void run(){
		root.run();
	}
	
}

public class GridTest extends PApplet {

	float target_x = 40, target_y = 120;
	String s_direction = "up";
	float goalRotation = 0, orientation = 0;
	int direction = 0;
	ArrayList<Vector2D> breadcrumbs = new ArrayList<>();

	float gridMatrix[][];

	Character person,monster;
	boolean personDefined = false, first_run = true, next_pos_flag = false,test=true,testMonster=true;

	int red, green, blue;

	FileReader finput;
	BufferedReader bufferedReader;

	int grid_width = 80;
	String currentLine;
	String matrixArray[];

	Graph graph;
	Astar astar;
	DecisionTree decisionTree;
	BehaviorTree behaviorTree;
	
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

		person = getNewBoid(40, 760,255);
		person.current_room = "2";
		personDefined = true;
		
		decisionTree = new DecisionTree(person,graph);
		
		decisionTree.evaluate();
		
		monster = getNewBoid(760, 760,0);
		
		behaviorTree = new BehaviorTree(person, monster);
		behaviorTree.run();
		
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
				v.vertexType = splitLine[3];
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
				updateMonster(monster,1);
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

		
		astar.executeAlgo();
		target_x = mouseX;
		target_y = mouseY;
		personDefined = true;

	}

	public static void main(String args[]) {
		PApplet.main("GridTest");
	}

	public Character getNewBoid(int x, int y,int color) {

		Character c = new Character(x, y);

		PShape pointer = createShape(GROUP);
		PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(color, 0, 0));
		head.setStroke(false);
		PShape body = createShape(ARC, 0, 0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(color, 0, 0));

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

		if (personDefined){
			
			if(test){
				decisionTree.evaluate();
				Vertex start = graph.quantizeOnGraph(person.position.x, person.position.y);
				Vertex goal = graph.quantizeOnGraph(person.target.x, person.target.y);
				astar = new Astar(graph, start.label, goal.label);
				astar.executeAlgo();
				System.out.println(astar.finalPath.size());
				test = false;
			}
			seek(c, pathFollow(person.target.x, person.target.y,astar));
		}
			
		count++;

		if (count == 10) {
			breadcrumbs.add(new Vector2D((int) c.position.x, (int) c.position.y));
			count = 0;
		}
	}
	
	public void updateMonster(Character c, int time_elapsed) {

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

		if (personDefined){
			
			if(testMonster){
				behaviorTree.run();
				Vertex start = graph.quantizeOnGraph(monster.position.x, monster.position.y);
				Vertex goal = graph.quantizeOnGraph(monster.target.x, monster.target.y);
				astar = new Astar(graph, start.label, goal.label);
				astar.executeAlgo();
				System.out.println(astar.finalPath.size());
				testMonster = false;
			}
			seekMonster(c, pathFollowMonster(monster.target.x, monster.target.y,astar));
		}
			
		count++;

		if (count == 10) {
			breadcrumbs.add(new Vector2D((int) c.position.x, (int) c.position.y));
			count = 0;
		}
	}

	public void seek(Character c, PVector target_position) {

		if (target_position.x == 999 && target_position.y == 999) {
			test=true;
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
	
	public void seekMonster(Character c, PVector target_position) {

		if (target_position.x == 999 && target_position.y == 999) {
			testMonster=true;
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

	public PVector pathFollow(float target_x, float target_y, Astar astar) {

		if (!personDefined) {
			return new PVector(person.position.x, person.position.y);
		} else {
			goal = graph.quantizeOnGraph(target_x, target_y);
		
			current_position = astar.quantizeOnPath(person.position.x, person.position.y);
			
			person.current_room = current_position.vertexType;
			
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
	
	public PVector pathFollowMonster(float target_x, float target_y, Astar astar) {

		if (!personDefined) {
			return new PVector(monster.position.x, monster.position.y);
		} else {
			goal = graph.quantizeOnGraph(target_x, target_y);
		
			current_position = astar.quantizeOnPath(monster.position.x, monster.position.y);
			
			monster.current_room = current_position.vertexType;
			
			if (graph.calculateDistance(goal.lat, goal.lon, current_position.lat, current_position.lon) == 0) {

				if (graph.calculateDistance(current_position.lat, current_position.lon, monster.position.x,
						monster.position.y) < 2) {
					return new PVector(999, 999);
				}

				return new PVector(current_position.lat.floatValue(), current_position.lon.floatValue());
			}

			if (first_run) {
				int index = astar.getIndexOfVertexInPath(current_position);

				next_position = astar.finalPath.get(index - 1);
				first_run = false;
			}

			if (astar.calculateDistance(monster.position.x, monster.position.y, next_position.lat,
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