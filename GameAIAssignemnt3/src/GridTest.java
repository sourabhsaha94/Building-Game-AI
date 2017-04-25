import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	boolean eaten = false;

	public String printCurrentRoom(){

		if(calculateDistance(position,new PVector(132,250))<260){
			return "RoomA";
		}
		else if(calculateDistance(position,new PVector(155,645))<260){
			return "RoomB";
		}
		else if(calculateDistance(position,new PVector(599,685))<200){
			return "RoomC";
		}
		else if(calculateDistance(position,new PVector(693,361))<180){
			return "RoomD";
		}
		else if(calculateDistance(position,new PVector(657,34))<180){
			return "RoomE";
		}
		else{
			return "corridor";
		}
	}
	double calculateDistance(PVector p1, PVector p2){
		return Math.pow(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2),0.5);
	}
}
/**********************************************Decision tree player*******************************************************/
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

/**********************************************Decision tree monster*******************************************************/

class ConditionNodeMonster implements TreeNode{
	
	String name="";
	TreeNode lchild,rchild;
	Character monster;
	
	
	ConditionNodeMonster(String _name,TreeNode _leftchild, TreeNode _rightchild, Character _monster){
		name = _name;
		lchild = _leftchild;
		rchild = _rightchild;
		monster = _monster;
	}

	@Override
	public void evaluate(Character person) {
		
		if(name.equalsIgnoreCase("distance")){
			
			if(calculateDistance(monster.position,person.position)<=2){
				monster.eaten = true;
				monster.wander = false;
				return;
			}
			else if(calculateDistance(monster.position,person.position)<=100){
				monster.wander = false;
				monster.current_room = "near";
			}
			else if(calculateDistance(monster.position,person.position)<=200){
				monster.current_room = "near";
			}
			else{
				monster.current_room = "far";
			}
			
			
			if(monster.current_room.equalsIgnoreCase("near")){
				lchild.evaluate(person);
			}else{
				rchild.evaluate(person);
			}
			
		}else{
			
			if(isSameRoom(person,monster)){
				lchild.evaluate(person);
			}
			else{
				rchild.evaluate(person);
			}
			
		}
		
	}
	
	double calculateDistance(PVector p1, PVector p2){
		return Math.pow(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2),0.5);
	}
	
	boolean isSameRoom(Character person, Character monster){
		return (person.printCurrentRoom().equalsIgnoreCase(monster.printCurrentRoom()))?true:false;
	}
}

class WanderActionNode implements TreeNode{

	Character monster;
	Random r;
	
	public WanderActionNode(Character _monster) {
		r = new Random();
		monster = _monster;
	}
	
	@Override
	public void evaluate(Character person) {
		
		if(!monster.wander){
			int i = r.nextInt(5);
			switch(i){
			case 0:monster.target.x=40;monster.target.y=120;monster.wander=true;
			break;
			case 1:monster.target.x=40;monster.target.y=760;monster.wander=true;
			break;
			case 2:monster.target.x=760;monster.target.y=760;monster.wander=true;
			break;
			case 3:monster.target.x=760;monster.target.y=280;monster.wander=true;
			break;
			case 4:monster.target.x=760;monster.target.y=120;monster.wander=true;
			break;
			}
		}
		
	}
	
}

class FollowActionNode implements TreeNode{

	Character monster;
	
	public FollowActionNode(Character _monster) {
		monster = _monster;
	}
	
	@Override
	public void evaluate(Character person) {
		monster.target.x = person.position.x;
		monster.target.y = person.position.y;
	}
	
}

class DecisionTreeMonster{
	ConditionNodeMonster root;
	Character monster;
	Graph graph;
	
	DecisionTreeMonster (Character _monster, Graph _graph){
		monster = _monster;
		graph = _graph;
		
		FollowActionNode actionNode = new FollowActionNode(monster);
		WanderActionNode actionNode2 = new WanderActionNode(monster);
		
		ConditionNodeMonster sameRoom = new ConditionNodeMonster("sameRoom",actionNode2,actionNode2,monster);
		
		root = new ConditionNodeMonster("distance", actionNode, sameRoom,monster);
		
	}
	
