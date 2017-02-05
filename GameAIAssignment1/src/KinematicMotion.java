import processing.core.PApplet;
import processing.core.PShape;

public class KinematicMotion extends PApplet{
	
	PShape pointer, head, body;
	int count=0;
	boolean turn = false;
	public void settings(){
		size(800,800);
	}
	
	public void setup(){
		 pointer = createShape(GROUP);
		 background(200);
		  // Make two shapes
		  head = createShape(TRIANGLE, -28, 0, 0, -55, 28, 0);
		  head.setFill(color(255,0,0));
		  head.setStroke(false);
		  body = createShape(ARC, 0, 0, 56, 56, 0, PI);
		  body.setStroke(false);
		  body.setFill(color(255,0,0));
		  

		  // Add the two "child" shapes to the parent group
		  pointer.addChild(body);
		  pointer.addChild(head);
	}
	
	public void draw(){
		background(200);
		if(!turn){
			count++;
			if(count==800){
				turn = true;
			}
		}
		else{
			count--;
			if(count==0){
				turn = false;
			}
		}
		translate(count,count);
		shape(pointer);
	}
	public static void main(String argv[]){
		PApplet.main("KinematicMotion");
	}
}