	void evaluate(Character person){
		root.evaluate(person);
	}
}

/**********************************************Behavior tree player*******************************************************/
class Selector implements BehaviorTreeTask{

	ArrayList<BehaviorTreeTask> children = new ArrayList<>();


	@Override
	public boolean run() {
		for(int i=0;i<children.size();i++){
			if(children.get(i).run()){
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
		for(int i=0;i<children.size();i++){
			if(!children.get(i).run()){
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
			int i = r.nextInt(5);
			
			switch(i){
			case 0:children.get(0).run();
			//System.out.println("Room A");
			break;
			case 1:children.get(1).run();
			//System.out.println("Room B");
			break;
			case 2:children.get(2).run();
			//System.out.println("Room C");
			break;
			case 3:children.get(3).run();
			//System.out.println("Room D");
			break;
			case 4:children.get(4).run();
			//System.out.println("Room E");
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

		//System.out.println(calculateDistance(c.position,m.position));
		if(calculateDistance(c.position,m.position)<=200){
			m.current_room = "near";
		}
		
		if(calculateDistance(c.position,m.position)<=100){

			m.current_room = "near";
			m.wander=false;
			return true;

		}else{
			m.current_room="far";
		}

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

		if(calculateDistance(c.position,m.position)<=2){
			m.wander=false;
			m.eaten = true;
			return false;

		}

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

		//Decorator decorator = new Decorator();
		//decorator.child = sequence1;

		Sequence sequence2 = new Sequence();
		sequence2.children.add(smellTask);
		//sequence2.children.add(decorator);
		sequence2.children.add(sequence1);

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

/**********************************************Implementation of Algorithm*****************************************************/

public class GridTest extends PApplet {

	float target_x = 40, target_y = 120;
	String s_direction = "up";
	String distance = "";

	boolean behaviourMode = false ;//false for decision tree
	
	FileWriter fwriter;
	BufferedWriter bufferedWriter;

	ArrayList<Vector2D> breadcrumbs = new ArrayList<>();

	float gridMatrix[][];

	Character person,monster;
	boolean personDefined = false, first_run = true,first_run_m=true, next_pos_flag = false,test=true,testMonster=true;

	int red, green, blue;

	FileReader finput;
	BufferedReader bufferedReader;

	int grid_width = 80;
	String currentLine;
	String matrixArray[];

	Graph graph;

	DecisionTree decisionTree;
	DecisionTreeMonster decisionTreeMonster;
	BehaviorTree behaviorTree;

	ArrayList<Vertex> vList = new ArrayList<Vertex>();
	ArrayList<Vertex> tempVList = new ArrayList<Vertex>();



	/*character navigation variables*/
	int direction = 0;
	float goalRotation = 0, orientation = 0;
	Vertex current_position, next_position = null, goal;
	Astar astar;


	/*monster navigation variables*/
	int direction_m = 0;
	float goalRotation_m = 0, orientation_m = 0;
	Vertex current_position_m, next_position_m = null, goal_m;
	Astar astarMonster;


	int i = 0, count = 0,count_m=0;

	PImage background;

	public void settings() {
		size(800, 800);
	}

	public void setup() {
		
		background = loadImage("layout.jpg");

		background(background);

		gridMatrix = new float[Math.floorDiv(height, grid_width)][Math.floorDiv(width, grid_width)];

		try {
			this.fwriter = new FileWriter("dataset.txt");
			this.bufferedWriter = new BufferedWriter(fwriter);
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

		monster = getNewBoid(760, 760,0);//40,120 40,760 760,760 760,280 760,120

		if(behaviourMode){
			behaviorTree = new BehaviorTree(person, monster);
			behaviorTree.run();
		}
		else{
			decisionTreeMonster = new DecisionTreeMonster(monster, graph);
			decisionTreeMonster.evaluate(person);
		}
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

		//System.out.println("Creating Graph..");

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

		if (person != null && !monster.eaten) {
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

		

		if(monster.eaten){
			//System.out.println("Game over");
			textSize(50);
			fill(255, 0, 0);
			text("PLAYER HAS BEEN EATEN", width/2 - 300, height/2); 

		}
		else{
			
			System.out.println(isSameRoom(person, monster)+","+monster.current_room+","+monster.wander);
			
			try {
				bufferedWriter.append(isSameRoom(person,monster)+","+monster.current_room+","+monster.wander+"\n");
				bufferedWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//displayGrid();

	}
	
	public String isSameRoom(Character c1, Character c2){
		return (c1.printCurrentRoom().equalsIgnoreCase(c2.printCurrentRoom()))?"true":"false";
	}
	
	public void mousePressed() {

		///System.out.println(mouseX+" "+mouseY);
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

			//if(testMonster){
			if(behaviourMode)
				behaviorTree.run();
			else
				decisionTreeMonster.evaluate(person);
			
			//System.out.println(monster.target);
			Vertex start = graph.quantizeOnGraph(monster.position.x, monster.position.y);
			Vertex goal = graph.quantizeOnGraph(monster.target.x, monster.target.y);
			astarMonster = new Astar(graph, start.label, goal.label);
			astarMonster.executeAlgo();
			//System.out.println(astarMonster.finalPath.size());
			testMonster = false;
			//}
			//System.out.println(monster.target);
			seekMonster(c, pathFollowMonster(monster.target.x, monster.target.y,astarMonster));
		}

		count_m++;

		if (count_m == 10) {
			breadcrumbs.add(new Vector2D((int) c.position.x, (int) c.position.y));
			count_m = 0;
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

		//System.out.println(target_position);

		if (target_position.x == 999 && target_position.y == 999) {
			testMonster=true;
			monster.wander=false;
			c.max_velocity = 0;

		} else {
			if(monster.wander)
				c.max_velocity = (float)1;
			else
				c.max_velocity= (float)2;
		}

		c.velocity = target_position.sub(c.position);

		c.velocity = c.velocity.normalize();

		c.velocity = c.velocity.mult(c.max_velocity);

		if (Math.signum(c.velocity.mag()) != 0) {

			orientation_m = c.velocity.heading() + (float) Math.PI / 2;

			goalRotation_m = orientation_m - c.orientation;

			if (goalRotation_m < 0) {
				direction_m = -1;
			} else {
				direction_m = 1;
			}

			if (goalRotation_m < c.max_rotation) {
				c.rotation = goalRotation_m;
			} else {

				c.rotation = direction_m * c.max_rotation;
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
			goal_m = graph.quantizeOnGraph(target_x, target_y);

			current_position_m = astar.quantizeOnPath(monster.position.x, monster.position.y);

			//monster.current_room = current_position_m.vertexType;

			if (graph.calculateDistance(goal_m.lat, goal_m.lon, current_position_m.lat, current_position_m.lon) == 0) {

				if (graph.calculateDistance(current_position_m.lat, current_position_m.lon, monster.position.x,
						monster.position.y) < 2) {
					return new PVector(999, 999);
				}

				return new PVector(current_position_m.lat.floatValue(), current_position_m.lon.floatValue());
			}

			if (first_run_m) {
				int index = astar.getIndexOfVertexInPath(current_position_m);

				next_position_m = astar.finalPath.get(index - 1);
				first_run_m = false;
			}

			if (astar.calculateDistance(monster.position.x, monster.position.y, next_position_m.lat,
					next_position_m.lon) > 2) {
				// do nothing keep moving to center of tile
			} else {
				int index = astar.getIndexOfVertexInPath(current_position_m);
				next_position_m = astar.finalPath.get(index - 1);

			}

			return new PVector(next_position_m.lat.floatValue(), next_position_m.lon.floatValue());
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